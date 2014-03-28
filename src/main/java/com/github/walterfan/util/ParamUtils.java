package com.github.walterfan.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import com.github.walterfan.util.db.ConnectionProvider;
import com.github.walterfan.util.db.DbConfig;
import com.github.walterfan.util.db.DbConn;
import com.github.walterfan.util.db.DbOperator;
import com.github.walterfan.util.db.DriverManagerProvider;
import com.mysql.jdbc.StringUtils;


public class ParamUtils {
    
    public static Map<String, String> parameterMap = new TreeMap<String, String>();

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
    			if(exceptionSet.contains(key)) {
    				sb.append("\"" + val + "\"");
    			    parameterMap.put(key, val);
    			}
    			else { 
    				sb.append("\"");
    				val = new String(EncodeUtils.decodeBase64(kv[1].getBytes()));
    				sb.append(val);
    				sb.append("\"");
    				parameterMap.put(key, val);
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
    	
        ConfigLoader cfgLoader = ConfigLoader.getInstance();
        try {
			cfgLoader.loadFromClassPath("devaid.properties");
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
    	
    	String filename = cfgLoader.get("file_name");
    	String sql_name = cfgLoader.get("sql_name");
        
    	try {
        	 
        	 File file = new File(filename);
        	 byte[] bytes = FileUtil.readFromFile(file);
        	 String str = ParamUtils.decode(new String(bytes));
        	 //System.out.println(str);
        	 FileUtil.writeToFile("./doc/docshow_decode.txt", str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        /*
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
       */

        writeDB(cfgLoader, sql_name, parameterMap);
        
    }

	public static void writeDB(ConfigLoader cfgLoader, String sql_name, Map<String,String> paraMap) {
		DbConfig dbCfg = new DbConfig(cfgLoader.get("db_driverClass"), cfgLoader.get("db_url"), 
        		cfgLoader.get("db_username"),cfgLoader.get("db_password"));

        
        String sql = cfgLoader.get(sql_name);
        System.out.println("url=" + cfgLoader.get("db_url"));
        System.out.println("sql=" + sql);
        
        ConnectionProvider provider = new DriverManagerProvider();
        provider.setDbConfig(dbCfg);
        DbConn dc = new DbConn(provider);

        try {
            if (dc.createConnection() == null) {
            	System.err.println("getConnection error, please check the specified jdbc parameters");
                return;
            }
            DbOperator dbOpt = new DbOperator(dc);
            int i = 1;
            for(Map.Entry<String, String> entry: paraMap.entrySet()) {
            	System.out.println("| " + i + " | " + entry.getKey()+ " | " + entry.getValue()+ " |" ) ;
            	//int ret = dbOpt.update(sql, entry.getKey(), entry.getValue());
            	//i+=ret;
            	i++;
            }
            
            dbOpt.commit();
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dc.closeConnection();
        }
	}
}
