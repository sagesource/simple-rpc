package org.sagesource.simplerpc.spring.provider;

import org.sagesource.simplerpc.provider.MultiplexedServiceProviderFactory;
import org.sagesource.simplerpc.provider.dto.ServiceRegisterDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Arrays;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/13
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ServiceProviderFactoryBean extends BaseProviderFactoryBean implements InitializingBean {
	private static Logger LOGGER = LoggerFactory.getLogger(ServiceProviderFactoryBean.class);

	/**
	 * 服务端口号
	 */
	private int    port;
	/**
	 * 服务实现类
	 */
	private Object serviceRef;
	/**
	 * 服务版本号
	 */
	private String version;
	/**
	 * 服务节点权重
	 */
	private int    weight;

	@Override
	public void afterPropertiesSet() throws Exception {
		// 设置默认权重
		if (weight <= 0) this.weight = 1;

		ServiceRegisterDto                serviceRegisterDto           = new ServiceRegisterDto(this.weight, this.serviceRef, this.version);
		MultiplexedServiceProviderFactory simpleServiceProviderFactory = new MultiplexedServiceProviderFactory(this.port, Arrays.asList(serviceRegisterDto));
		simpleServiceProviderFactory.createServiceProvider();

		LOGGER.info("service provider factory init. serviceRef={},version={},port={},weight={}", this.serviceRef, this.version, this.port, this.weight);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setServiceRef(Object serviceRef) {
		this.serviceRef = serviceRef;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
