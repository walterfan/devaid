package com.github.walterfan.util.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class ByteBufferUtils {
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static CharsetDecoder decoder = charset.newDecoder();

	public static ByteBuffer str_to_bb(String msg){
	  try{
	    return encoder.encode(CharBuffer.wrap(msg));
	  }catch(Exception e){e.printStackTrace();}
	  return null;
	}

	public static String bb_to_str(ByteBuffer buffer){
	  String data = "";
	  try{
	    int old_position = buffer.position();
	    data = decoder.decode(buffer).toString();
	    // reset buffer's position to its original so it is not altered:
	    buffer.position(old_position);  
	  }catch (Exception e){
	    e.printStackTrace();
	    return "";
	  }
	  return data;
	}

}
