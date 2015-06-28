package com.github.walterfan.devaid.webmonitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * provide thread status for web monitor
 * 
 * @version 1.0 6/3/2008
 * @author Walter Fan Ya Min
 */
/**
 * @author walter
 */
public final class ThreadStatus {

    private ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();

    private static ThreadStatus instance = new ThreadStatus();

    private static final int MAX_DEPTH = Integer.MAX_VALUE;

    // added the below variables to reduce CPU rate if query frequently
    // Update interval of threadInfos,unit is millisecond
    private static final long QUERY_INTERVAL = 100;

    private long lastThreadInfoUpdateTime = 0;

    private ThreadInfo[] threadInfos = null;

    // added end by xiang,walter on 8/20/08

    private ThreadStatus() {

    }

    public static ThreadStatus getInstance() {
        return instance;
    }

    public long getThreadCount() {
        return threadBean.getThreadCount();
    }

    public long[] getThreadIds() {
        return threadBean.getAllThreadIds();
    }

    /**
     * @return ThreadInfo array
     */
    public synchronized ThreadInfo[] getThreadInfo() {
        // To reduce CPU rate if query frequently, revised by walter on 08/50/08
        if (threadInfos == null || (System.currentTimeMillis() - lastThreadInfoUpdateTime) > QUERY_INTERVAL) {
            long[] ids = getThreadIds();
            threadInfos = new ThreadInfo[ids.length];
            for(int i= 0; i< ids.length; i++) {
                threadInfos[i] = threadBean.getThreadInfo(ids[i]);
            }
            //threadInfos = threadBean.getThreadInfo()
            lastThreadInfoUpdateTime = System.currentTimeMillis();
        }

        return threadInfos;
    }

    /**
     * @param threadId Thread Id
     * @return Thread stack trace string
     */
    public String getStackTrace(long threadId) {
        ThreadInfo info = threadBean.getThreadInfo(threadId, MAX_DEPTH);
        return info.toString();
    }

    /**
     * @return all Thread stack trace string
     */
    public String getStackTrace() {
        StringBuffer sb = new StringBuffer();
        ThreadInfo[] infos = getThreadInfo();

        for (int i = 0; i < infos.length; i++) {
            sb.append(i + ". ");
            ThreadInfo info = infos[i];
            sb.append(info.toString() + "\n");
            /*
             * StackTraceElement[] bts = info.getStackTrace() ; for (int j = 0;
             * j < bts.length; j++) { StackTraceElement e = bts[j];
             * sb.append(e.toString()+"\n"); }
             */
        }
        return sb.toString();
    }

    /**
     * @param args command line
     */
    public static void main(String[] args) {
        ThreadStatus status = ThreadStatus.getInstance();
        ThreadInfo[] infos = status.getThreadInfo();
        System.out.println(status.getStackTrace(Thread.currentThread().getId()));
        System.out.println("---all thread info---");
        System.out.println(status.getStackTrace());

    }
}
