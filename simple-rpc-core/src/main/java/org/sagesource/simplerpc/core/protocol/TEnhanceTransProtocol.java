package org.sagesource.simplerpc.core.protocol;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TTransport;
import org.sagesource.simplerpc.basic.entity.ServerInfo;
import org.sagesource.simplerpc.core.trace.ThreadTrace;
import org.sagesource.simplerpc.core.trace.TraceSupport;

/**
 * <p>增强 TTprotocol</p>
 * <pre>
 *     author      XueQi
 *     date        2018/7/9
 *     email       job.xueqi@outlook.com
 * </pre>
 */
public class TEnhanceTransProtocol extends TMultiplexedProtocol {
	private static final String TRACEID_SEPARATOR = "#";

	/**
	 * 目标服务器信息
	 */
	private ServerInfo serverInfo;

	// 重写 ThriftProtocolFactory
	public static class Factory implements TProtocolFactory {
		private TProtocolFactory protocolFactory;

		public Factory(TProtocolFactory protocolFactory) {
			this.protocolFactory = protocolFactory;
		}

		@Override
		public TProtocol getProtocol(TTransport tTransport) {
			return new TEnhanceTransProtocol(this.protocolFactory.getProtocol(tTransport));
		}
	}

	public TEnhanceTransProtocol(TProtocol protocol) {
		super(protocol, null);
	}

	public TEnhanceTransProtocol(TProtocol protocol, ServerInfo serverInfo) {
		super(protocol, serverInfo.getServiceName());
		this.serverInfo = serverInfo;
	}

	/**
	 * 重写消息发送
	 *
	 * @param tMessage
	 * @throws TException
	 */
	@Override
	public void writeMessageBegin(TMessage tMessage) throws TException {
		Long traceId = ThreadTrace.get();
		super.writeMessageBegin(new TMessage(tMessage.name + TRACEID_SEPARATOR + traceId, tMessage.type, tMessage.seqid));
	}

	@Override
	public TMessage readMessageBegin() throws TException {
		TMessage message      = super.readMessageBegin();
		String   name         = message.name;
		int      traceIdIndex = name.indexOf(TRACEID_SEPARATOR);
		if (traceIdIndex == -1) {
			throw new TProtocolException(TProtocolException.UNKNOWN, "Expected trace protocol is error");
		}

		String traceId = name.substring(traceIdIndex + 1);
		if (traceId == null || "".equals(traceId)) {
			throw new TProtocolException(TProtocolException.INVALID_DATA, "Expected traceId is null");
		}

		TraceSupport.set(traceId);
		name = name.substring(0, traceIdIndex);
		return new TMessage(name, message.type, message.seqid);
	}

	public ServerInfo getServerInfo() {
		return serverInfo;
	}
}
