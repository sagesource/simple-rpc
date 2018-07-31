package org.sagesource.simplerpc.core.context;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ThreadContext {
	private static ThreadLocal<Context> contextThreadLocal = new InheritableThreadLocal<>();

	public static void set(Context context) {
		contextThreadLocal.set(context);
	}

	public static Context get() {
		return contextThreadLocal.get();
	}

	public static void remove() {
		contextThreadLocal.remove();
	}
}
