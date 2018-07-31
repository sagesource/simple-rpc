package org.sagesource.simplerpc.spring.provider;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.sagesource.simplerpc.provider.MultiplexedServiceProviderFactory;
import org.sagesource.simplerpc.provider.dto.ServiceRegisterDto;
import org.sagesource.simplerpc.spring.provider.dto.MultiplexedServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * <p>多路复用</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/16
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MultiplexedServiceProviderFactoryBean extends BaseProviderFactoryBean implements InitializingBean {
	private static Logger LOGGER = LoggerFactory.getLogger(MultiplexedServiceProviderFactoryBean.class);

	/**
	 * 服务端口
	 */
	private int                            port;
	/**
	 * 服务配置
	 */
	private List<MultiplexedServiceConfig> configList;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.configList == null || this.configList.size() == 0)
			throw new IllegalArgumentException("create multiplexed service provider factory error. config list is empty!");

		List<ServiceRegisterDto> serviceRegisterDtoList = Lists.transform(this.configList, new Function<MultiplexedServiceConfig, ServiceRegisterDto>() {
			@Override
			public ServiceRegisterDto apply(MultiplexedServiceConfig input) {
				ServiceRegisterDto serviceRegisterDto = new ServiceRegisterDto(input.getWeight(), input.getServiceRef(), input.getVersion());
				return serviceRegisterDto;
			}
		});

		MultiplexedServiceProviderFactory multiplexedServiceProviderFactory = new MultiplexedServiceProviderFactory(this.port, serviceRegisterDtoList);
		multiplexedServiceProviderFactory.createServiceProvider();

		LOGGER.info("multiplexed service provider factory init. serviceRegisterDtoList={},port={}", serviceRegisterDtoList, this.port);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setConfigList(List<MultiplexedServiceConfig> configList) {
		this.configList = configList;
	}
}
