package com.github.walterfan.devaid.webmonitor;
/** 
* view interface of monitor module 
* 
* @version 1.0 2 June 2008 
* @author Walter Fan Ya Min 
*/
public interface WebRender {
	void setTplFile(String filename);
	/*
	 * merge monitor info with template
	 * @param monitor info contains a Map<String,Object>
	 * @return web response content
	 * 
	 */
	String render(MonitorInfo monitorInfo);
}
