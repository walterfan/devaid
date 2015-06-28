package com.github.walterfan.devaid.webmonitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/** 
*  time keeper : begin time, end time
* 
* @version 1.0 6/3/2008 
* @author Walter Fan Ya Min 
*/
public class TimeKeeper {
	private Long beginTime = null;
	private Long endTime = null;
	private static final long DURATION_DAY = 24 * 60 * 60 * 1000;
	private static Log logger = LogFactory.getLog(TimeKeeper.class);
	public TimeKeeper() {
		beginTime = System.currentTimeMillis();
	}
	
	public long getDuration() {
		if (endTime == null) {
			return 0;
		} else {
			return endTime - beginTime;
		}
	}
	
	public String getDurationStr() {	
		return getDurationStr(getDuration());		
	}
	
	public static String getDurationStr(long beginTime, long endTime) {
		return getDurationStr(endTime - beginTime);
	}
	
	public String getDurationSec() {
		long duration = getDuration();
		if (duration <= 0) {
			return "0";
		} else {
			long sec = duration/1000;
			long ms = duration - sec*1000;
			if (ms == 0){
				return String.valueOf(sec);
			} else {
				String strMS="00"+ms;
				return sec + "."+ strMS.substring(strMS.length()-3,strMS.length());
			} 
		}
	}
	
	public static String getDurationStr(long duration) {
		StringBuilder sb = new StringBuilder("");
		long ms = Math.abs(duration);
		if (duration > DURATION_DAY) {
			long days = duration / DURATION_DAY;							
			sb.append(days + " day(s) ");			
			ms = ms - days * DURATION_DAY;
		}
		
		try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            sb.append(format.format(new Date(ms)));
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
        }

		return sb.toString();
	}
	
	public void begin() {
		beginTime = System.currentTimeMillis();		
	}
	
	public void end() {
		endTime = System.currentTimeMillis();
	}

	public Long getBeginTime() {
		return beginTime;
	}


	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
			logger.error(ie.getMessage());
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TimeKeeper counter = new TimeKeeper();
		TimeKeeper.sleep(3000);
		System.out.println("duration: " + counter.getDurationStr());
		counter.begin();
		TimeKeeper.sleep(2000);	
		counter.end();
		TimeKeeper.sleep(1000);
		System.out.println("duration: " + counter.getDurationStr());
		System.out.println("duration: " + TimeKeeper.getDurationStr(Long.valueOf("1864000001")));
	}
}
