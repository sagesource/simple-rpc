package org.sagesource.test.api.impl;

import org.apache.thrift.TException;
import org.sagesource.test.api.HelloWorldService;
import org.sagesource.test.api.JobService;
import org.sagesource.test.api.Response;
import org.sagesource.test.api.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/16
 *     email       job.xueqi@outlook.com
 * </pre>
 */
@Service("jobService")
public class JobServiceImpl implements JobService.Iface {
	private static Logger LOGGER = LoggerFactory.getLogger(JobServiceImpl.class);

	@Autowired
	@Qualifier("helloWorldServiceClient")
	private HelloWorldService.Iface helloWorldServiceClient;

	@Override
	public Response execute(String jobName) throws TException {
		LOGGER.info(">>>>>>>>>> provider jobName:{}", jobName);

		Response response = new Response();
		response.setCode(ResponseStatus.SUCCESS);
		response.setMessage("SUCCESS");

		LOGGER.info("call hello world service");
		LOGGER.info(helloWorldServiceClient.sayHello("trace sage"));

		return response;
	}
}
