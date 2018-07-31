package org.sagesource.test.loadbalance;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class LoadBalanceEngineTest {

	public static void main(String[] args) {
		System.setProperty("SIMPLERPC_STATIC_ROUTER", "org.sagesource.test.api.HelloWorldService.Iface.1.0.0=127.0.0.1:8090");

		/*LoadBalanceEngine loadBalanceEngine = new SimpleLoadBalanceEngineImpl();
		ServerInfo serverInfo = loadBalanceEngine.availableServerInfo("org.sagesource.test.api.HelloWorldService.Iface", "1.0.0");
		System.out.println(ReflectionToStringBuilder.toString(serverInfo));*/
	}

}
