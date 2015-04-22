package com.github.walterfan.server.logger.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;



public class LogCodecFactory implements ProtocolCodecFactory {
	private ProtocolEncoder encoder;
	private ProtocolDecoder decoder;

	public LogCodecFactory(boolean client) {
		if (client) {
			encoder = new LogMessageEncoder();
			decoder = new ResultMessageDecoder();
		} else {
			encoder = new ResultMessageEncoder();
			decoder = new LogMessageDecoder();
		}
	}

	public ProtocolEncoder getEncoder() throws Exception {
		return encoder;
	}

	public ProtocolDecoder getDecoder() throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}}
