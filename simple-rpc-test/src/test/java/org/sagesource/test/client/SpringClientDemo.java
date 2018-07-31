package org.sagesource.test.client;

import org.apache.thrift.TException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sagesource.simplerpc.core.trace.ThreadTrace;
import org.sagesource.simplerpc.core.trace.TraceFun;
import org.sagesource.simplerpc.core.zookeeper.ZookeeperClientFactory;
import org.sagesource.test.api.HelloWorldService;
import org.sagesource.test.api.JobService;
import org.sagesource.test.api.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/1
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:spring-client.xml"})
public class SpringClientDemo {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringClientDemo.class);

	@Autowired
	@Qualifier("helloWorldService")
	private HelloWorldService.Iface helloWorldService;

	@Autowired
	@Qualifier("jobService")
	private JobService.Iface jobService;

	@Test
	public void test() throws Exception {
		for (int i = 0; i < 2; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
						ThreadTrace.set(TraceFun.getTrace());
						MDC.put("traceId", ThreadTrace.get() + "");
						LOGGER.info(helloWorldService.sayHello("sage" + ThreadTrace.get()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
		while (true) ;
		/*ClientProtocolPoolFactory.close();
		ServiceAddressProviderAgent.close();
		ZookeeperClientFactory.close();*/
	}

	@Test
	public void test2() throws TException {
		Response resp = jobService.execute("sage");
		LOGGER.info(resp.message);
		System.out.println(ThreadTrace.get());
	}
}
