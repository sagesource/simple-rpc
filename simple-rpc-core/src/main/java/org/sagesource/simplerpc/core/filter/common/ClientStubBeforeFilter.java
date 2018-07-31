package org.sagesource.simplerpc.core.filter.common;

import com.alibaba.fastjson.JSON;
import org.sagesource.simplerpc.core.context.Context;
import org.sagesource.simplerpc.core.context.ThreadContext;
import org.sagesource.simplerpc.core.filter.AbstractBaseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>客户端默认前置过滤器</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/11
 *     email       sage.xue@vipshop.com
 * </pre>
 */
public class ClientStubBeforeFilter extends AbstractBaseFilter {
	private static Logger LOGGER = LoggerFactory.getLogger(ClientStubBeforeFilter.class);

	@Override
	protected void doFilter(Context context) {
		// 上传 Context 信息
		LOGGER.debug(JSON.toJSONString(ThreadContext.get()));
	}
}
