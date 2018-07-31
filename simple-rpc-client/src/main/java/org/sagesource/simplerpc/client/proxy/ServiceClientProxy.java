package org.sagesource.simplerpc.client.proxy;

import org.sagesource.simplerpc.basic.entity.ProtocolPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> 服务客户端代理</p>
 * <pre>
 *     author      XueQi
 *     date        2018/6/29
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ServiceClientProxy {
	private static Logger LOGGER = LoggerFactory.getLogger(ServiceClientProxy.class);

	/**
	 * 同步客户端缓存对象 key:serviceName:version
	 */
	private static final ConcurrentHashMap<String, Object> cacheClientMapper  = new ConcurrentHashMap<>();
	// 服务父类接口名称
	private static final String                            SERVICE_IFACE_NAME = "$Iface";

	/**
	 * 创建 Service Client
	 *
	 * @param serviceClass       服务类型
	 * @param version            版本号
	 * @param protocolPoolConfig 连接池配置
	 * @param <T>
	 * @return
	 */
	public static <T> T createClient(Class<T> serviceClass, String version, ProtocolPoolConfig protocolPoolConfig) throws Exception {
		// 服务名称
		String serviceName = serviceClass.getEnclosingClass().getName();
		// 从缓存中获取客户端
		String clientCacheKey = serviceName + ":" + version;
		Object cacheClient    = cacheClientMapper.get(clientCacheKey);
		if (cacheClient != null) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("get serviceName:{} version:{} proxy client form cache", serviceName, version);

			return (T) cacheClient;
		}

		// 基于 JDK 动态代理获取 Client
		ServiceClientProxyInvocationHandler proxyInvocationHandler = new ServiceClientProxyInvocationHandler(serviceName, version, protocolPoolConfig);

		T client = (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, proxyInvocationHandler);
		if (client != null) {
			// 写入客户端缓存
			cacheClientMapper.putIfAbsent(clientCacheKey, client);
		}
		return client;
	}

}
