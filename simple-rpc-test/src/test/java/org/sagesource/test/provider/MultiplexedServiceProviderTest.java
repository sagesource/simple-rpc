package org.sagesource.test.provider;

import org.junit.Test;
import org.sagesource.simplerpc.core.zookeeper.ZookeeperClientFactory;
import org.sagesource.simplerpc.provider.MultiplexedServiceProviderFactory;
import org.sagesource.simplerpc.provider.dto.ServiceRegisterDto;
import org.sagesource.test.api.impl.HelloWorldServiceImpl;
import org.sagesource.test.api.impl.JobServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/16
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MultiplexedServiceProviderTest {

	@Test
	public void test() throws Exception {
		ServiceRegisterDto       serviceRegisterDto  = new ServiceRegisterDto(new HelloWorldServiceImpl(), "1.0.0");
		ServiceRegisterDto       serviceRegisterDto1 = new ServiceRegisterDto(new JobServiceImpl(), "1.0.0");
		List<ServiceRegisterDto> list                = Arrays.asList(serviceRegisterDto, serviceRegisterDto1);


		MultiplexedServiceProviderFactory multiplexedServiceProviderFactory = new MultiplexedServiceProviderFactory(8090, list);
		multiplexedServiceProviderFactory.createServiceProvider();

		Thread.sleep(1000000);

		MultiplexedServiceProviderFactory.close();
		ZookeeperClientFactory.close();
	}


	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("classpath*:spring-provider-multi.xml");
	}

}
