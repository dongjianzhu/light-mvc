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
package org.lightframework.mvc.binding;

import java.lang.annotation.Annotation;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.lightframework.mvc.Lang.Type;
import org.lightframework.mvc.config.Format;
import org.lightframework.mvc.exceptions.BindingException;

/**
 * class to bind date value
 * 
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class DateBinder implements ITypeBinder {

	private static final Set<Class<?>> types = new HashSet<Class<?>>();

	static {
		types.add(Date.class);
		types.add(java.sql.Date.class);
		types.add(Time.class);
		types.add(Timestamp.class);
	}

	public Set<Class<?>> getSupportedTypes() {
		return types;
	}

	public Object bind(Type type, String string) throws BindingException, Exception {

//		long time = parseTime(string);

		Class<?> clazz = type.getType();
		if (Time.class == clazz) {
			return toDate(Time.class, string, Format.DEFAULT_TIME);
		} else if (java.sql.Date.class == clazz) {
			return toDate(java.sql.Date.class, string, Format.DEFAULT_DATE);
		} else if (Timestamp.class == clazz) {
			return toDate(Timestamp.class, string, Format.DEFAULT_TIMESTAMP);
		} else if (Date.class == clazz) {
			for (Annotation annotation : type.getConfigs()) {
				if (annotation.annotationType() == Format.class) {
					return toDate(Date.class, string, ((Format) annotation).value());
				}
			}
			
			Date date = toDate(Date.class, string, Format.DEFAULT_DATE);
			if (null == date) {
				date = toDate(Date.class, string, Format.DEFAULT_DATETIME);
				if (null == date) {
					date = toDateByRfcFromat(string);
					if(null == date){
						date = toDate(Date.class, string, Format.DEFAULT_TIME);
						if (null == date) {
							date = toDate(Date.class, string, Format.DEFAULT_TIMESTAMP);
						}
					}
				}
			}
			return date;
		} else {
			throw new BindingException("@InvalidDateType", type.getType().getName());
		}
	}

	private static Date toDate(Class<? extends Date> type, String string, String format) {
		Date date = null;
		SimpleDateFormat formater = new SimpleDateFormat(format);
		ParsePosition pos = new ParsePosition(0);
		formater.setLenient(false);
		date = formater.parse(string, pos);
		if (null != date && pos.getIndex() != string.length()) {
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
	
	private static Date toDateByRfcFromat(String string){
        final StringBuffer sb = new StringBuffer(string);
        if (string.lastIndexOf(":") == string.length()-3) {
            sb.deleteCharAt(string.length()-3);
        }
        return toDate(Date.class,string,Format.DEFAULT_RFC_DATE);		
	}

	/*
	private static long parseTime(String string) {
		if(null != string){
			try {
				return Long.parseLong(string);
			} catch (NumberFormatException e) {
				;
			}
		}
		return 0;
	}
	*/
}
