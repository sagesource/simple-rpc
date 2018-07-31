package org.sagesource.simplerpc.core.protocol;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.basic.exception.SimpleRpcException;
import org.sagesource.simplerpc.core.zookeeper.ServiceAddressProviderAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * <p>TProtocol对象池工厂 服务调用方在初始化调用客户端前创建TProtocol连接对象</p>
 * <pre>
 *     author      XueQi
 *     date        2018/6/29
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class TProtocolPooledFactory extends BasePooledObjectFactory<TProtocol> {
	private static Logger LOGGER = LoggerFactory.getLogger(TProtocolPooledFactory.class);

	private static final int MAX_LENGTH = 1638400000; // 单次最大传输的数据量（1.6G）

	private String serviceName;
	private String version;
	// 超时时间
	private int     timeout   = 3000000;
	// 保持长连接
	private boolean keepAlive = true;
	// 服务地址获取代理
	private ServiceAddressProviderAgent serviceAddressProviderAgent;

	public TProtocolPooledFactory buildServiceAddressProviderAgent(ServiceAddressProviderAgent serviceAddressProviderAgent) {
		this.serviceAddressProviderAgent = serviceAddressProviderAgent;
		this.serviceName = serviceAddressProviderAgent.getServiceName();
		this.version = serviceAddressProviderAgent.getVersion();
		return this;
	}

	/**
	 * 构建超时时间
	 *
	 * @param timeout
	 * @return
	 */
	public TProtocolPooledFactory buildTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * 构建保持长连接
	 *
	 * @param keepAlive
	 * @return
	 */
	public TProtocolPooledFactory buildKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
		return this;
	}

	/**
	 * 创建对象
	 *
	 * @return
	 * @throws Exception
	 */
	@Override
	public TProtocol create() throws Exception {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Create TProtocol Pool Bean. ServiceName=" + this.serviceName + " Version=" + this.version);

		// 通过 LoadBalance 获取服务连接
		ServerInfo serverInfo = this.serviceAddressProviderAgent.findServiceServerInfo();
		if (serverInfo == null) {
			throw new SimpleRpcException(MessageFormat.format("SERVICE:[{0}]:VERSION:[{1}] ADDRESS NOT FOUND.",
					this.serviceName, this.version));
		}

		// 创建 Thrift 连接配置
		TSocket    tSocket    = new TSocket(serverInfo.getServerIP(), serverInfo.getPort(), this.timeout);
		TTransport tTransport = new TFramedTransport(tSocket, MAX_LENGTH);
		if (tTransport != null && !tTransport.isOpen()) {
			tTransport.open();
		}
		TProtocol protocol = new TEnhanceTransProtocol(new TCompactProtocol(tTransport), serverInfo);
		return protocol;
	}

	/**
	 * 将对象放入到对象池中，包装对象，让对象池方便管理
	 *
	 * @param tProtocol
	 * @return
	 */
	@Override
	public PooledObject<TProtocol> wrap(TProtocol tProtocol) {
		return new DefaultPooledObject<>(tProtocol);
	}

	/**
	 * 对象钝化(从激活状态 -> 非激活状态，returnObject时触发)
	 *
	 * @param p
	 * @throws Exception
	 */
	@Override
	public void passivateObject(PooledObject<TProtocol> p) throws Exception {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Passivate TProtocol Pool Bean. ServiceName=" + this.serviceName + " Version=" + this.version + " KeepAlive=" + this.keepAlive);

		if (!this.keepAlive && p.getObject() != null) {
			p.getObject().getTransport().flush();
			p.getObject().getTransport().close();
		}
	}

	/**
	 * 对象激活(borrowObject时触发）
	 *
	 * @param p
	 * @throws Exception
	 */
	@Override
	public void activateObject(PooledObject<TProtocol> p) throws Exception {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Activate TProtocol Pool Bean. ServiceName=" + this.serviceName + " Version=" + this.version);

		if (p.getObject() != null && !p.getObject().getTransport().isOpen()) {
			p.getObject().getTransport().open();
		}
	}

	/**
	 * 对象销毁（clear时会触发）
	 *
	 * @param p
	 * @throws Exception
	 */
	@Override
	public void destroyObject(PooledObject<TProtocol> p) throws Exception {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Destroy TProtocol Pool Bean. ServiceName=" + this.serviceName + " Version=" + this.version + " Object Status=" + p.getState());

		if (!PooledObjectState.INVALID.equals(p.getState()) && p.getObject() != null && p.getObject().getTransport().isOpen()) {
			p.getObject().getTransport().flush();
			p.getObject().getTransport().close();
		}
		p.markAbandoned();
	}
}
