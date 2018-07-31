package org.sagesource.simplerpc.core.zookeeper;

import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.config.SystemConfigClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>服务注册 Agent</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/12
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ServiceRegisterProviderAgent {
	private static Logger LOGGER = LoggerFactory.getLogger(ServiceRegisterProviderAgent.class);

	/**
	 * ZK 客户端
	 */
	private static CuratorFramework zkClient;

	static {
		// 获取 zkClient, 通过 env 获取 zk 的连接字符串
		String zkConnStr = SystemConfigClientManager.getSystemConfigClient().zkConnStrConfig();
		zkClient = ZookeeperClientFactory.createClient(zkConnStr);
	}

	/**
	 * 注册服务节点
	 *
	 * @param serverInfo
	 */
	public void register(ServerInfo serverInfo) throws Exception {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug(">>>>>>>> Register Service ServerInfo={}", serverInfo);

		// 配置节点的 data
		String node    = builtServicePath(serverInfo);
		String nodeVal = JSON.toJSONString(serverInfo);

		try {
			zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(node, nodeVal.getBytes());
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取服务的 ZK 节点信息
	 *
	 * @return
	 */
	private String builtServicePath(ServerInfo serverInfo) {
		String address = serverInfo.getServerIP() + ":" + serverInfo.getPort();
		return "/" + serverInfo.getServiceName() + "/" + serverInfo.getServiceVersion() + "/" + address;
	}
}
