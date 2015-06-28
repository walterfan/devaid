package com.github.walterfan.devaid.webmonitor;


import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.sql.SQLException;

/**
 * provide server information by ServerInfo class
 * @version 1.0 6/3/2008
 * @author:<a href="mailto:walter.fan@gmail.com">Walter Fan</a>
 */
public class ServerInfoMonitor extends BaseWebMonitor {
    /**
     * DOCUMENT ME!
     */
    private RuntimeMXBean runMbean = ManagementFactory.getRuntimeMXBean();

    private MemoryMXBean memMbean = ManagementFactory.getMemoryMXBean();
      /**
     * Creates a new ServerInfoMonitor object.
     * @throws SQLException 
     */
    public ServerInfoMonitor() throws IOException {
        super();   	
    }

    /**
     * DOCUMENT ME!
     * @param query DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    @Override
    public MonitorInfo queryMonitorInfo(MonitorInfo queryInfo) {
    	MonitorInfo monitorInfo = new MonitorInfo();
    	monitorInfo.putInfo("version", getVersion());
    	monitorInfo.putInfo("workdir", getWorkDir());
    	monitorInfo.putInfo("uptime",  runMbean.getUptime());
    	monitorInfo.putInfo("startTime",  runMbean.getStartTime());
    	monitorInfo.putInfo("memoryHeap",  memMbean.getHeapMemoryUsage());
    	monitorInfo.putInfo("memoryNonHeap",  memMbean.getNonHeapMemoryUsage());
    	
        return monitorInfo;
    }

    /**
     * DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    private String getVersion() {
        return "1.0";
    }

    /**
     * DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    private String getWorkDir() {
        File f = new File("./");

        return f.getAbsoluteFile().getParent();
    }

}