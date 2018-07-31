package org.sagesource.simplerpc.core.loadbalance;

import org.sagesource.simplerpc.core.loadbalance.impl.SimpleRoundRobinEngineImpl;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class RoundRobinFactory {
	private static final RoundRobinEngine ROUND_ROBIN_ENGINE = new SimpleRoundRobinEngineImpl();

	/**
	 * 获取服务器轮询处理引擎
	 *
	 * @return
	 */
	public static RoundRobinEngine getRoundRobinEngine() {
		return ROUND_ROBIN_ENGINE;
	}

}
