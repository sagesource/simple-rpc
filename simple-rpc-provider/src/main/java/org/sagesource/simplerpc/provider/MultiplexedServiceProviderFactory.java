package org.sagesource.simplerpc.provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.sagesource.simplerpc.basic.Constants;
import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.basic.utils.InetSocketAddressUtils;
import org.sagesource.simplerpc.config.SystemConfigClientManager;
import org.sagesource.simplerpc.provider.dto.ServiceRegisterDto;
import org.sagesource.simplerpc.provider.thread.ProviderServerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p>多路复用服务注册工厂</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/16
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MultiplexedServiceProviderFactory implements ServiceProviderFactory, Constants {
	private static Logger LOGGER = LoggerFactory.getLogger(MultiplexedServiceProviderFactory.class);

	/**
	 * 服务端口
	 */
	private int port;

	private List<ServiceRegisterDto> serviceRegisterList;

	private static final CopyOnWriteArrayList<ProviderServerThread> providerThreadList = new CopyOnWriteArrayList<>();

	public MultiplexedServiceProviderFactory(int port, List<ServiceRegisterDto> serviceRegisterList) {
		this.port = port;
		this.serviceRegisterList = serviceRegisterList;
	}

	/**
	 * 创建服务提供方
	 *
	 * @throws Exception
	 */
	@Override
	public void createServiceProvider() throws Exception {
		String appName = SystemConfigClientManager.getSystemConfigClient().appName();
		String localIp = InetSocketAddressUtils.getLocalIP();

		if (this.serviceRegisterList != null && this.serviceRegisterList.size() > 0) {

			// 多路复用 Processor
			TMultiplexedProcessor tMultiplexedProcessor = new TMultiplexedProcessor();
			// 线程类加载器
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			// Service 配置列表
			List<ServerInfo> serverInfoList = new ArrayList<>(this.serviceRegisterList.size());

			for (ServiceRegisterDto serviceRegisterDto : this.serviceRegisterList) {
				Object serviceImpl = serviceRegisterDto.getServiceImpl();

				// 校验实现类属性是否存在
				if (serviceImpl == null) {
					throw new IllegalArgumentException("create multiplexed service error. implement is null");
				}

				// 获取实现类的接口信息
				Class<? extends Object> serviceImplClass = serviceImpl.getClass();
				Class<?>[]              interfaces       = serviceImplClass.getInterfaces();
				if (interfaces == null || interfaces.length == 0) {
					throw new IllegalClassFormatException("create multiplexed service error. service implement interface is null");
				}

				String serviceName = null;
				// 创建 Processor
				TProcessor processor = null;
				for (Class<?> anInterface : interfaces) {
					String interfaceName = anInterface.getSimpleName();

					if (!StringUtils.contains(interfaceName, IFACE)) continue;
					// 获取服务名称
					serviceName = anInterface.getEnclosingClass().getName();
					// 获取 Processor
					String processorName = serviceName + _$PROCESSOR;

					Constructor<?> constructor = classLoader.loadClass(processorName).getConstructor(anInterface);
					processor = (TProcessor) constructor.newInstance(serviceImpl);

					// 注册 Processor
					tMultiplexedProcessor.registerProcessor(serviceName, processor);
					break;
				}

				// 判断 Processor 是否已经创建
				if (processor == null) {
					throw new IllegalClassFormatException("create multiplexed service error, TProcessor is null");
				}

				ServerInfo serverInfo = new ServerInfo();
				serverInfo.setWeight(serviceRegisterDto.getWeight());
				serverInfo.setAppName(appName);
				serverInfo.setServiceName(serviceName);
				serverInfo.setServiceVersion(serviceRegisterDto.getServiceVersion());
				serverInfo.setServerIP(localIp);
				serverInfo.setPort(this.port);
				serverInfoList.add(serverInfo);
			}

			// 启动并注册服务
			ProviderServerThread providerServerThread = new ProviderServerThread(serverInfoList, tMultiplexedProcessor);
			providerServerThread.start();
			providerThreadList.add(providerServerThread);
		} else {
			LOGGER.warn("create multiplexed service list is empty");
		}
	}

	/**
	 * 关闭服务
	 */
	public static void close() {
		for (ProviderServerThread providerServerThread : providerThreadList) {
			providerServerThread.close();
		}
	}
}
