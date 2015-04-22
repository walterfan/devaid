package com.github.walterfan.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.walterfan.server.AbstractServer;





public class TaskRunner extends AbstractServer implements Runnable {
	private static Log logger = LogFactory.getLog(TaskRunner.class);
	private final BlockingQueue<Callable<Integer>> taskQueue;
	private final ExecutorService executor;
	private volatile boolean stopRequested;
	
	public TaskRunner(int nThreads, int nCapacity) {
		this.taskQueue = new PriorityBlockingQueue<Callable<Integer>>(nCapacity);
		this.executor = Executors.newFixedThreadPool(nThreads);
	}


	public void run() {
		while(!stopRequested) {
			try {
				Callable<Integer> task = taskQueue.poll(5, TimeUnit.MILLISECONDS);
				int ret = task.call();
				if(0 != ret) {
					//error handler
					logger.error("task call error");
				}
			} catch (InterruptedException e) {
				logger.error(e);
				continue;
			} catch (Exception e) {
				logger.error("task run error: ", e);
				continue;
			}
		}
		
	}

	@Override
	protected void onStart() throws Exception {
		this.stopRequested = false;
		this.executor.execute(this);
		
	}

	@Override
	protected void onStop() throws Exception {
		this.stopRequested = true;
		this.executor.shutdown();
		
	}
	
	public void addTask(Callable<Integer> aTask) {
		this.taskQueue.add(aTask);
	}
	
	public int remainingCapacity() {
		return this.taskQueue.remainingCapacity();
	}
	
	public boolean isEmpty() {
		return this.taskQueue.isEmpty();
	}
	
	public static void main(String[] args) throws Throwable {
		
	}
}
