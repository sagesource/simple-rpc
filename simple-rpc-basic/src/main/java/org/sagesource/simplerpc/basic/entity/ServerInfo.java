package org.sagesource.simplerpc.basic.entity;

/**
 * <p>服务端信息封装，包括 IP HOST 服务 版本号信息 等</p>
 * <pre>
 *     author      XueQi
 *     date        2018/6/29
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ServerInfo {

	// 服务端 IP
	private String serverIP;
	// 服务端口号
	private int    port;
	// 服务名称
	private String serviceName;
	// 服务版本号
	private String serviceVersion;
	// 服务权重
	private int    weight;
	// 应用名称
	private String appName;

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		ServerInfo serverInfo = (ServerInfo) obj;

		if (serverInfo.serviceVersion.equals(this.serviceVersion)
				&& serverInfo.serviceName.equals(this.serviceName)
				&& serverInfo.serverIP.equals(this.serverIP)
				&& serverInfo.port == this.port)
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "ServerInfo:[serverIP=" + this.serverIP + ",port=" + this.port + ",serviceName=" + this.serviceName + ",serviceVersion=" + this.serviceVersion + ",weight=" + this.weight + "]";
	}

	public String getServerIP() {
		return serverIP;
	}

	public int getPort() {
		return port;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getServiceVersion() {
		return serviceVersion;
	}

	public int getWeight() {
		return weight;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setServiceVersion(String serviceVersion) {
		this.serviceVersion = serviceVersion;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}
