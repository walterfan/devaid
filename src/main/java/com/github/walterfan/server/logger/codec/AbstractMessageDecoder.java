package com.github.walterfan.server.logger.codec;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;


public abstract class AbstractMessageDecoder extends CumulativeProtocolDecoder {
	protected final CharsetDecoder decoder = Charset.forName("ISO-8859-1").newDecoder();
	

}
