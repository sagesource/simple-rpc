package org.sagesource.simplerpc.core.loadbalance;

import org.sagesource.simplerpc.core.loadbalance.impl.SimpleLoadBalanceEngineImpl;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class LoadBalanceFactory {
	private static final LoadBalanceEngine LOAD_BALANCE_ENGINE = new SimpleLoadBalanceEngineImpl();

	/**
	 * 获取 LoadBalance 引擎
	 *
	 * @return
	 */
	public static LoadBalanceEngine getLoadBalanceEngine() {
		return LOAD_BALANCE_ENGINE;
	}

}
