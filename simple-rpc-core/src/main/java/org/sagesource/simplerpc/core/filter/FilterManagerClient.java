package org.sagesource.simplerpc.core.filter;

import org.sagesource.simplerpc.config.SystemConfigClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>过滤器管理客户端</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/10
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class FilterManagerClient {
	private static Logger LOGGER = LoggerFactory.getLogger(FilterManagerClient.class);

	// .... 单例处理 .... //
	private FilterManagerClient() {
	}

	private static class INSTANCE {
		private static final FilterManagerClient FILTER_MANAGER_CLIENT = new FilterManagerClient();
	}

	public static FilterManagerClient getInstance() {
		return INSTANCE.FILTER_MANAGER_CLIENT;
	}

	/**
	 * 获取客户端前置过滤器
	 *
	 * @return
	 */
	public List<IFilter> getClientBeforeFilter() {
		List<String> clientFilterClazzList = SystemConfigClientManager.getSystemConfigClient().clientBeforeFilterList();
		return convertFilterList(clientFilterClazzList);
	}

	/**
	 * 获取客户端后置过滤器
	 *
	 * @return
	 */
	public List<IFilter> getClientPostFilter() {
		List<String> clientFilterClazzList = SystemConfigClientManager.getSystemConfigClient().clientPostFilterList();
		return convertFilterList(clientFilterClazzList);
	}

	//..................//

	/**
	 * 转换过滤器对象列表
	 *
	 * @param filterClazzList
	 * @return
	 */
	private List<IFilter> convertFilterList(List<String> filterClazzList) {
		List<IFilter> filterList = new ArrayList<>();
		for (String filterClazz : filterClazzList) {
			try {
				IFilter filter = (IFilter) FilterManagerClient.class.getClassLoader().loadClass(filterClazz).newInstance();
				filterList.add(filter);
			} catch (Exception e) {
				LOGGER.error("obtain filter clazz error, filterClass=" + filterClazz, e);
			}
		}
		return filterList;
	}
}
