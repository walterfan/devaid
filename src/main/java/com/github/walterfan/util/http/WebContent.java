package com.github.walterfan.util.http;
/** 
* information holder of http request 
* 
* @version 1.0 2 June 2008 
* @author Walter Fan Ya Min 
*/
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;



public class WebContent  {
	
    private String contentType = "";
	private String content = "";
	private String pathInfo = "";
	
	private Map<String, Object> mapHeader = null;
	private Map<String, Object> mapInfo = null;
	
	public WebContent() {
		this.mapHeader = new HashMap<String, Object>();
		this.mapInfo = new HashMap<String, Object>();
	}
	
	public void putInfo(String key, Object val) {
		this.mapInfo.put(key, val);
	}
	
	public Object getInfo(String key) {
		return this.mapInfo.get(key);
	}
	
	public Map<String, Object> getMapInfo() {
		return this.mapInfo;
	}
	
	public Map<String, Object> getHeaders() {
		return this.mapInfo;
	}
	
	public void putHeader(String key, Object val) {
		this.mapHeader.put(key, val);
	}
	
	public Object getHeader(String key) {
		return this.mapHeader.get(key);
	}
	

	
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		//sb.append("Path: "+ this.getPathInfo() +"\n");
		sb.append("Headers:\n");
		
		for(Map.Entry<String, Object> entry: mapHeader.entrySet()) {
			sb.append(entry.getKey() + " = " + entry.getValue() + "\n");
		}
		
		sb.append("Parameters:\n");
		
		for(Map.Entry<String, Object> entry: mapInfo.entrySet()) {
			sb.append(entry.getKey() + " = " + entry.getValue() + "\n");
		}
		if(StringUtils.isNotEmpty(content)) {
		    sb.append("contentType=" + contentType+ "\n");
		    sb.append("content=" + content + "\n");
		}
		return sb.toString();
	}
	
	public String toHtmlString() {
		StringBuilder sb = new StringBuilder("");
		//sb.append("Path: "+ this.getPathInfo() +"\n<br>");
		sb.append("<ul>Headers:\n");
		for(Map.Entry<String, Object> entry: mapHeader.entrySet()) {
			 sb.append("<li>" + entry.getKey());
	         sb.append(": " + entry.getValue() + "</li>\n");
		}
		sb.append("</ul>");
		
        sb.append("<ul>Parameters:\n");
        for(Map.Entry<String, Object> entry: mapInfo.entrySet()) {
            sb.append("<li>" + entry.getKey());
            sb.append(": " + entry.getValue() + "</li>\n");
        }
        if(StringUtils.isNotEmpty(content)) {
            sb.append("<li>contentType=" + contentType + "</li>\n");
            sb.append("<li>content=" + content + "</li>\n");
        }
        sb.append("</ul>");
        return sb.toString();
    }

    
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    
    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    
    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    
    /**
     * @param contentType the contentType to set
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

	public String getPathInfo() {
		return pathInfo;
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}
    
	
	public static WebContent createWebContent(HttpServletRequest request) throws IOException {
        WebContent queryInfo = new WebContent();
        queryInfo.setPathInfo(request.getPathInfo());
        
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while(headerNames.hasMoreElements()){
        	String headerName = (String) headerNames.nextElement();
        	queryInfo.putHeader(headerName, request.getHeader(headerName));
        }
        
        Map<String, String[]> map = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String[] arrPara = entry.getValue();
            if(ArrayUtils.isEmpty(arrPara)) {
                continue;
            }
            if(arrPara.length==1) {
                queryInfo.putInfo(entry.getKey(), arrPara[0]);
            } else {
                queryInfo.putInfo(entry.getKey(), arrPara);
            }
        }
        
        int contentLength = request.getContentLength();
        if( contentLength > 0) {
            queryInfo.setContentType(request.getContentType());
            InputStream is = request.getInputStream();
            byte[] bytes = IOUtils.toByteArray(is);
            queryInfo.setContent(new String(bytes));
            IOUtils.closeQuietly(is);
        }
        return queryInfo;
    }
}
