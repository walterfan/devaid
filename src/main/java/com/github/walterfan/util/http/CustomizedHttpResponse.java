package com.github.walterfan.util.http;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class CustomizedHttpResponse extends HttpResponse 
	implements Iterable<String>, Cloneable {
	private String contentType = "text/html";
	private int statusCode = 200;
	private List<String> fields = new LinkedList<String>();

	public void addField(String value) {
		this.fields.add(value);
	}
	
	
	/*public void putHeader(String pair) {int len = StringUtils.length(pair);
	if(len < 2) {
		return;
	}
	int pos = StringUtils.indexOf(pair, ':');
	if(pos < 1) {
		return;
	}
	this.fields.put(StringUtils.substring(pair, 0, pos), StringUtils.substring(pair, pos+1,len));
}*/
	
	public List<String> getFields() {
		return fields;
	}


	public void setFields(List<String> fields) {
		this.fields = fields;
	}


	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer(100);
		buf.append("statusCode:");
		buf.append(this.statusCode);
		buf.append(", contentType:");
		buf.append(this.contentType);
		buf.append(", content:");
		buf.append(this.getBody());

		return buf.toString();
	}

	public Iterator<String> iterator() {
		return this.fields.iterator();
	}
	
	public Object clone() throws CloneNotSupportedException{		
		return super.clone();
	}
	
}
