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
package org.lightframework.mvc.core;

import java.lang.annotation.Annotation;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.Config.Default;
import org.lightframework.mvc.Config.Format;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * the core plugin to binding parameters in request of action method.
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class Binder extends Plugin {
	
	@Override
    public boolean binding(Request request, Response response, Action action) throws Throwable{
		for(Argument arg : action.getArguments()){
			if(!arg.isBinded()){
				binding(request,action,arg);
			}
		}
		return true;
    }
	
	/**
	 * binding value to the argument
	 */
	private static void binding(Request request,Action action,Argument arg){
		String   name = arg.getName();
		Class<?> type = arg.getType();
		
		if(type.isPrimitive()){
			//convert primitive type to warpper type , such as : int -> Integer
			type = ClassUtils.getWrapperType(type);
		}
		
		//get value from action's scope
		Object value = action.getParameter(name);
		if(null == value){
			//then get from request's scope
			value = request.getParameter(name);
			
			if(null == value || "".equals(value)){
				value = getDefaultValue(arg);
			}
		}

		if(null != value){
			if(value.getClass().equals(type) || type.equals(Object.class)){
				//binding directly
				arg.binding(value);
			}else{
				//map binding
				if(mapBinding(request,action,arg)){
					return ;
				}
				
				//string binding
				if(value instanceof String && stringBinding(arg, (String)value)){
					return;
				}
				
				if(type.isArray() && arrayBinding(request,action,arg)){
					return ;
				}
				
				//bean binding
				beanBinding(request,action,arg);
			}
		}else{
			arg.binding(null);
		}
		
		if(arg.getType().isPrimitive() && null == arg.getValue()){
			arg.binding(ClassUtils.getDefaultValue(arg.getType()));
		}
	}
	
	private static String getDefaultValue(Argument arg){
		for(Annotation annotation : arg.getConfigs()){
			if(Default.class.equals(annotation.annotationType())){
				return ((Default)annotation).value();
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static boolean mapBinding(Request request, Action action, Argument arg){
		if(Map.class.isAssignableFrom(arg.getType())){
			Map map = new HashMap();
			map.putAll(request.getParameters());
			map.putAll(action.getParameters());
			arg.binding(map);
			return true;
		}
        return false;
	}
	
	private static boolean stringBinding(Argument arg,String string){
		if(string.length() == 0){
			return false;
		}
		
		Class<?> type = arg.getType();
		Object value  = null;

		if (type == Integer.TYPE || type == Integer.class) {
            value = Integer.parseInt(string);
        } else if (type == Short.TYPE || type == Short.class) {
            value = Short.parseShort(string);
        } else if (type == Long.TYPE || type == Long.class) {
            value = Long.parseLong(string);
        } else if (type == Boolean.TYPE || type == Boolean.class) {
        	if("1".equals(string)){
        		value = Boolean.TRUE;
        	}else if("0".equals(string)){
        		value = Boolean.FALSE;
        	}else{
        		value = Boolean.parseBoolean(string);	
        	}
        } else if (type == Float.TYPE || type == Float.class) {
            value = Float.parseFloat(string);
        } else if (type == Double.TYPE || type == Double.class) {
            value = Double.parseDouble(string);
        } else if (type == Character.TYPE || type == Character.class && string.length() == 0) {
            value = string.charAt(0);
        } else if (type == Byte.TYPE || type == Byte.class) {
            value = Byte.parseByte(string);
        } else if (Date.class.isAssignableFrom(type)){
        	value = dateConvert(arg, string);
        }
		if(null != value){
			arg.binding(value);
		}
		return null != value;
	}
	
	private static boolean arrayBinding(Request request, Action action, Argument arg){
		//TODO : array binding
		return false;
	}
	
	private static boolean beanBinding(Request request, Action action, Argument arg){
		if(!ClassUtils.isJdkClass(arg.getType())){
			//TODO : bean binding
		}
		return false;
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
			throw new org.lightframework.mvc.ExException("@InvalidDateType",arg.getType().getName());
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