package com.github.walterfan.server.logger;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.commons.lang.builder.ToStringBuilder;


public class LogData implements Externalizable{

	private static final long serialVersionUID = -3786963763942972033L;
	
	private String logType;
	private long offset;
	private long timestamp;
	private String content;
	
	public String getLogType() {
		return logType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((logType == null) ? 0 : logType.hashCode());
		result = prime * result + (int) (offset ^ (offset >>> 32));
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LogData))
			return false;
		LogData other = (LogData) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (logType == null) {
			if (other.logType != null)
				return false;
		} else if (!logType.equals(other.logType))
			return false;
		if (offset != other.offset)
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	public void setLogType(String aType) {
		this.logType = aType;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int calcChecksum() {

		return this.getLogType().getBytes().length 
		+ (int)(this.getOffset() + this.getTimestamp()) 
		+ this.getContent().getBytes().length;
	}
	
	public int calcLength() {
		return this.getLogType().getBytes().length + 8 + 8 
			   + this.getContent().getBytes().length;
	}
	
/*	public void serialize(ByteBuffer buf) {
		byte[] logTypeBytes = this.logType.getBytes(); 
		buf.putInt(logTypeBytes.length);
		buf.put(logTypeBytes);
		buf.putLong(this.offset);
		buf.putLong(this.timestamp);
		byte[] contentBytes = this.content.getBytes();
		buf.putInt(contentBytes.length);
		buf.put(contentBytes);
	}*/
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this); 
	}

	public void readExternal(ObjectInput buf) throws IOException,
			ClassNotFoundException {

		int typeLen = buf.readInt();
		if(typeLen > 0) {
			byte[] bytes = new byte[typeLen];		
			buf.read(bytes, 0, typeLen);
			this.setLogType(new String(bytes));
		}
		this.setOffset(buf.readLong());
		this.setTimestamp(buf.readLong());
		
		int contentLen = buf.readInt();
		if(contentLen > 0 ) {
			byte[] contentBytes = new byte[contentLen];
			buf.read(contentBytes, 0, contentLen);
			this.setContent(new String(contentBytes));
		}
		
	}

	public void writeExternal(ObjectOutput buf) throws IOException {

		if(null==this.logType) {
			buf.writeInt(0);
		} else {			
			byte[] logTypeBytes = this.logType.getBytes(); 
			buf.writeInt(logTypeBytes.length);
			buf.write(logTypeBytes);
		}
		
		buf.writeLong(this.offset);
		buf.writeLong(this.timestamp);
		
		if(null == this.content) {
			buf.writeInt(0);
		} else {
			byte[] contentBytes = this.content.getBytes();
			buf.writeInt(contentBytes.length);
			buf.write(contentBytes);
		}
	}
}
