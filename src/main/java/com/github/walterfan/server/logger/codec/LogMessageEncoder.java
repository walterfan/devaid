package com.github.walterfan.server.logger.codec;

import java.nio.charset.CharacterCodingException;
import java.util.Iterator;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import com.github.walterfan.server.logger.LogData;
import com.github.walterfan.server.logger.message.LogMessage;



public class LogMessageEncoder  extends AbstractMessageEncoder {

	@Override
	protected void encodeBody(IoSession session, Object message,
			IoBuffer out) {
		LogMessage msg = (LogMessage) message;
		
		out.put(LogMessage.LOG_MSG_TYPE);
		out.put(LogMessage.LOG_MSG_VERSION);
		out.putChar(LogMessage.RESERVED_CHAR);
		out.putInt(msg.calcLength());
		out.putInt(msg.getSequence());
		out.putInt(msg.size());		
		Iterator<LogData> it = msg.iterator();
		while(it.hasNext()) {
			LogData data = it.next();
			out.putInt(data.calcChecksum());
			try {
				//it will add a 16bit prefix of length before encoded string
				out.putPrefixedString(data.getLogType(), encoder);
				out.putLong(data.getOffset());
				out.putLong(data.getTimestamp());
				out.putPrefixedString(data.getContent(),encoder);
			} catch (CharacterCodingException e) {
				throw new RuntimeException(e);
			}

		}
	}

	@Override
	protected byte getType(Object message) {
		return LogMessage.LOG_MSG_TYPE;
	}

	@Override
	protected byte getVersion(Object message) {
		return LogMessage.LOG_MSG_VERSION;
	}	
				
	

	

}
