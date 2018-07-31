package org.sagesource.simplerpc.core.filter;

import org.sagesource.simplerpc.basic.exception.SimpleRpcFilterException;
import org.sagesource.simplerpc.core.context.Context;
import org.sagesource.simplerpc.core.context.ThreadContext;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/10
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public abstract class AbstractBaseFilter implements IFilter {

	@Override
	public final void filter() {
		try {
			Context context = ThreadContext.get();
			doFilter(context);
		} catch (Exception e) {
			throw new SimpleRpcFilterException(e);
		}
	}

	/**
	 * 前置拦截器逻辑
	 *
	 * @param context
	 */
	protected abstract void doFilter(Context context);
}
