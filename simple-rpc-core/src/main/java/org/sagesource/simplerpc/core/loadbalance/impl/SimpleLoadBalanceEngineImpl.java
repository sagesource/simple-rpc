package org.sagesource.simplerpc.core.loadbalance.impl;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.basic.exception.SimpleRpcException;
import org.sagesource.simplerpc.config.SystemConfigClientManager;
import org.sagesource.simplerpc.core.loadbalance.LoadBalanceEngine;
import org.sagesource.simplerpc.core.loadbalance.RoundRobinFactory;
import org.sagesource.simplerpc.core.zookeeper.utils.ZKConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>简单负载均衡引擎实现</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class SimpleLoadBalanceEngineImpl implements LoadBalanceEngine, ZKConstants {
	private static Logger LOGGER = LoggerFactory.getLogger(SimpleLoadBalanceEngineImpl.class);

	// 缓存静态路由表
	private static final ConcurrentHashMap<String, ServerInfo> cacheStaticRouterMapper = new ConcurrentHashMap<>();

	@Override
	public ServerInfo availableServerInfo(String serviceName, String version, List<ServerInfo> serverListInfo) throws SimpleRpcException {
		// 1. 检查静态路由配置，如果存在静态路由，直接解析返回
		LOGGER.debug("Simple Loadbalance Try Find Server Info From Static Router Config. ServiceName=" + serviceName + " Version=" + version);

		ServerInfo staticServerInfo = queryStaticRouterConfig(serviceName, version);
		if (staticServerInfo != null) return staticServerInfo;

		// 2. 调用 ZK 注册中心，获取服务地址列表，基于加权随机法获取一个地址
		ServerInfo serverInfo = RoundRobinFactory.getRoundRobinEngine().getAvailableServerInfo(serviceName, version, serverListInfo);

		return serverInfo;
	}

	//.................//

	/**
	 * 查询静态路由配置
	 * 读取顺序：JVM 参数 - 环境变量<br/>
	 * JVM 配置: -Dsimplerpc.static.router=serviceName.version=ip:port,...<br/>
	 * ENV 配置: SIMPLERPC_STATIC_ROUTER=serviceName.version=ip:port,....<br/>
	 * 全局静态路由 *=ip:port<br/>
	 *
	 * @param serviceName
	 * @param version
	 * @return
	 */
	private ServerInfo queryStaticRouterConfig(String serviceName, String version) {
		String staticRouterConfig = SystemConfigClientManager.getSystemConfigClient().staticRouterConfig();

		// 存在静态路由配置
		String staticRouterStr = null;
		String cacheKey        = serviceName + "." + version;
		if (!StringUtils.isEmpty(staticRouterConfig)) {
			// 获取缓存配置
			ServerInfo cacheServerInfo = cacheStaticRouterMapper.get(cacheKey);
			if (cacheServerInfo != null) return cacheServerInfo;

			// 解析静态路由配置字符串
			Map<String, String> staticRouterMapper = Splitter.on(",").withKeyValueSeparator("=").split(staticRouterConfig);
			// 获取 *.version 全局静态路由配置
			staticRouterStr = staticRouterMapper.get("*." + version);
			if (StringUtils.isEmpty(staticRouterStr)) {
				// 获取 * 全局静态路由配置
				staticRouterStr = staticRouterMapper.get("*");
				// 获取 serviceName.version 静态路由配置
				if (StringUtils.isEmpty(staticRouterStr)) {
					staticRouterStr = staticRouterMapper.get(serviceName + "." + version);
					// 获取 serviceName 静态路由配置
					if (StringUtils.isEmpty(staticRouterStr)) {
						staticRouterStr = staticRouterMapper.get(serviceName);
					}
				}
			}
		}

		// 处理静态路由字符串配置
		if (!StringUtils.isEmpty(staticRouterStr)) {
			List<String> staticRouterList = Splitter.on(":").trimResults().splitToList(staticRouterStr);
			ServerInfo   serverInfo       = new ServerInfo();
			serverInfo.setServiceName(serviceName);
			serverInfo.setServiceVersion(version);
			serverInfo.setServerIP(staticRouterList.get(0));
			serverInfo.setPort(Integer.parseInt(staticRouterList.get(1)));
			cacheStaticRouterMapper.putIfAbsent(cacheKey, serverInfo);
			return serverInfo;
		}

		return null;
	}
}
