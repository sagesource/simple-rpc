package org.sagesource.simplerpc.config;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/10
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class SystemConfigClientManager {
	private static final SystemConfigClient SYSTEM_CONFIG_CLIENT = new BaseSystemConfigClient();

	public static SystemConfigClient getSystemConfigClient() {
		return SYSTEM_CONFIG_CLIENT;
	}
}
