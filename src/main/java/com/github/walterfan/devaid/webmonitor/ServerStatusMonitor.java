package com.github.walterfan.devaid.webmonitor;
import java.lang.management.ThreadInfo;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
*  provide server status for web monitor
* 
* @version 1.0 6/3/2008 
* @author:<a href="mailto:walter.fan@gmail.com">Walter Fan</a> 
*/

public class ServerStatusMonitor extends BaseWebMonitor {
	
    private static Log logger = LogFactory.getLog(ServerStatusMonitor.class);
    
	private ThreadStatus threadStatus = ThreadStatus.getInstance();
	
	private FixedList<TimingEvent> serverEvents = null;
	
	private static ServerStatusMonitor instance = new ServerStatusMonitor();
	
	private ServerStatusMonitor() {
		serverEvents = new FixedList<TimingEvent>();
				
	}
	
	public static ServerStatusMonitor getInstance() {
		return instance;
	}
	
	@Override
	public MonitorInfo queryMonitorInfo(MonitorInfo queryInfo) {				
		MonitorInfo monitorInfo = new MonitorInfo();		
		
		//Thread info
		ThreadInfo[] threadArray = threadStatus.getThreadInfo();
		if (threadArray != null) {
			monitorInfo.putInfo("threadArray", threadArray);
			logger.debug("ServerStatusMonitor threadArray length=" + threadArray.length);
		}

		
		// Events
        if(serverEvents != null && serverEvents.size() != 0) {            
            int pages = 0 ;
            int totalSize = serverEvents.size();
            int subSize = serverEvents.getSubSize();
            
            if (totalSize % subSize == 0) {
				pages = totalSize / subSize;
			} else {
				pages = totalSize / subSize + 1;
			}
            
            monitorInfo.putInfo("pages", pages);
            int pagenum = getPagenum(queryInfo);
            monitorInfo.putInfo("currentPage", pagenum + 1);
            List<TimingEvent> list = serverEvents.getSubList(pagenum);
            if( list != null) {
                monitorInfo.putInfo("serverEvents", list);
                logger.debug("serverEvents size = " + list.size());
            }
        }
        
		if(logger.isDebugEnabled()) {
			logger.debug("monitorInfo = " + monitorInfo.toString());
		}
		return monitorInfo;

	}
	
	public void addServerEvent(TimingEvent event) {
		serverEvents.addElement(event);
	}
	
	public void addEvent(String message) {
		TimingEvent ev = new TimingEvent();
		ev.setName("normal");
		ev.setMessage(message);
		addServerEvent(ev);
	}
	
	public void addErrEvent(String message) {
		TimingEvent ev = new TimingEvent();
		ev.setName("error");
		ev.setMessage(message);
		addServerEvent(ev);
	}
	public void clear( ){
		serverEvents.clear();
	}
}
