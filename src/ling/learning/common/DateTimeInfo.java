package ling.learning.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeInfo {
	static public String getDateTimeInSeconds() {
		String ret = "";
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss"); 
		ret = df.format(day);			
		return ret;		
	}
	
	static public String getDateTimeInMilliSeconds() {
		String ret = "";
		Date day=new Date();    
		SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.SSS"); 
		ret = df.format(day);			
		return ret;		
	}

}
