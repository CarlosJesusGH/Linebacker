package com.cmsys.linebacker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;


public class DateUtils {

	
	public static String getDateString(Date pDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String date = sdf.format(pDate);
		return date;
	}
	
	public static String getDateTimeString(Date pDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		String date = sdf.format(pDate);
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
	
	static public String customFormatCasagri(String pDate){
		Date parsed = new Date();
		SimpleDateFormat sdfStandard = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		SimpleDateFormat sdfCasagri = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		try{		    
		    parsed = sdfStandard.parse(pDate);
		}catch(ParseException pe){
			Log.e("ERROR", "throw new IllegalArgumentException()");
			return "";		    
		}
		return sdfCasagri.format(parsed);		
	}
	
	static public String customFormatCasagri(Date pDate){
		SimpleDateFormat sdfCasagri = new SimpleDateFormat("dd-MM-yyyy", Locale.US);		
		return sdfCasagri.format(pDate);		
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
}
