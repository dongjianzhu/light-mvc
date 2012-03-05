/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lightframework.mvc.internal.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lightframework.mvc.internal.format.ConcurrentDateFormat;

/**
 * <code>{@link DateUtils}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public class DateUtils {

    public static final String DATE_FORMAT      = "yyyy-MM-dd";
    public static final String TIME_FORMAT      = "HH:mm:ss";
    public static final String DATETIME_FORMAT  = "yyyy-MM-dd HH:mm:ss";
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String RFC_DATE_FORMAT  = "yyyy-MM-dd'T'HH:mm:ssZ";  
    
    public static final DateFormat dateFormater;
    public static final DateFormat timeFormater;
    public static final DateFormat dateTimeFormater;
    public static final DateFormat timestampFormater;
    public static final DateFormat rfcDateFormater;
    
    private static final Map<String, DateFormat> formaters = new ConcurrentHashMap<String, DateFormat>();
    
    static {
        dateFormater      = new ConcurrentDateFormat(DATE_FORMAT);
        timeFormater      = new ConcurrentDateFormat(TIME_FORMAT);
        dateTimeFormater  = new ConcurrentDateFormat(DATETIME_FORMAT);
        timestampFormater = new ConcurrentDateFormat(TIMESTAMP_FORMAT);
        rfcDateFormater   = new ConcurrentDateFormat(RFC_DATE_FORMAT);
        
        dateFormater.setLenient(false);
        timeFormater.setLenient(false);
        dateTimeFormater.setLenient(false);
        timestampFormater.setLenient(false);
        rfcDateFormater.setLenient(false);
        
        formaters.put(DATE_FORMAT, 		dateFormater);
        formaters.put(TIME_FORMAT, 		timeFormater);
        formaters.put(DATETIME_FORMAT,  dateTimeFormater);
        formaters.put(TIMESTAMP_FORMAT, timestampFormater);
        formaters.put(RFC_DATE_FORMAT,  rfcDateFormater);
    }
    
    public static DateFormat getDateFormat(String format){
		DateFormat formater = formaters.get(format);
		
		if(null == formater){
			formater = new ConcurrentDateFormat(format);
			formater.setLenient(false);
			
			formaters.put(format, formater);
		}
		
		return formater;
    }
    
    public static Date toDate(Class<? extends Date> type, String string,String format) {
    	if(null != format && !"".equals(format)){
    		return toDate(type,string,getDateFormat(format));
    	}
    	
    	return toDate(type,string);
    }
    
    public static Date toDate(Class<? extends Date> type, String string) {
        if(Time.class == type){
            return toDate(type, string, timeFormater);
        }
        
        if(java.sql.Date.class == type){
            return toDate(type, string, dateFormater);
        }
        
        if(Timestamp.class == type){
            return toDate(type, string, timestampFormater);
        }
        
        if(Date.class == type){
            return toDate(string);
        }
        
        throw new IllegalArgumentException("unsupported date type '" + type.getSimpleName() + "'");
    }
    
    public static Date toRfcDate(String string){
        final StringBuffer sb = new StringBuffer(string);
        if (string.lastIndexOf(":") == string.length()-3) {
            sb.deleteCharAt(string.length()-3);
        }
        return toDate(Date.class,string,rfcDateFormater);
    }
    
    public static Date toDate(String string){
        Date date = null;
        
        if((date = toDate(string,dateFormater)) != null){
            return date;
        }
        
        if((date = toDate(string,dateTimeFormater)) != null){
            return date;
        }  
        
        if((date = toDate(string,timestampFormater)) != null){
            return date;
        }    
        
        if((date = toRfcDate(string)) != null){
            return date;
        }   

        if((date = toDate(string,timeFormater)) != null){
            return date;
        }
        
        return null;
    }

    private static Date toDate(Class<? extends Date> type,String string,DateFormat format){
        ParsePosition position = new ParsePosition(0);
        
        Date date = format.parse(string,position);
        
        if (null != date && position.getIndex() != string.length()) {
            date = null;
        }
        
        if (null != date) {
            if (type.equals(Time.class)) {
                return new Time(date.getTime());
            } else if (type.equals(java.sql.Date.class)) {
                return new java.sql.Date(date.getTime());
            } else if (type.equals(Timestamp.class)) {
                return new Timestamp(date.getTime());
            }
        }
        
        return date;
    }
    
    private static Date toDate(String string,DateFormat format){
        ParsePosition position = new ParsePosition(0);
        
        Date date = format.parse(string,position);
        
        if (null != date && position.getIndex() != string.length()) {
            date = null;
        }
        
        return date;
    }
    
	public static String toString(Date date) {
		String format = DATE_FORMAT;
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (c.get(Calendar.HOUR) > 0 || c.get(Calendar.MINUTE) > 0 || c.get(Calendar.SECOND) > 0) {
			format = DATETIME_FORMAT;
		}
		return toString(date, format);
	}

	public static String toString(Date date, String format) {
		return getDateFormat(format).format(date);
	}
}
