package com.github.walterfan.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import com.mysql.jdbc.StringUtils;


public class ParamUtils {

    /** The Constant BUFFER. */
    public static final int BUF_LEN = 1024;

    private static Set<String> exceptionSet = new HashSet<String>(20);
    
    private static String exceptionKeys ;
    		
    static {
		exceptionKeys =  "type,name,id,WIDTH,HEIGHT,GpcProductRoot,GpcProductVersion,GpcErrorPageUrl,";
		exceptionKeys += "FilterSecParameters,GpcCompressMethod,GpcUnpackName,GpcUrlRoot,";
		exceptionKeys += "GpcUnpackVersion,GpcExtVersion,GpcExtName, GpcMovingInSubdir, GpcFullPage";
		String[] keys = exceptionKeys.split(",");
		for(String key: keys) {
			//System.out.println(key);
			exceptionSet.add(key.trim());
		}
    }
    public static String decode(String input) throws DecoderException 
    {
    	if(StringUtils.isEmptyOrWhitespaceOnly(input)) {
    		return "";
    	}
    	StringBuilder sb = new StringBuilder("");
    	String[] items = input.split("\\s+");
    	
    	String tailStr = "";
    	if(input.endsWith("/>")) {
    		tailStr = "/>";
    		input = input.substring(0, input.length()-2);
    	}
    	for(String item: items) {
    		//System.out.println("----" + item);
    		String[] kv = StringUtil.split(item, "=");
    		if(kv == null) {
    			sb.append(item);
    			sb.append(" ");
    			continue;
    		}
    		if(kv.length == 2) {
    			sb.append(kv[0].trim() + "=");
    			String key = kv[0].trim();
    			String val = kv[1].trim();
    			val = StringUtil.trimLeadingCharacter(val, '\"');
    			val = StringUtil.trimTrailingCharacter(val, '\"');
    			
    			
    			//System.out.println(key + "---" + val);
    			if(exceptionSet.contains(key))
    				sb.append("\"" + val + "\"");
    			else { 
    				sb.append("\"");
    				sb.append(new String(EncodeUtils.decodeBase64(kv[1].getBytes())));
    				sb.append("\"");
    			}
    		}
    		else {
    			System.err.println("no = in it");
    			sb.append(kv[0]);
    		}
    		sb.append(" \n");
    	}
    	String output = StringUtil.trimTrailingWhitespace(sb.toString());
    	return output + tailStr;
    }
    
    public static String encode(String input) throws EncoderException  {
    	
    	if(StringUtils.isEmptyOrWhitespaceOnly(input)) {
    		return "";
    	}
    	
    	StringBuilder sb = new StringBuilder("");
    	String[] items = input.split("\\s+");
    	input = StringUtil.trimTrailingWhitespace(input);
    	String tailStr = "";
    	if(input.endsWith("/>")) {
    		tailStr = "/>";
    		input = input.substring(0, input.length()-2);
    	}
    	
    	for(String item: items) {
    		//System.out.println( "---" + item);
    		String[] kv = StringUtil.split(item, "=");
    		if(kv == null) {
    			sb.append(item);
    			sb.append(" ");
    			continue;
    		}
    		if(kv.length == 2) {
    			sb.append(kv[0].trim() + "=");
    			String key = kv[0].trim();
    			String val = kv[1].trim();
    			val = StringUtil.trimLeadingCharacter(val, '\"');
    			val = StringUtil.trimTrailingCharacter(val, '\"');
    			
    			
    			//System.out.println(key + "---" + val);
    			if(exceptionSet.contains(key))
    				sb.append("\"" + val + "\"");
    			else { 
    				sb.append("\"");
    				sb.append(new String(EncodeUtils.encodeBase64(kv[1].getBytes())));
    				sb.append("\"");
    			}
    		}
    		else {
    			System.err.println("no = in it");
    			sb.append(kv[0]);
    		}
    		sb.append(" \n" );
    	}
    	String output = StringUtil.trimTrailingWhitespace(sb.toString());
    	return output + tailStr;
    }
    
    public static void main(String[] args) throws Exception {
    	
        try {
        	 String filename = "./doc/docshow.txt";
        	 File file = new File(filename);
        	 byte[] bytes = FileUtil.readFromFile(file);
        	 String str = ParamUtils.decode(new String(bytes));
        	 //System.out.println(str);
        	 FileUtil.writeToFile("./doc/docshow_decode.txt", str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
       	 String filename = "./doc/docshow_decode.txt";
       	 File file = new File(filename);
       	 byte[] bytes = FileUtil.readFromFile(file);
       	 String str = ParamUtils.encode(new String(bytes));
       	 //System.out.println(str);
       	 FileUtil.writeToFile("./doc/docshow_encode.txt", str.getBytes());
       } catch (IOException e) {
           e.printStackTrace();
       }
    }
}
