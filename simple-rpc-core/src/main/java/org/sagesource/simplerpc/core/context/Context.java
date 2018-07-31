package org.sagesource.simplerpc.core.context;

import org.sagesource.simplerpc.basic.utils.InetSocketAddressUtils;
import org.sagesource.simplerpc.config.SystemConfigClientManager;

/**
 * <p>上下文对象</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class Context {
	/**
	 * 追踪 Id
	 */
	private Long     traceId;
	/**
	 * 调用方应用名称
	 */
	private String   invokeAppName;
	/**
	 * 调用方请求参数
	 */
	private Object[] invokeArgs;
	/**
	 * 调用发起时间
	 */
	private long     invokeBeginTime;
	/**
	 * 调用结束时间
	 */
	private long     invokeEndTime;
	/**
	 * 调用方地址
	 */
	private String   invokeAddress;

	/**
	 * 目标服务名称
	 */
	private String providerServiceName;
	/**
	 * 目标服务方法
	 */
	private String providerServiceMethod;
	/**
	 * 目标服务版本号
	 */
	private String providerServiceVersion;
	/**
	 * 目标服务地址
	 */
	private String providerServiceAddress;
	/**
	 * 目标服务端口
	 */
	private int    providerServicePort;
	/**
	 * 目标服务 AppName
	 */
	private String providerAppName;

	public Context(Long traceId, Object[] invokeArgs, String providerServiceName, String providerServiceMethod, String providerServiceVersion, String providerServiceAddress, int providerServicePort, String providerAppName) {
		this.traceId = traceId;
		this.invokeArgs = invokeArgs;
		this.providerServiceName = providerServiceName;
		this.providerServiceMethod = providerServiceMethod;
		this.providerServiceVersion = providerServiceVersion;
		this.providerServiceAddress = providerServiceAddress;
		this.providerServicePort = providerServicePort;
		this.providerAppName = providerAppName;

		this.invokeAppName = SystemConfigClientManager.getSystemConfigClient().appName();
		this.invokeBeginTime = System.currentTimeMillis();
		this.invokeAddress = InetSocketAddressUtils.getLocalIP();
	}

	public void setInvokeEndTime(long invokeEndTime) {
		this.invokeEndTime = invokeEndTime;
	}

	public String getInvokeAppName() {
		return invokeAppName;
	}

	public Object[] getInvokeArgs() {
		return invokeArgs;
	}

	public long getInvokeBeginTime() {
		return invokeBeginTime;
	}

	public long getInvokeEndTime() {
		return invokeEndTime;
	}

	public String getProviderServiceName() {
		return providerServiceName;
	}

	public String getProviderServiceVersion() {
		return providerServiceVersion;
	}

	public String getProviderServiceAddress() {
		return providerServiceAddress;
	}

	public int getProviderServicePort() {
		return providerServicePort;
	}

	public String getProviderAppName() {
		return providerAppName;
	}

	public String getProviderServiceMethod() {
		return providerServiceMethod;
	}

	public Long getTraceId() {
		return traceId;
	}

	public String getInvokeAddress() {
		return invokeAddress;
	}
}
