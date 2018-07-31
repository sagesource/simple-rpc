package org.sagesource.simplerpc.core.trace;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>获取 long 型 traceId</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class TraceFun {
	private static final AtomicLong counter = new AtomicLong(0);

	/**
	 * 获取 Long 型 traceId
	 *
	 * @return
	 */
	public static Long getTrace() {
		return System.currentTimeMillis() + counter.incrementAndGet();
	}
}
