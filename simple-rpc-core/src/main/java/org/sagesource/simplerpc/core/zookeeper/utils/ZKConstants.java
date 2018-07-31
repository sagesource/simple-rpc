package org.sagesource.simplerpc.core.zookeeper.utils;

/**
 * <p>ZK 常量配置</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/3
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface ZKConstants {
	public static final String DEFAULT_NAMESPACES = "default";

	public static final int DEFAULT_SESSION_TIMEOUT_MS    = 60 * 1000;
	public static final int DEFAULT_CONNECTION_TIMEOUT_MS = 3 * 1000;
	public static final int DEFAULT_RETRY_TIMES           = 1;
	public static final int DEFAULT_SLEEPMS_BETWEENRETRY  = 30;
}
