package com.github.walterfan.devaid.webmonitor;

/** 
* Web Monitor base class 
* 
* @version 1.0 2 June 2008 
* @author Walter Fan Ya Min 
*/
public class BaseWebMonitor implements WebMonitor {
	
	//protected MonitorInfo monitorInfo = null;	
	private static final String PAGE_NUM = "pagenum";
	
	public BaseWebMonitor() {
		//monitorInfo =  new MonitorInfo();
	}
	/*
	 * query needed information for web monitor page
	 * @param URI query string
 	 * @return MonitorInfo, a map wrapper contains data 
 	 * @see cn.fanyamin.appserver.webservice.WebMonitor#queryMonitorInfo(java.lang.String)
	 */
	
	public MonitorInfo queryMonitorInfo(MonitorInfo queryInfo){
	    return queryInfo;
	}
	
	
	public void clear() {
		
	}
	/*
	 * parse query string to get page number
	 */
	protected int getPagenum(MonitorInfo queryInfo) {
		int pagenum = 0;
		
		if(queryInfo == null) {
			return pagenum;
		}
	
		String strPagenum = (String)queryInfo.getInfo(PAGE_NUM);
		if(strPagenum == null) {
			return pagenum;
		}
			
		try {
			pagenum = Integer.valueOf(strPagenum); 
		} catch(NumberFormatException ne) {
			pagenum = 0;
		}
		
		return pagenum;
	}
}
