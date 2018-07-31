package org.sagesource.simplerpc.core.loadbalance.impl;

import org.sagesource.simplerpc.core.loadbalance.RoundRobinEngine;
import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>简单轮询实现</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class SimpleRoundRobinEngineImpl implements RoundRobinEngine {
	private static Logger LOGGER = LoggerFactory.getLogger(SimpleRoundRobinEngineImpl.class);

	// 原子 Integer 获取位置索引
	private static AtomicInteger atomicInteger = new AtomicInteger(0);

	// 锁对象
	private static Object LOCK_OBJECT = new Object();

	@Override
	public ServerInfo getAvailableServerInfo(String serviceName, String version, List<ServerInfo> waitSelectorServerList) {
		// 只有一台服务的情况下 直接返回
		if (waitSelectorServerList.size() == 1) return waitSelectorServerList.get(0);

		// 遍历符合权重的服务列表
		List<ServerInfo> weightServerList = new ArrayList<>();
		for (int i = 0; i < waitSelectorServerList.size(); i++) {
			if (i <= waitSelectorServerList.get(i).getWeight()) {
				weightServerList.add(waitSelectorServerList.get(i));
			}
		}

		// 获取索引位置
		synchronized (LOCK_OBJECT) {
			if (atomicInteger.get() >= weightServerList.size()) {
				atomicInteger.set(0);
			}

			ServerInfo server = weightServerList.get(atomicInteger.getAndIncrement());
			return server;
		}
	}
}
