package org.sagesource.simplerpc.core.loadbalance;

import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.basic.exception.SimpleRpcException;

import java.util.List;

/**
 * <p>LoadBalanceEngine</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface LoadBalanceEngine {

	/**
	 * 获取服务可用的服务列表
	 *
	 * @param serviceName
	 * @param version
	 * @param serverListInfo
	 * @return
	 * @throws SimpleRpcException
	 */
	ServerInfo availableServerInfo(String serviceName, String version, List<ServerInfo> serverListInfo) throws SimpleRpcException;

}
