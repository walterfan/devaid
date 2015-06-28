
package com.github.walterfan.devaid.webmonitor;



/** 
* model interface of monitor module 
* 
* @version 1.0 2 June 2008 
* @author <a href="mailto:walter.fan@gmail.com">Walter Fan</a> 
*/
public interface WebMonitor {
	/*
	 * query needed information for web monitor page
	 * @param URI query string
 	 * @return MonitorInfo, a map wrapper contains data 
	 */
	//MonitorInfo queryMonitorInfo(String query);	
	
	MonitorInfo queryMonitorInfo(MonitorInfo queryInfo);    

}
