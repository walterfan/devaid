package com.github.walterfan.util;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
/**
* Convert date string from one time zone to another time zone
*
* @version 1.0 26 Feb 2008
* @author:<a href="mailto:walter.fan@gmail.com">Walter Fan</a>
*/
public final class TimeZoneConv {
	public static final TimeZone TZ_GMT = new SimpleTimeZone(0, "GMT");
	public static final TimeZone TZ_PRC = new SimpleTimeZone(0, "PRC");
	public static final TimeZone TZ_PST = TimeZone.getTimeZone("America/Los_Angeles");
	
	public static final Locale LC_USA = new Locale("en", "US");
	public static final Locale LC_PRC = new Locale("zh", "CN");
	
	public static final String DATE_FMT_ISO = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FMT_USA = "MM/dd/yyyy HH:mm:ss";
	
	public static final String TIME_FMT_ISO = "yyyy-MM-dd HH:mm:ss.SSS";
	public static final String TIME_FMT_USA = "MM/dd/yyyy HH:mm:ss.SSS";
	
	
	public static final long TIME_MILLSEC = 1;
	public static final long TIME_SEC = 1000;
	public static final long TIME_MIN = 60 * TIME_SEC;
	public static final long TIME_HALF_HOUR = 30 * TIME_MIN;
	public static final long TIME_HOUR = 60 * TIME_MIN;
	
	private TimeZoneConv() {
		
	}
	
/*
 * Convert date string from one time zone to another time zone
 * @param str date string , such as "2008-03-09 01:59:59"
 * @param fmt date format,  such as "yyyy-MM-dd HH:mm:ss"
 * @return date string , such as "2008-03-09 09:59:59"
 */
	public static String convTZ(String str, String fmt, TimeZone srcTZ,
			TimeZone destTZ) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(fmt, LC_USA);
		sdf.setTimeZone(srcTZ);
		Date date = sdf.parse(str);
		sdf.setTimeZone(destTZ);
		return sdf.format(date);
	}

	public static String convTZ4Time(String str, TimeZone srcTZ, TimeZone destTZ)
			throws Exception {
		return convTZ(str, TIME_FMT_ISO, srcTZ, destTZ);
	}

	public static String convTZ4Date(String str, TimeZone srcTZ, TimeZone destTZ)
		throws Exception {
		return convTZ(str, DATE_FMT_ISO, srcTZ, destTZ);
	}
	
	public static String convTime4PST2GMT(String str) throws Exception {
		return convTZ(str, TIME_FMT_ISO, TZ_PST, TZ_GMT);
	}

	public static String convTime4GMT2PST(String str) throws Exception {
		return convTZ(str, TIME_FMT_ISO, TZ_GMT, TZ_PST);
	}
	/*
	 * @deprecated you should convTZ to replace the method 
	 * @see convTZ
	 */
	public static long getTime4GMT2PST(long timestamp)	{	
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FMT_ISO, LC_USA);
		sdf.setTimeZone(TZ_GMT);
		//get the srcStr("2008-03-09 09:59:59") by the time stamp 
		String srcStr = sdf.format(new Date(timestamp));

		long destTime = timestamp;
		try {
			//srcStr("2008-03-09 09:59:59") --> destStr("2008-03-09 01:59:59"))
			String destStr = convTime4GMT2PST(srcStr);
			//destStr("2008-03-09 01:59:59") --> destTime
			Date destDate = sdf.parse(destStr);
			destTime = destDate.getTime();
		} catch (Exception e) {
			//System.out.println(e.getMessage());
		}
		return destTime;
	}

	/*
	 * @deprecated you should convTZ to replace the method 
	 * @see convTZ
	 */	
	public static long getTime4PST2GMT(long timestamp) {	
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_FMT_ISO, LC_USA);
		sdf.setTimeZone(TZ_GMT);
		//get the srcStr("2008-03-09 01:59:59") by the time stamp 
		String srcStr = sdf.format(new Date(timestamp));
		
		long destTime = timestamp;
		try {
			//srcStr("2008-03-09 01:59:59") --> destStr("2008-03-09 09:59:59"))
			String destStr = convTime4PST2GMT(srcStr);
			//destStr("2008-03-09 09:59:59") --> destTime
			Date destDate = sdf.parse(destStr);
			destTime = destDate.getTime();
		} catch (Exception e) {
			//System.out.println(e.getMessage());
		}
		return destTime;
	}


	public static void main(String[] args) {

		String[] arrPSTDate = new String[8];
		arrPSTDate[0] = "2008-03-09 01:59:59";
		arrPSTDate[1] = "2008-03-09 02:00:00";
		arrPSTDate[2] = "2008-03-09 02:00:01";
		arrPSTDate[3] = "2008-11-02 01:59:59";
		arrPSTDate[4] = "2008-11-02 02:00:00";
		arrPSTDate[5] = "2008-11-02 02:00:01";
		arrPSTDate[6] = "2008-11-02 00:00:01";
		arrPSTDate[7] = "2008-11-02 03:00:01";

		System.out.println("----------convTZ PST to GMT-----------------");

		String[] arrGMTDate = new String[8];
		try {
			for (int i = 0; i < arrPSTDate.length; i++) {
				String gmtstr = convTZ4Date(arrPSTDate[i], TZ_PST, TZ_GMT);
				System.out.println(arrPSTDate[i] + "-->" + gmtstr + "(GMT)");
			}
		} catch (Exception e) {
			System.out.println("convTZ error");
		}
		System.out.println("----------getTime4PST2GMT-----------------");
		for (int i = 0; i < arrPSTDate.length; i++) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT_ISO, LC_USA);
			sdf.setTimeZone(TZ_GMT);
			//get the srcStr("2008-03-09 01:59:59") by the time stamp 
			try {
				Date srcDate = sdf.parse(arrPSTDate[i]);
				long srcTime = srcDate.getTime();
				long destTime = getTime4PST2GMT(srcTime);
				Date destDate = new Date(destTime);
				arrGMTDate[i] = sdf.format(destDate);
				System.out.println(arrPSTDate[i] + "-->" + sdf.format(destDate) + "(GMT)");
			} catch (Exception e2) {
				System.out.println("getTime4PST2GMT error");
			}
		}
		System.out.println("----------convTZ GMT to PST-----------------");

		try {
			for (int i = 0; i < arrGMTDate.length; i++) {
				String pststr = convTZ4Date(arrGMTDate[i], TZ_GMT, TZ_PST);
				System.out.println(arrGMTDate[i] + "-->" + pststr + "(PST)");
			}
		} catch (Exception e) {
			System.out.println("convTZ error");
		}

		System.out.println("----------getTime4GMT2PST-----------------");
		for (int i = 0; i < arrGMTDate.length; i++) {
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FMT_ISO, LC_USA);
			sdf.setTimeZone(TZ_GMT);
			//get the srcStr("2008-03-09 01:59:59") by the time stamp 
			try {
				Date srcDate = sdf.parse(arrGMTDate[i]);
				long srcTime = srcDate.getTime();
				long destTime = getTime4GMT2PST(srcTime);
				Date destDate = new Date(destTime);
				System.out.println(arrGMTDate[i] + "-->" + sdf.format(destDate) + "(PST)");
			} catch (Exception e2) {
				System.out.println("getTime4GMT2PST error");
			}
		}
	}
}
