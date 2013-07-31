package com.github.walterfan.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;


import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

import com.github.walterfan.util.EncodeUtils;


/**
 * @author Walter Fan Ya Min 
 *
 */
public class HttpUtil {
    public static final String HTML_CONTENT_TYPE = "text/html; charset=utf-8";
    public static final String XML_CONTENT_TYPE = "text/xml; charset=utf-8";
    public static final String DEFAULT_CHARSET = "UTF-8";
    
    private static Log logger = LogFactory.getLog(HttpUtil.class);
    private static Map<String,String> mimeTypes = new HashMap<String, String>();
    
    static {
    	mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("exe", "application/octet-stream");
	    mimeTypes.put("zip", "application/zip");
	    mimeTypes.put("doc", "application/msword");
	    mimeTypes.put("xls", "application/vnd.ms-excel");
	    mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
	    mimeTypes.put("gif", "image/gif");
	    mimeTypes.put("png", "image/png");
	    mimeTypes.put("jpg", "image/jpg");
	    mimeTypes.put("jpeg","image/jpg");
	    mimeTypes.put("mp3", "audio/mpeg");
	    mimeTypes.put("wav", "audio/x-wav");
	    mimeTypes.put("mpeg","video/mpeg");
	    mimeTypes.put("mpg", "video/mpeg");
	    mimeTypes.put("mpe", "video/mpeg");
	    mimeTypes.put("mov", "video/quicktime");
	    mimeTypes.put("avi", "video/x-msvideo");
	    mimeTypes.put("txt", "text/plain");
	    mimeTypes.put("log", "text/plain");
	    mimeTypes.put("csv", "text/plain");
	    mimeTypes.put("htm", "text/html");
	    mimeTypes.put("html", "text/html");
	    mimeTypes.put("xml", "text/xml");
    }
    
    private HttpUtil() {
        
    }
    
    public static String getMimeType(String fileExt) {
    	String mimeType =  mimeTypes.get(fileExt);
    	if(null == mimeType) {
    		return "application/force-download";
    	}
    	return mimeType;
    }
    
    public static void writeResponse(int status, String contentType,String content, HttpServletResponse response) throws IOException {
        OutputStream os = null;
        try {
            response.setStatus(status);
            response.setContentType(contentType);

            byte[] data = content.getBytes(DEFAULT_CHARSET);
            response.setContentLength(data.length);
            os = response.getOutputStream();
            if(os != null)
            	os.write(data);
        } finally {
            IOUtils.closeQuietly(os);

        }

    }
    
    public static Map<String,String> getMapFromParameters(String paramters) {
        Map<String,String> paraMap = new HashMap<String,String>();
        if(StringUtils.isNotEmpty(paramters)) {
            String[] strArr = paramters.split("&");      
            for (String para : strArr) {     
                if(para != null && StringUtils.contains(para, '=')) {
                    int pos = para.indexOf('=');
                	if(pos < 0) {
                		paraMap.put(para, "");
                	} else {
                        String content = StringUtils.substring(para, pos+1);
                        content = EncodeUtils.urlDecode(content);
                        paraMap.put(StringUtils.trim(StringUtils.substring(para, 0,pos)), content);
                        
                    } 
                }
            }  
        }
        return paraMap;
    }
    
    public static byte[] makeHttpHeader(int statusCode, String contentType, long contentLength)
        throws UnsupportedEncodingException {
        return makeHttpHeader(statusCode, contentType, contentLength, null);
    }
    
    public static byte[] makeHttpHeader(int statusCode, String contentType, long contentLength, List<String> fields)
    throws UnsupportedEncodingException {
	    StringBuilder sb = new StringBuilder("HTTP/1.0 " + statusCode + " " + HttpStatus.getStatusText(statusCode));
	    sb.append("\r\n");
	    sb.append("Server: Walter Fan 1.0");     	
	    sb.append("\r\n");
	    if(null != fields) {
		    for(String str: fields) {
		    	sb.append(str);
		    	sb.append("\r\n");
		    } 	    
	    }
	    sb.append("Content-length: " + contentLength);
	    sb.append("\r\n");
	    sb.append("Content-type: " + contentType + "\r\n\r\n");
	    return sb.toString().getBytes("UTF-8");
}
    
    public String readInputStream(InputStream in) throws IOException {
    	if(null == in) {
    		return "";
    	}
    	StringBuilder sb = new StringBuilder("");
		while (true) {
			int c = in.read();
			if (c == '\r' || c == '\n' || c == -1)
				break;
			sb.append((char) c);

		}
		return sb.toString();
    }
    
    public static void main(String[] args) {
    	Map<String,String> map = getMapFromParameters("Authorization=Basic YWRtaW46cGFzcw==");
    	for(Map.Entry<String, String> entry: map.entrySet()) {
    		System.out.println(entry.getKey() + "=" + entry.getValue());
    	}
    }
    
    //need not close it because it's not open it
	public static String stream2String(InputStream is) {
		if(null == is) {
    		return "";
    	}
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));

			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (Exception e) {
			logger.error("stream2String read error ", e);
		} 

		return sb.toString();
	}
	
	public static String getValueByName(String text, String name) {
		int pos = StringUtils.indexOf(text, name);
		if(pos == -1) {
			return null;
		}
		int pos1 = StringUtils.indexOf(text, '=', pos);
		if(pos1 == -1 || pos1 > pos + name.length()) {
			return null;
		}
		int pos2 = StringUtils.indexOf(text, "&", pos1);
		if(-1 == pos2)
			pos2 = StringUtils.indexOf(text, " ", pos1);
		if(-1 == pos2)
			pos2 = StringUtils.indexOf(text, "\r", pos1);
		if(-1 == pos2)
			pos2 = StringUtils.indexOf(text, "\n", pos1);
		
		if(pos2 == -1) {
			return StringUtils.substring(text, pos1+1);
		} else {
			return StringUtils.substring(text, pos1+1, pos2);
		}
	}
	
	
	public static String getStringFromXmlResponse(String url, Map<String, String> headMap, String xmlpath) {
		HttpClientWrapper client = new HttpClientWrapper(); 
        
        try {
            HttpResponse response = client.doGet(url, null, headMap);
            String xmlText = response.getBody();
            Document document = DocumentHelper.parseText(xmlText);
            Node node = document.selectSingleNode(xmlpath);
            return node.getText();            
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
	}
}
