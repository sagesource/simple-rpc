package org.sagesource.simplerpc.core.zookeeper;

import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.basic.exception.SimpleRpcException;
import org.sagesource.simplerpc.config.SystemConfigClientManager;
import org.sagesource.simplerpc.core.loadbalance.LoadBalanceFactory;
import org.sagesource.simplerpc.core.zookeeper.utils.ZKConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * <p>服务地址Agent</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/6
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ServiceAddressProviderAgent implements ZKConstants {
	private static Logger LOGGER = LoggerFactory.getLogger(ServiceAddressProviderAgent.class);

	// 初始化计数器 当ServiceAddressProviderAgent构造的时候，需要等到childEvent执行完成后，在执行后面的初始化操作
	private CountDownLatch countDownLatch = new CountDownLatch(1);

	/**
	 * 服务名称
	 */
	private String serviceName;

	/**
	 * 服务版本号
	 */
	private String version;

	/**
	 * 服务提供方信息
	 */
	private final List<ServerInfo> serverInfoList = new CopyOnWriteArrayList<>();

	/**
	 * 将调用的服务列表缓存，当serverInfoList为空的时候，可以尝试从缓存列表中获取机器信息
	 */
	private final Set<ServerInfo> traceCacheServer = new HashSet<>();

	private PathChildrenCache cachedPath;

	private CuratorFramework zkClient;

	private static final List<PathChildrenCache> PATH_CHILDREN_CACHES = new CopyOnWriteArrayList<>();

	public ServiceAddressProviderAgent(String serviceName, String version) throws Exception {
		this.serviceName = serviceName;
		this.version = version;
		init();
	}

	/**
	 * 初始化
	 */
	public void init() throws Exception {
		// 获取 zkClient, 通过 env 获取 zk 的连接字符串
		String zkConnStr = SystemConfigClientManager.getSystemConfigClient().zkConnStrConfig();
		this.zkClient = ZookeeperClientFactory.createClient(zkConnStr);

		// 构建服务节点
		String servicePath = builtServicePath();
		buildPathChildrenCache(zkClient, servicePath);
		// 需要等待第一次childEvent完成，初始化才可以结束
		countDownLatch.await();
		// 将监听的节点缓存，便于 close 操作
		PATH_CHILDREN_CACHES.add(this.cachedPath);
	}

	/**
	 * 构建子节点的监听事件
	 *
	 * @param client
	 * @param servicePath
	 */
	private void buildPathChildrenCache(final CuratorFramework client, String servicePath) throws Exception {
		this.cachedPath = new PathChildrenCache(client, servicePath, true);
		try {
			this.cachedPath.start();
		} catch (Exception e) {
			if (!"already started".equals(e.getMessage())) {
				throw e;
			}
		}
		this.cachedPath.getListenable().addListener(new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
				// 节点事件
				PathChildrenCacheEvent.Type eventType = pathChildrenCacheEvent.getType();
				switch (eventType) {
					case CONNECTION_RECONNECTED:
						LOGGER.info(">>>>>>>> connection is reconnection <<<<<<<<");
						break;
					case CONNECTION_SUSPENDED:
						LOGGER.info(">>>>>>>> connection is supended <<<<<<<<");
						break;
					case CONNECTION_LOST:
						LOGGER.warn(">>>>>>>> connection lost, waiting.... <<<<<<<<");
						return;
					case INITIALIZED:
						LOGGER.warn(">>>>>>>> connection init.... <<<<<<<<");
					default:
				}

				// 当任何节点的事件变动，简单处理，rebuild并清空 serverInfoList
				cachedPath.rebuild();
				rebuild();
				countDownLatch.countDown();
			}

			// rebuild
			protected void rebuild() {
				List<ChildData> childDataList = cachedPath.getCurrentData();
				if (childDataList == null || childDataList.isEmpty()) {
					// service 节点下没有机器数据，可能是服务端和 zk 的连接断开而已
					return;
				}

				// 获取新的机器列表
				List<ServerInfo> currentServerInfoList = new ArrayList<>();
				for (ChildData childData : childDataList) {
					// 获取节点值
					String     nodePathVal = new String(childData.getData());
					ServerInfo serverInfo  = JSON.parseObject(nodePathVal, ServerInfo.class);
					currentServerInfoList.add(serverInfo);
				}
				// 将最新的机器列表 添加到节点列表
				serverInfoList.clear();
				serverInfoList.addAll(currentServerInfoList);
			}
		});
	}

	/**
	 * 获取服务的机器列表
	 *
	 * @return
	 */
	public ServerInfo findServiceServerInfo() {
		ServerInfo serverInfo = null;
		if ((this.serverInfoList == null || this.serverInfoList.isEmpty()) && !traceCacheServer.isEmpty()) {
			List<ServerInfo> traceCacheList = new ArrayList<>(this.traceCacheServer);
			serverInfo = LoadBalanceFactory.getLoadBalanceEngine().availableServerInfo(this.serviceName, this.version, traceCacheList);
		} else if (!serverInfoList.isEmpty()) {
			serverInfo = LoadBalanceFactory.getLoadBalanceEngine().availableServerInfo(this.serviceName, this.version, this.serverInfoList);
			if (serverInfo != null)
				traceCacheServer.add(serverInfo);
		} else {
			throw new SimpleRpcException(MessageFormat.format("SERVICE:[{0}] VERSION:[{0}] Unavailable", this.serviceName, this.version));
		}

		if (LOGGER.isDebugEnabled())
			LOGGER.debug(">>>>>>>> serviceName:{},version:{},serverInfo:{} <<<<<<<<", this.serviceName, this.version, JSON.toJSONString(serverInfo));
		return serverInfo;
	}

	/**
	 * 获取服务的 ZK 节点信息
	 *
	 * @return
	 */
	private String builtServicePath() {
		return "/" + this.serviceName + "/" + this.version;
	}

	/**
	 * 关闭
	 */
	public static synchronized void close() {
		for (PathChildrenCache pathChildrenCach : PATH_CHILDREN_CACHES) {
			try {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("close zk pathChildrenCach:{}", pathChildrenCach);

				pathChildrenCach.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getVersion() {
		return version;
	}
}
