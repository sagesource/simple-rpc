package org.sagesource.test.client;

import org.sagesource.simplerpc.client.proxy.ServiceClientProxy;
import org.sagesource.simplerpc.basic.entity.ProtocolPoolConfig;
import org.sagesource.test.api.HelloWorldService;

import java.util.concurrent.CyclicBarrier;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/6/29
 *     email       sage.xue@vipshop.com
 * </pre>
 */
public class HelloClientDemo {
	private static CyclicBarrier cyclicBarrier = new CyclicBarrier(20);

	public static void main(String[] args) throws Exception {
		ProtocolPoolConfig protocolPoolConfig = new ProtocolPoolConfig();
		protocolPoolConfig.setKeepAlive(true);
		protocolPoolConfig.setTimeout(300000);
		protocolPoolConfig.setMinIdle(1);
		protocolPoolConfig.setMaxIdle(8);
		protocolPoolConfig.setMaxTotal(8);

		for (int i = 0; i < 20; i++) {
			new Thread(new ThreadA(protocolPoolConfig)).start();
		}
	}

	static class ThreadA implements Runnable {
		private ProtocolPoolConfig protocolPoolConfig;

		public ThreadA(ProtocolPoolConfig protocolPoolConfig) {
			this.protocolPoolConfig = protocolPoolConfig;
		}

		@Override
		public void run() {
			try {
				System.out.println(Thread.currentThread().getName() + " waiting...");
				cyclicBarrier.await();
				HelloWorldService.Iface client = ServiceClientProxy.createClient(HelloWorldService.Iface.class, "1.0.0", protocolPoolConfig);
				System.out.println(client.sayHello(Thread.currentThread().getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
