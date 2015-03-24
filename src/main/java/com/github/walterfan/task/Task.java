package com.github.walterfan.task;

import java.util.concurrent.Callable;

import com.github.walterfan.devaid.domain.Command;

public class Task extends Command implements Comparable<Task>, Callable<Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5027623069271486151L;
	private long startTime;
	private long endTime;
	private int estimatedDuration;
	private long deadline;
	private String description;
	private int priority;
	private Callable<Integer> handler;
	
	
	
	public Task() {
		super();
	}

	public Task(Callable<Integer> handler) {
		super();
		this.handler = handler;
	}

	private int compare(long a, long b) {
		if(a>b)  return 1;
		else if(a<b) return -1;
		else return 0;
	}
	

	public int compareTo(Task other) {
		int ret = compare(this.priority, other.getPriority());
		if(ret != 0) return ret;
		
	    ret = compare(this.deadline, other.getDeadline());
		if(ret != 0) return ret;
		
		ret = compare(this.startTime, other.getStartTime());
		if(ret != 0) return ret;
		
		ret = compare(this.estimatedDuration, other.getEstimatedDuration());
		if(ret != 0) return ret;
		
		return 0;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public int getEstimatedDuration() {
		return estimatedDuration;
	}

	public void setEstimatedDuration(int estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}

	public long getDeadline() {
		return deadline;
	}

	public void setDeadline(long deadline) {
		this.deadline = deadline;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}


	public Integer call() throws Exception {
		if(null != handler) {
			return handler.call();
		}
		return 0;
	}

	public Callable<Integer> getHandler() {
		return handler;
	}

	public void setHandler(Callable<Integer> handler) {
		this.handler = handler;
	}


	
	
	
	
}
