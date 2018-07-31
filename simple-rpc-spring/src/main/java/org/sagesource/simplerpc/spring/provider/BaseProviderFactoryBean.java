package org.sagesource.simplerpc.spring.provider;

import org.sagesource.simplerpc.core.zookeeper.ZookeeperClientFactory;
import org.sagesource.simplerpc.provider.MultiplexedServiceProviderFactory;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/17
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class BaseProviderFactoryBean {

	/**
	 * 关闭方法
	 */
	public void close() {
		ZookeeperClientFactory.close();
		MultiplexedServiceProviderFactory.close();
	}
}
