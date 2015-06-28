package com.github.walterfan.devaid.webmonitor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/** 
*  event with time info
* 
* @version 1.0 6/3/2008 
* @author Walter Fan Ya Min 
*/
public class TimingEvent extends TimeKeeper implements Serializable {
	
	static final long serialVersionUID = 6276592488221644334L;	

	private String message = "";
	private String name = "normal";
	
	public TimingEvent() {
		super();
	}
	
	
	/**
	 * @param beginTime
	 * @param endTime
	 * @param message
	 * @param eventName
	 */
	public TimingEvent(long beginTime, long endTime, String message,
			String eventName) {
		super();
		setBeginTime(beginTime);
		setEndTime(endTime);
		setMessage(message);
		setName(eventName);
	}

	public String getMessage() {
		return message;
	}
	
	public void setErrMessage(String message) {
		this.name = "error";
		this.message = message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	} 
	
	public void end(String message) {
		super.end();
		setMessage(message);
	}
	
	public String toString() {
		return this.getBeginTime() 
		+ "(" + super.getDurationSec()
		+ ") [" + this.name 
		+ "] " + this.message;
	}
}
