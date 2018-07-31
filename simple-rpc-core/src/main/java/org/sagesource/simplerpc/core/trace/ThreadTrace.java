package org.sagesource.simplerpc.core.trace;

/**
 * <p> 存放 trace 信息线程变量</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ThreadTrace {
	//线程本地变量，子线程也能读取父线程设置变量，服务调用子线程调用其他的服务场景
	private static ThreadLocal<Long> threadLocal = new InheritableThreadLocal<>();

	public static void set(Long traceId) {
		threadLocal.set(traceId);
	}

	public static Long get() {
		return threadLocal.get();
	}

	public static void remove() {
		threadLocal.remove();
	}
}
