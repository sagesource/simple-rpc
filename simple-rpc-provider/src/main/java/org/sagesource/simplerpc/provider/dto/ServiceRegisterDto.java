package org.sagesource.simplerpc.provider.dto;

/**
 * <p> 服务注册信息 DTO </p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/16
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ServiceRegisterDto {
	/**
	 * 权重
	 */
	private int weight = 1;
	/**
	 * 服务实现类
	 */
	private Object serviceImpl;
	/**
	 * 服务版本号
	 */
	private String serviceVersion;

	public ServiceRegisterDto(int weight, Object serviceImpl, String serviceVersion) {
		this.weight = weight;
		this.serviceImpl = serviceImpl;
		this.serviceVersion = serviceVersion;
	}

	public ServiceRegisterDto(Object serviceImpl, String serviceVersion) {
		this.serviceImpl = serviceImpl;
		this.serviceVersion = serviceVersion;
	}

	public int getWeight() {
		return weight;
	}

	public Object getServiceImpl() {
		return serviceImpl;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}
}
