package org.sagesource.simplerpc.basic.exception;

/**
 * <p>Protocol 相关异常</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class ProtocolException extends SimpleRpcException {

	public ProtocolException() {
	}

	public ProtocolException(String message) {
		super(message);
	}

	public ProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolException(Throwable cause) {
		super(cause);
	}
}
