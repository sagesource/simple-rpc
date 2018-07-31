package org.sagesource.test.api.impl;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.sagesource.test.api.HelloWorldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by wangxiaoyan on 2018/6/24.
 */
@Service("helloWorldService")
public class HelloWorldServiceImpl implements HelloWorldService.Iface {
	private static Logger LOGGER = LoggerFactory.getLogger(HelloWorldServiceImpl.class);

	@Override
	public String sayHello(String username) throws TException {
		try {
			LOGGER.info(">>>>>>>> server receive:{}", username);
			int i = 1/0;
			return "Hi," + username + " welcome";
		} catch (Exception e) {
			throw new TApplicationException(e.getMessage());
		}
	}
}
