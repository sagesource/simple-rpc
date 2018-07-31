package org.sagesource.simplerpc.basic.exception;

/**
 * <p></p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/11
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class SimpleRpcFilterException extends SimpleRpcException {

	public SimpleRpcFilterException() {
	}

	public SimpleRpcFilterException(String message) {
		super(message);
	}

	public SimpleRpcFilterException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimpleRpcFilterException(Throwable cause) {
		super(cause);
	}
}
