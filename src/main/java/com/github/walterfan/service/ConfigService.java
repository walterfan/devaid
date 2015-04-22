package com.github.walterfan.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ConfigService implements Configurable, IService {
    private static final Log logger = LogFactory.getLog(ConfigService.class);
    
    private static final String myName = "configService";
    
    private volatile boolean startFlag = false;

    private volatile boolean stopRequest = false;
    
    //private ExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    
    //@UseCase(id = 101, description = "reload configuraton information")
    public void reconfig() {
        logger.debug("--- reconfig ---");
    }

    /**
     * @throws Exception
     */
    public void stop() throws Exception {
        if (!startFlag) {
            return;
        }
        this.stopRequest = true;
        logger.debug("--- stopped ---");
        startFlag = false;
    }

    /**
     * @throws Exception
     */
    public void start() throws Exception {
        if (startFlag) {
            return;
        }
        //exec.execute(this);
        logger.debug("--- started ---");
        startFlag = true;

    }

    /**
     * @return
     */
    public boolean isStarted() {
        return startFlag;
    }

    /**
     * @return
     */
    public String getName() {
        return myName;
    }

    /**
     * @throws Exception
     */
    public void init() throws Exception {
        logger.debug("--- init ---");

    }

    /**
     * @throws Exception
     */
    public void clean() throws Exception {
        logger.debug("--- clean ---");

    }


    
    /*public void run() {
        while (!stopRequest) {

            logger.debug("--- run ---");
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                continue;
            }
        }
        
        
    }*/

}
