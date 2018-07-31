package org.sagesource.simplerpc.core.loadbalance;

import org.sagesource.simplerpc.basic.entity.ServerInfo;

import java.util.List;

/**
 * <p> 轮询处理 </p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface RoundRobinEngine {

	/**
	 * 轮询获取一个可用的服务信息
	 *
	 * @param serviceName
	 * @param version
	 * @param waitSelectorServerList
	 * @return
	 */
	ServerInfo getAvailableServerInfo(String serviceName, String version, List<ServerInfo> waitSelectorServerList);

}
