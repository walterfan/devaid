package com.github.walterfan.server.logger.message;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.github.walterfan.server.logger.LogData;


public class LogMessage extends AbstractMessage 
	implements Serializable, Iterable<LogData> {

	private static final long serialVersionUID = -3786963763942972033L;

	List<LogData> logList = new LinkedList<LogData>();

	public LogMessage() {
		
	}
	
	public void add(LogData log) {
		this.logList.add(log);
	}
	
	public int size() {
		return this.logList.size();
	}
	
	public List<LogData> getLogList() {
		return logList;
	}
	public void setLogList(List<LogData> logList) {
		this.logList = logList;
	}


	public Iterator<LogData> iterator() {
		return this.logList.listIterator();
	}
	
	public int calcCheckSum() {
		Iterator<LogData> it = iterator();
		int checkSum = 0;
		while(it.hasNext()) {
			LogData data = it.next();
			checkSum += data.getLogType().getBytes().length 
			+ (int)(data.getOffset() + data.getTimestamp()) 
			+ data.getContent().getBytes().length; 
		}
		return checkSum;
	}
	
	public int calcLength() {

		Iterator<LogData> it = iterator();
		int length = 4; //4 bytes for "size" field
		while(it.hasNext()) {
			LogData data = it.next();
			length += 4; //4 bytes for "checksum" field
			//add additional 2 bytes for every String field
			length += 2 + data.getLogType().getBytes().length + 16 
			   + 2 + data.getContent().getBytes().length;
		}
		return length;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
