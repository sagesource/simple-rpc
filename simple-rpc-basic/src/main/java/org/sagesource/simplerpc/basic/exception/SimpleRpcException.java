package org.sagesource.simplerpc.basic.exception;

/**
 * <p>框架异常 Exception</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/2
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class SimpleRpcException extends RuntimeException {

	public SimpleRpcException() {
	}

	public SimpleRpcException(String message) {
		super(message);
	}

	public SimpleRpcException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimpleRpcException(Throwable cause) {
		super(cause);
	}
}
