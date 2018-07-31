package org.sagesource.simplerpc.core.filter.common;

import com.alibaba.fastjson.JSON;
import org.sagesource.simplerpc.core.context.Context;
import org.sagesource.simplerpc.core.context.ThreadContext;
import org.sagesource.simplerpc.core.filter.AbstractBaseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>客户端默认后置过滤器</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/11
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ClientStubPostFilter extends AbstractBaseFilter {
	private static Logger LOGGER = LoggerFactory.getLogger(ClientStubPostFilter.class);

	@Override
	protected void doFilter(Context context) {
		// 设置结束时间
		context.setInvokeEndTime(System.currentTimeMillis());
		ThreadContext.set(context);
		LOGGER.debug(JSON.toJSONString(ThreadContext.get()));
	}
}
