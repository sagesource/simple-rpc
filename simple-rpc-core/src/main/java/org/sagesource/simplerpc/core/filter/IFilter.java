package org.sagesource.simplerpc.core.filter;

import org.sagesource.simplerpc.basic.exception.SimpleRpcFilterException;

/**
 * <p> 过滤器接口 </p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public interface IFilter {

	/**
	 * 执行过滤器
	 */
	void filter() throws SimpleRpcFilterException;
}
