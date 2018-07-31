package org.sagesource.simplerpc.provider.thread;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.core.protocol.TEnhanceTransProtocol;
import org.sagesource.simplerpc.core.zookeeper.ServiceRegisterProviderAgent;
import org.sagesource.simplerpc.core.zookeeper.ZookeeperClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>服务提供方线程</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/12
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ProviderServerThread extends Thread {
	private static Logger LOGGER = LoggerFactory.getLogger(ProviderServerThread.class);

	private List<ServerInfo> serverInfoList;
	private TServer          server;
	private volatile boolean isStart = false;

	public ProviderServerThread(List<ServerInfo> serverInfoList, TProcessor processor) throws Exception {
		this.serverInfoList = serverInfoList;

		TNonblockingServerSocket     serverTransport = new TNonblockingServerSocket(serverInfoList.get(0).getPort());
		TThreadedSelectorServer.Args tArgs           = new TThreadedSelectorServer.Args(serverTransport);
		tArgs.processorFactory(new TProcessorFactory(processor));
		tArgs.transportFactory(new TFramedTransport.Factory());
		tArgs.protocolFactory(new TEnhanceTransProtocol.Factory(new TCompactProtocol.Factory()));
		this.server = new TThreadedSelectorServer(tArgs);
	}


	@Override
	public void run() {
		try {
			for (ServerInfo serverInfo : serverInfoList) {
				// 为当前线程赋予一个可辨识的 name 在出现问题的时候 便于排查
				Thread.currentThread().setName("provider-thread-" + serverInfo.getServiceName() + "-" + serverInfo.getServiceVersion());
				LOGGER.info("init service provider thread ...");

				ServiceRegisterProviderAgent serviceRegisterProviderAgent = new ServiceRegisterProviderAgent();
				serviceRegisterProviderAgent.register(serverInfo);
			}
			// 在线程中启动服务，并注册服务信息
			isStart = true;
			this.server.serve();
		} catch (Exception e) {
			//FIXME 启动失败 关闭zk连接 更新启动状态为失败
			ZookeeperClientFactory.close();
			LOGGER.error("init service provider thread error ...", e);
		}
	}

	public void close() {
		if (this.server != null)
			this.server.stop();
	}
}
