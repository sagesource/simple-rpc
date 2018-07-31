package org.sagesource.test.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/13
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class SpringSimpleProviderTest {

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("classpath*:spring-provider.xml");
	}

}
