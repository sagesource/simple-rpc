package org.sagesource.simplerpc.spring.provider.dto;

/**
 * <p>多路复用服务配置</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/17
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MultiplexedServiceConfig {
	/**
	 * 权重 默认值=1
	 */
	private int weight = 1;
	/**
	 * 服务实现类
	 */
	private Object serviceRef;
	/**
	 * 服务版本号
	 */
	private String version;

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public void setServiceRef(Object serviceRef) {
		this.serviceRef = serviceRef;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getWeight() {
		return weight;
	}

	public Object getServiceRef() {
		return serviceRef;
	}

	public String getVersion() {
		return version;
	}
}
