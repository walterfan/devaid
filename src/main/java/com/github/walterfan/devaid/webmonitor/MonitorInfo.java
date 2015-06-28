package com.github.walterfan.devaid.webmonitor;
/** 
* information holder of monitor module 
* 
* @version 1.0 2 June 2008 
* @author Walter Fan Ya Min 
*/
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class MonitorInfo {
    private String contentType = "";
	private String content = "";
	private Map<String, Object> mapInfo = null;
	public MonitorInfo() {
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
	
	public String toString() {
		StringBuilder sb = new StringBuilder("");
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
        sb.append("<ul>\n");
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
    
    
}
