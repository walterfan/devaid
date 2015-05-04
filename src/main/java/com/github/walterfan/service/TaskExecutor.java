package com.github.walterfan.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.server.AbstractServer;
import com.github.walterfan.server.IServer;

/**
 * @author walter
 * 
 */
public class TaskExecutor extends AbstractServer implements IServer, Runnable {
    private static Log logger = LogFactory.getLog(TaskExecutor.class);
    private volatile boolean stopRequest = false;
    private ExecutorService executor = Executors
            .newSingleThreadScheduledExecutor();
    private Runnable task;
    private long interval;

    public TaskExecutor(Runnable task, long interval) {
        this.task = task;
        this.interval = interval;
    }

    public void onStart() throws Exception {

        executor.execute(this);

    }

    public void onStop() throws Exception {

        this.stopRequest = true;
        executor.shutdown();

    }

    public void run() {
        while (!stopRequest) {
            try {
                task.run();
                TimeUnit.MILLISECONDS.sleep(interval);
            } catch (InterruptedException e) {
                logger.error("Interrupt: ", e);
            }
        }
    }

}
