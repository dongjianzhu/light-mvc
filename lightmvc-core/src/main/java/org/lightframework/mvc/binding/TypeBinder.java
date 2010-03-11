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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Utils;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.Config.Format;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.exceptions.BindingException;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * TODO : document me
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class TypeBinder extends Binder {
	
	private static final Set<Class<?>> supportedTypes = new HashSet<Class<?>>();
	
	static {
		supportedTypes.add(Boolean.class);
		supportedTypes.add(Character.class);
		supportedTypes.add(Byte.class);
		supportedTypes.add(Short.class);
		supportedTypes.add(Integer.class);
		supportedTypes.add(Long.class);
		supportedTypes.add(Float.class);
		supportedTypes.add(Double.class);
		supportedTypes.add(String.class);
		supportedTypes.add(Date.class);
		supportedTypes.add(java.sql.Date.class);
		supportedTypes.add(java.sql.Time.class);
		supportedTypes.add(java.sql.Timestamp.class);
		supportedTypes.add(BigDecimal.class);
		supportedTypes.add(Object.class);
	}
	
	@Override
	public Object binding(Request request, Action action, Argument arg) throws BindingException {
		Class<?> type = ClassUtils.getWrapperType(arg.getType());
		if(supportedTypes.contains(type)){
			Object value = getObjectValue(request, action, arg);
			if(null != value){
				return binding(arg,type,value);
			}
		}
		return null;
	}
	
	protected Object binding(Argument arg,Class<?> type,Object value) throws BindingException{
		if(type.equals(Object.class) || type.isAssignableFrom(value.getClass())){
			return value;
		} else {
			String string = 
				value.getClass().isArray() ? Utils.arrayToString((Object[])value) : value.toString();
			
		    //if not blank
			if(!"".equals(string = string.trim())){
				Object object = convert(arg,type,string);
				if(null != object){
					return object;
				}else{
					//TODO : i18n
					throw new BindingException("invalid value [{0}] of type [{1}]",string,type.getName());
				}
			}
		}	
		return null;
	}
	
	protected Object convert(Argument arg,Class<?> type,String string){
		Object value = null;

		if( String.class == type){
			value = string;
		}else if (type == Integer.class || type == Integer.TYPE) {
            value = Integer.parseInt(string);
        } else if (type == Short.class || type == Short.TYPE) {
            value = Short.parseShort(string);
        } else if (type == Long.class || type == Long.TYPE) {
            value = Long.parseLong(string);
        } else if (type == Boolean.class || type == Boolean.TYPE) {
        	if("1".equals(string)){
        		value = Boolean.TRUE;
        	}else if("0".equals(string)){
        		value = Boolean.FALSE;
        	}else{
        		value = Boolean.parseBoolean(string);	
        	}
        } else if (type == Float.class || type == Float.TYPE) {
            value = Float.parseFloat(string);
        } else if (type == Double.class || type == Double.TYPE) {
            value = Double.parseDouble(string);
        } else if ((type == Character.class || type == Character.TYPE ) && string.length() == 1) {
            value = string.charAt(0);
        } else if (type == Byte.class || type == Byte.TYPE) {
            value = Byte.parseByte(string);
        } else if (Date.class.isAssignableFrom(type)){
        	value = dateConvert(arg, string);
        } else if (BigDecimal.class == type){
        	value = new BigDecimal(string);
        } else if (BigInteger.class == type){
        	value = new BigInteger(string);
        }
		return value;		
	}
	
	private static Date dateConvert(Argument arg,String string){
		if(arg.getType().equals(Time.class)){
			return (Time)toDate(Time.class,string, Format.DEFAULT_TIME);
		}else if(arg.getType().equals(java.sql.Date.class)){
			return (java.sql.Date)toDate(java.sql.Date.class,string,Format.DEFAULT_DATE);
		}else if(arg.getType().equals(Timestamp.class)){
			return (Timestamp)toDate(Timestamp.class,string,Format.DEFAULT_TIMESTAMP);
		}else if(arg.getType().equals(Date.class)){
			for(Annotation annotation : arg.getConfigs()){
				if(annotation.annotationType().isAnnotationPresent(Format.class)){
					return toDate(Date.class,string,((Format)annotation).value());
				}
			}
			Date date = toDate(Date.class,string,Format.DEFAULT_DATETIME);
			if(null == date){
				date = toDate(Date.class,string,Format.DEFAULT_DATE);
				if(null == date){
					date = toDate(Date.class,string,Format.DEFAULT_TIME);
					if(null == date){
						date = toDate(Date.class,string,Format.DEFAULT_TIMESTAMP);
					}
				}
			}
			return date;
		}else{
			throw new org.lightframework.mvc.MVCException("@InvalidDateType",arg.getType().getName());
		}
	}
	
   private static Date toDate(Class<? extends Date> type, String string, String format) {
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);
    	ParsePosition pos = new ParsePosition(0);
        formater.setLenient(false);
        date = formater.parse(string, pos);
        if(null != date && pos.getIndex() != string.length()){
            date = null;
        }
        if(null != date){
        	if(type.equals(Time.class)){
        		return new Time(date.getTime());
        	}else if(type.equals(java.sql.Date.class)){
        		return new Date(date.getTime());
        	}else if(type.equals(Timestamp.class)){
        		return new Timestamp(date.getTime());
        	}
        }
        return date;
   }
}
