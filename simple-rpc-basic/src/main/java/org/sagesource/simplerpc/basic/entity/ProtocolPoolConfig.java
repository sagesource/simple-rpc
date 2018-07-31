package org.sagesource.simplerpc.basic.entity;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * <p>连接池配置类</p>
 * <pre>
 *     author      XueQi
 *     date        2018/6/29
 *     email       sage.xue@vipshop.com
 * </pre>
 */
public class ProtocolPoolConfig extends GenericObjectPoolConfig {
	// 超时时间
	private int     timeout   = 3000000;
	// 保持长连接
	private boolean keepAlive = true;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public boolean getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}
