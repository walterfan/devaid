package com.github.walterfan.server.logger.codec;


import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.github.walterfan.server.logger.message.ResultMessage;


/**
 * 
 * 
 * @author walter
 *
 */
public class ResultMessageEncoder implements ProtocolEncoder {

	public void dispose(IoSession arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void encode(IoSession session, Object message, ProtocolEncoderOutput out)
			throws Exception {

        IoBuffer buf = IoBuffer.allocate(8);
        buf.setAutoExpand(true); // Enable auto-expand for easier encoding

        ResultMessage msg = (ResultMessage)message;
        buf.put(ResultMessage.RESULT_MSG_TYPE);//1 byte
        buf.put(ResultMessage.RESULT_MSG_VERSION);//1 byte
        buf.putChar(ResultMessage.RESERVED_CHAR);//2 bytes
        buf.put((byte)(msg.isOkay()?1:0));//1 byte
        buf.putInt(msg.getSequence());    //4 bytes
        //---------------------------------total 9 bytes
        buf.flip();
        out.write(buf);
    }

	/*@Override
	protected void encodeBody(IoSession session, Object message, IoBuffer out) {
		ResultMessage msg = (ResultMessage)message;
		out.put((byte)(msg.isOkay()?1:0));
		out.putInt(msg.getSequence());
	}

	@Override
	protected int getMsgCheckSum(Object message) {
		ResultMessage msg = (ResultMessage)message;
		return (msg.isOkay()?1:0)+ msg.getSequence();
	}

	@Override
	protected int getMsgLength(Object message) {
		return 8 + 32;
	}*/


}
