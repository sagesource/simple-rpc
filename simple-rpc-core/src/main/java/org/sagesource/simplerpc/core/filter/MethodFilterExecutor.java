package org.sagesource.simplerpc.core.filter;

import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.basic.exception.SimpleRpcFilterException;
import org.sagesource.simplerpc.core.context.Context;
import org.sagesource.simplerpc.core.context.ThreadContext;
import org.sagesource.simplerpc.core.trace.ThreadTrace;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>方法过滤器执行器</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/11
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class MethodFilterExecutor {
	// 客户端前置过滤器列表
	private static List<IFilter> clientBeforeFilterList = new ArrayList<>();
	// 客户端后置过滤器
	private static List<IFilter> clientPostFilterList   = new ArrayList<>();

	static {
		// 客户端前置过滤器列表
		clientBeforeFilterList.addAll(FilterManagerClient.getInstance().getClientBeforeFilter());
		// 客户端后置过滤器
		clientPostFilterList.addAll(FilterManagerClient.getInstance().getClientPostFilter());
	}

	/**
	 * 客户端执行方法
	 *
	 * @param instance
	 * @param method
	 * @param args
	 * @param serverInfo
	 * @return
	 */
	public static Object clientInvokeMethodByFilter(Object instance, Method method, Object[] args, ServerInfo serverInfo) throws Exception {
		// 创建 Context
		Context context = new Context(ThreadTrace.get(), args, serverInfo.getServiceName(), method.getName(), serverInfo.getServiceVersion(),
				serverInfo.getServerIP(), serverInfo.getPort(), serverInfo.getAppName());
		ThreadContext.set(context);

		try {
			// 执行前置拦截器
			executorFilter(clientBeforeFilterList);

			// 执行方法
			return method.invoke(instance, args);
		} catch (Exception e) {
			throw e;
		} finally {
			// 执行后置拦截器
			executorFilter(clientPostFilterList);
		}
	}

	/**
	 * 执行过滤器
	 */
	private static void executorFilter(List<IFilter> filterList) throws SimpleRpcFilterException {
		for (IFilter iFilter : filterList) {
			iFilter.filter();
		}
	}
}
