package com.cmsys.linebacker.util;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;


public class DateUtils {

	
	public static String getDateString(Date pDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String date = sdf.format(pDate);
		return date;
	}

	public static String getDateString(Long pTimeStamp){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(pTimeStamp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String date = sdf.format(calendar.getTime());
		return date;
	}
	
	public static String getDateTimeString(Date pDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		String date = sdf.format(pDate);
		return date;
	}

	public static String getDateTimeString(Long pTimeStamp){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(pTimeStamp);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		String date = sdf.format(calendar.getTime());
		return date;
	}
	
	public static Date getDateFromString(String pStringDate){
		Date parsed = new Date();
		SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		try{		    
		    parsed = sdfStandard.parse(pStringDate);
		}catch(ParseException pe){
		    throw new IllegalArgumentException();
		}
		return parsed;
	}
	
	public static double getDaysDiff(Date pStart, Date pEnd){
		return (pEnd.getTime() - pStart.getTime()) / (24 * 60 * 60 * 1000);
	}
	
	public static Date getNow(){
		return new Date();
	}

	static public String getStandardFormat(String pCurrentDate, String pCurrentFormat){
		Date parsed = new Date();
		SimpleDateFormat sdfCurrent = new SimpleDateFormat(pCurrentFormat, Locale.US);
		SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		try{		    
		    parsed = sdfCurrent.parse(pCurrentDate);
		}catch(ParseException pe){
		    throw new IllegalArgumentException();
		}
		return sdfStandard.format(parsed);		
	}

    static public String getCustomFormat(String pCurrentDate, String pCurrentFormat, String pNewFormat){
        Date parsed = new Date();
        SimpleDateFormat sdfCurrent = new SimpleDateFormat(pCurrentFormat, Locale.US);
        SimpleDateFormat sdfStandard = new SimpleDateFormat(pNewFormat, Locale.US);
        try{
            parsed = sdfCurrent.parse(pCurrentDate);
        }catch(ParseException pe){
            throw new IllegalArgumentException();
        }
        return sdfStandard.format(parsed);
    }

	public static String getTimeString(Long pTimeStamp){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(pTimeStamp);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US);
		String date = sdf.format(calendar.getTime());
		return date;
	}

	public static Calendar resetTime(Calendar calendar) {
		calendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		return calendar;
	}

	public static Date getTodayAt0000() {
		Calendar calendar = Calendar.getInstance();
		calendar = resetTime(calendar);
		return calendar.getTime();
	}

	public static Date getYesterdayAt0000() {
		Calendar calendar = Calendar.getInstance();
		calendar = resetTime(calendar);
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

	public static Date getThisWeekStart() {
		Calendar calendar = Calendar.getInstance();
		calendar = resetTime(calendar);
		//calendar.getFirstDayOfWeek();
		calendar.set(Calendar.DAY_OF_WEEK, 1);
		return calendar.getTime();
	}

	public static Date getThisMonthStart() {
		Calendar calendar = Calendar.getInstance();
		calendar = resetTime(calendar);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public static boolean isDateToday(Date testDate) {
		return isDateInRange(testDate, getTodayAt0000(), getNow());
	}

	public static boolean isDateYesterday(Date testDate) {
		return isDateInRange(testDate, getYesterdayAt0000(), getTodayAt0000());
	}

	public static boolean isDateInRange(Date testDate, Date startDate, Date endDate) {
		//return startDate.compareTo(testDate) * testDate.compareTo(endDate) > 0;
		//return testDate.after(startDate) && testDate.before(endDate);     // This way doesn't include limits
		return !(testDate.before(startDate) || testDate.after(endDate));
	}
}
