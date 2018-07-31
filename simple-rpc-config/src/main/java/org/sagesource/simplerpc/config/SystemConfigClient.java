package org.sagesource.simplerpc.config;

import java.util.List;

/**
 * <p>系统配置客户端接口</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/10
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface SystemConfigClient {

	/**
	 * 获取静态路由配置
	 *
	 * @return
	 */
	String staticRouterConfig();

	/**
	 * 获取 ZK 连接地址
	 *
	 * @return
	 */
	String zkConnStrConfig();

	/**
	 * 获取前置拦截器列表
	 *
	 * @return
	 */
	List<String> clientBeforeFilterList();

	/**
	 * 获取后置拦截器列表
	 *
	 * @return
	 */
	List<String> clientPostFilterList();

	/**
	 * 获取应用名称
	 *
	 * @return
	 */
	String appName();
}
