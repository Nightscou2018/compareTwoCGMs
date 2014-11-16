package com.mltkm.mongo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class Util {

	public static long getPositiveNumber(Object value){
		if(value == null){
			return(-1);
		}
		if(value instanceof java.lang.Integer){
			return(((Integer)value).intValue());
		}else if(value instanceof java.lang.Long){
			return(((Long)value).longValue());
		}
		try{
			return(Long.valueOf(value.toString()));
		}catch(Exception e){
			e.printStackTrace();
		}
		return(-2);
	}
	
	public static String getString(Object value){
		if(value == null){
			return(null);
		}
		if(value instanceof java.lang.String){
			return((String)value);
		}else if(value instanceof java.lang.Integer){
			return(((Integer)value).toString());
		}else if(value instanceof java.lang.Long){
			return(((Long)value).toString());
		}
		//This probably all that is needed, when it's not null
		return(value.toString());
	}
	
	public static long getDateAsMilliseconds(Object value){
		if(value == null){
			return(-1);
		}
		if(value instanceof java.util.Date){
			return(((Date)value).getTime());
		}
		return(-2);
	}
	
	public static boolean hasGap(long timeA, long timeB){
		//has a gap if time is greater than this
		return(!isDateWithRange(timeA, timeB, 6));
	}
	public static boolean isDateWithRange(long timeA, long timeB){
		//default the number of minutes that is considered close enough to compare results
		return(isDateWithRange(timeA, timeB, 4.5));
	}
	public static boolean isDateWithRange(long timeA, long timeB, double numberOfMinutes){
		//time is in milliseconds, so compare difference < minutes (millisecond * seconds * minutes)
		return(Math.abs(timeA - timeB) < (1000 * 60 * numberOfMinutes)); //
	}
	
	public static Properties loadProperties(String filename) {
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {	 
			input = new FileInputStream(filename);
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not open property file: "+ filename);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return(prop);
	}
}
