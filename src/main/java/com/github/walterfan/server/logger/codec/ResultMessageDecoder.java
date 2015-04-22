package com.github.walterfan.server.logger.codec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.github.walterfan.server.logger.message.LogMessage;
import com.github.walterfan.server.logger.message.ResultMessage;

public class ResultMessageDecoder extends CumulativeProtocolDecoder {

	private Log logger = LogFactory.getLog(ResultMessageDecoder.class);
	@Override
	protected boolean doDecode(IoSession arg0, IoBuffer buf,
			ProtocolDecoderOutput out) throws Exception {

		if (buf.remaining() >= 9)
        {         
            byte type = buf.get();//1 byte
            byte version = buf.get();//1 byte
            char reserve = buf.getChar();//2 bytes
            
            if(type != LogMessage.RESULT_MSG_TYPE
            		|| version != LogMessage.RESULT_MSG_VERSION) {
            	if(logger.isDebugEnabled()) {
            		logger.debug("Cannot get message " + type + ", "+ 
            			version  + ", "+ buf.remaining());
            	}
            	return false;
            }
			
			boolean isOkay = (buf.get()==1)? true:false;
			int sequence = buf.getInt();
			ResultMessage msg = new ResultMessage();
			msg.setOkay(isOkay);
			msg.setSequence(sequence);
			out.write(msg);
			return true;
        }
		return false;
	}

}
