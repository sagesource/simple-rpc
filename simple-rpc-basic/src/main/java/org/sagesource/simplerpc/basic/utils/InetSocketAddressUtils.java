package org.sagesource.simplerpc.basic.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * <p>IP 工具类</p>
 * <pre>
 *     author      XueQi
 *     date        2018/6/29
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class InetSocketAddressUtils {
	private static Logger LOGGER = LoggerFactory.getLogger(InetSocketAddressUtils.class);

	// 默认 IP 地址
	private static final String DEFAULT_LOCAL_IP = "127.0.0.1";

	// 本地 IP 地址
	private static String localIP;

	static {
		initLocalIP();
	}

	/**
	 * 初始化本地 IP 地址
	 */
	private static void initLocalIP() {
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = netInterfaces.nextElement();
				// 每个网络接口,都会有多个"网络地址",比如一定会有lookback地址,会有siteLocal地址等.以及IPV4或者IPV6 .
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if (address instanceof Inet6Address) {
						continue;
					}
					if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
						localIP = address.getHostAddress();
						LOGGER.info("resolve server ip :" + localIP);
						continue;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Init Local IP Error.", e);
		}
	}

	public static String getLocalIP() {
		if (StringUtils.isEmpty(localIP)) return DEFAULT_LOCAL_IP;

		return localIP;
	}
}
