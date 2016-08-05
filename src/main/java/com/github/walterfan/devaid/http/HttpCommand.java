package com.github.walterfan.devaid.http;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * @author walter
 *
 */
public class HttpCommand implements Comparable<HttpCommand> {
	
	private String method = "GET";
	
	private String name;

	private Map<String, String> paraMap;

	private String header;
	
	private String request;
	
	private String response;
	
	private String url;

	public HttpCommand() {
		
	}
	
	public HttpCommand(String strHost, String strHeader, String strRequest, String methodName) {
		this.url = strHost;
		this.header = strHeader;
		this.request = strRequest;
		this.method = methodName;
	}

	public HttpCommand(String strHost, String strRequest, String methodName) {
		this.url = strHost;
		this.request = strRequest;
		this.method = methodName;
	}
	
	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	public String getMethod() {
		return method;
	}

	public String getName() {
		return name;
	}
	
	public Map<String, String> getParaMap() {
		return paraMap;
	}
	
	public String getRequest() {
		return request;
	}

	public String getResponse() {
		return response;
	}

	public String getUrl() {
		return url;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setParaMap(Map<String, String> paraMap) {
		this.paraMap = paraMap;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	public boolean equals(Object object) {   
        return EqualsBuilder.reflectionEquals(this, object);   
    }   
           
    public int hashCode(){   
        return HashCodeBuilder.reflectionHashCode(this);   
    }   
           
    public String toString(){   
        return ToStringBuilder.reflectionToString(this);   
    }   
	
    public void parseUrl() {
    	if(StringUtils.contains(url, '?')) {
            int pos = url.indexOf('?');
            if(StringUtils.isBlank(request)) {
            	request = "";
            } else {
            	request += "&"; 
            }
            request += StringUtils.substring(url, pos + 1);
            url = StringUtils.substring(url, 0, pos);
        }
    }

	public int compareTo(HttpCommand o) {
		return this.name.compareTo(o.getName());
	}

}
