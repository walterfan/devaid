package com.github.walterfan.server.logger.codec;



import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/*------------------------- PDU header ---------------------------
type(8) + version(6) + priority(2) + reserve number(16) +
length(32) + checksum(32)      total  12 bytes   96 bits
------------------------------------------------------------------*/
public abstract class AbstractMessageEncoder implements ProtocolEncoder {
	
	protected final CharsetEncoder encoder = Charset.forName("ISO-8859-1").newEncoder();
	
	private char reserve;
	//----total 4 bytes == sizeof(int)
	
	public void dispose(IoSession arg0) throws Exception {
		// nothing to dispose
	}

    public void encode(IoSession session, Object msg,
            ProtocolEncoderOutput out) throws Exception {

        IoBuffer buf = IoBuffer.allocate(16);
        buf.setAutoExpand(true); // Enable auto-expand for easier encoding

        // Encode a header
        encodeHeader(session, msg, buf);
        // Encode a body
        encodeBody(session, msg, buf);
        
        buf.flip();
        out.write(buf);
    }
    protected  void encodeHeader(IoSession session,
    		Object message, IoBuffer out) {
        // Encode a header
        out.put(this.getType(message));
        out.put(this.getVersion(message));
        out.putChar(this.getReserve(message));
        //out.putInt(this.getMsgLength(message));
        //out.putInt(this.getMsgCheckSum(message));
    }
    
    protected abstract byte getType(Object message) ;
  
    protected abstract byte getVersion(Object message);
    
    protected char getReserve(Object message) {
    	return this.reserve;
    }
    
 /* protected abstract int getMsgLength(Object message);
    
    protected abstract int getMsgCheckSum(Object message);
 */   
    protected abstract void encodeBody(IoSession session,
            Object message, IoBuffer out);
}

