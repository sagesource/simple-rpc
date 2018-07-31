package org.sagesource.simplerpc.core.trace;

import org.apache.commons.lang3.StringUtils;
import org.sagesource.simplerpc.basic.Constants;
import org.slf4j.MDC;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/11
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class TraceSupport {

	/**
	 * trace 统一处理
	 */
	public static void set(String traceId) {
		if (StringUtils.isEmpty(traceId)) {
			if (ThreadTrace.get() == null) {
				Long traceVal = TraceFun.getTrace();
				ThreadTrace.set(traceVal);
				MDC.put(Constants.TRACE_ID, traceVal + "");
			}
		} else {
			Long traceVal = Long.valueOf(traceId);
			ThreadTrace.set(traceVal);
			MDC.put(Constants.TRACE_ID, traceId);
		}
	}

}
