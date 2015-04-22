package com.github.walterfan.server.logger.codec;


import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.github.walterfan.server.logger.LogData;
import com.github.walterfan.server.logger.message.LogMessage;

public class LogMessageDecoder  extends AbstractMessageDecoder {
	public static final int HEADER_LEN  = 12;
	public static final int BODY_MIN_LEN  = 8;
	private static Log logger = LogFactory.getLog(LogMessageDecoder.class);
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer buf,
			ProtocolDecoderOutput out) throws Exception {
		if (buf.remaining() > 20)
        {
            byte type = buf.get();//1 byte
            byte version = buf.get();//1 byte
            char reserve = buf.getChar();//2 bytes
            int length = buf.getInt();   //4 bytes       
            int sequence = buf.getInt(); //4 bytes            
            int size = buf.getInt(); //4 bytes
            
            if(type != LogMessage.LOG_MSG_TYPE
            		|| version != LogMessage.LOG_MSG_VERSION
            		|| 0 == length 
            		|| 0 == size 
            		|| buf.remaining() < length - 4) {
            	if(logger.isDebugEnabled()) {
            		logger.debug(String.format("Cannot get message:  type=%d, version=%d,reserver=%c, length=%d, sequence=%d,siz=%d" 
            				,type ,version,reserve, length ,sequence, size));
            	}
            	return false;
            }
            
            LogMessage msg = new LogMessage();
            msg.setSequence(sequence);
            
            for(int i=0;i < size;i++) {
            	int logChecksum = buf.getInt();
            	LogData logData = extractLogData(buf);
            	if(logData.calcChecksum() == logChecksum) {
            		msg.add(logData);                	
                } else {
                	return false;
                }
            }
            out.write(msg);
            return true;
            
        }
        
        return false;
        
	}
	
	private LogData extractLogData(IoBuffer buf) throws CharacterCodingException {
		 String logType = buf.getPrefixedString(decoder);
         long offset = buf.getLong();
         long timestamp = buf.getLong();
         String content = buf.getPrefixedString(decoder);
         
         LogData data = new LogData();
         data.setLogType(logType);
         data.setOffset(offset);
         data.setTimestamp(timestamp);
         data.setContent(content);
         return data;
	}

}
