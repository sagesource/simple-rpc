package org.sagesource.simplerpc.config;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>基本系统配置客户端，扩展可继承该类，实现不同配置的获取方式</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/10
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class BaseSystemConfigClient extends AbstractSystemConfigClient {

	@Override
	public String staticRouterConfig() {
		// 获取 JVM 配置
		String jvmStaticRouterConfig = super.getJvmPropertyValue(SIMPLERPC_STATIC_ROUTER_JVM, null);
		// 获取环境变量配置
		String envStaticRouterConfig = super.getEnvPropertyValue(SIMPLERPC_STATIC_ROUTER, null);
		String staticRouterConfig    = null;
		if (!StringUtils.isEmpty(jvmStaticRouterConfig)) {
			staticRouterConfig = jvmStaticRouterConfig;
		} else if (!StringUtils.isEmpty(envStaticRouterConfig)) {
			staticRouterConfig = envStaticRouterConfig;
		}
		return staticRouterConfig;
	}

	@Override
	public String zkConnStrConfig() {
		return getEnvPropertyValue(SIMEPLE_RPC_ZK, null);
	}

	@Override
	public List<String> clientBeforeFilterList() {
		String filterStr = super.getPropertiesValue(PROPERTY_CLIENTFILTER_FILENAME, "beforeFilter", null);
		if (!StringUtils.isEmpty(filterStr)) {
			return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(filterStr);
		}
		return new ArrayList<>(0);
	}

	@Override
	public List<String> clientPostFilterList() {
		String filterStr = super.getPropertiesValue(PROPERTY_CLIENTFILTER_FILENAME, "postFilter", null);
		if (!StringUtils.isEmpty(filterStr)) {
			return Splitter.on(",").trimResults().omitEmptyStrings().splitToList(filterStr);
		}
		return new ArrayList<>(0);
	}

	@Override
	public String appName() {
		String appName = super.getPropertiesValue(PROPERTY_GLOBAL_FILENAME, "app.name", null);
		return appName;
	}
}
