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
package org.lightframework.mvc.internal.convert;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import org.lightframework.mvc.internal.ognl.SimpleOgnl;
import org.lightframework.mvc.internal.reflect.ReflectEnum;
import org.lightframework.mvc.internal.reflect.ReflectField;
import org.lightframework.mvc.internal.reflect.ReflectType;
import org.lightframework.mvc.internal.reflect.ReflectUtils;
import org.lightframework.mvc.internal.utils.ClassUtils;
import org.lightframework.mvc.internal.utils.DateUtils;
import org.lightframework.mvc.internal.utils.StringUtils;


/**
 * Object Converter
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public final class Converter {
    
    private static final Class<?>[] EMPTY_ARGS = new Class<?>[]{};
    
    public static Object convert(Class<?> targetType,Object value) throws ConvertUnsupportedException {
        return convert(targetType,null,value,null);
    }
    
	public static Object convert(Class<?> targetType,Type genericType, Object value) throws ConvertUnsupportedException {
		return convert(targetType,genericType,value,null);
	}
    
    @SuppressWarnings("unchecked")
    public static Object convert(Class targetType,Type genericType, Object value,ConvertOptions options) throws ConvertUnsupportedException {
		if(null == targetType){
		    throw new IllegalArgumentException("argument 'targetType' required");
		}
	    
		if(null == value){
			return defaultValue(targetType);
		}
		
		if(targetType == Object.class){
		    return value;
		}
		
		Class<?> valueType = value.getClass();

		if(targetType.isAssignableFrom(valueType) && !targetType.isArray() && !Iterable.class.isAssignableFrom(targetType)){
			return value;
		}
		
		if(valueType.isEnum()){
		    value = ReflectEnum.toValue(value);
		}
		
		try{
			if(targetType == String.class){
				return convertToString(value,null == options ? null : options.format);
			}
			
			if(valueType == String.class){
				return convert(targetType, (String)value,null == options ? null : options.format);
			}
			
			if(targetType == Integer.TYPE || targetType == Integer.class){
				if(valueType == Integer.class || valueType == Integer.TYPE){
					return value;
				}else{
					return Integer.parseInt(value.toString());
				}
			}
			
			if(targetType == Long.TYPE || targetType == Long.class){
				if(valueType == Long.class || valueType == Long.TYPE){
					return value;
				}else{
					return Long.parseLong(value.toString());
				}
			}			
			
			if(targetType == Short.TYPE || targetType == Short.class){
				if(valueType == Short.class || valueType == Short.TYPE){
					return value;
				}else{
					return Short.parseShort(value.toString());
				}
			}
			
			if (targetType == Boolean.TYPE || targetType == Boolean.class) {
				if(valueType == Boolean.class || valueType == Boolean.TYPE){
					return value;
				}else{
					String string = value.toString();
		        	if("1".equals(string) || "on".equalsIgnoreCase(string)){
		        		return Boolean.TRUE;
		        	}else if("0".equals(string) || "off".equalsIgnoreCase(string)){
		        		return Boolean.FALSE;
		        	}else{
		        		return Boolean.parseBoolean(string);	
		        	}		
				}
	        }			
			
			if(targetType == Float.TYPE || targetType == Float.class){
				if(valueType == Float.class || valueType == Float.TYPE){
					return value;
				}else{
					return Float.parseFloat(value.toString());
				}
			}	
			
			if(targetType == Double.TYPE || targetType == Double.class){
				if(valueType == Double.class || valueType == Double.TYPE){
					return value;
				}else{
					return Double.parseDouble(value.toString());
				}
			}
			
			if(targetType == Character.TYPE || targetType == Character.class){
				if(valueType == Character.class || valueType == Character.TYPE){
					return value;
				}else {
					String string = value.toString();
					if(string.length() == 1){
						return string.charAt(0);
					}
					throw new IllegalArgumentException("cant not convert string \"" + string + "\" to char");
				}
			}	
			
			if(targetType == Byte.TYPE || targetType == Byte.class){
				if(valueType == Byte.class || valueType == Byte.TYPE){
					return value;
				}else{
					String string = value.toString();
		        	if(string.startsWith("0x") || string.startsWith("0X")){//HEX STRING
		        		return Byte.parseByte(string.substring(2),16);
		        	}else{
		        		return Byte.parseByte(string);	
		        	}
				}
			}
			
			if(Date.class.isAssignableFrom(targetType)){
			    return convertToDate(targetType, value,null != options ? options.format : null);
			}
			
			if (BigDecimal.class == targetType){
	        	return new BigDecimal(value.toString());
	        }
			
			if (BigInteger.class == targetType){
	        	return new BigInteger(value.toString());
	        }
			
            if (targetType.isEnum()) {
                return convertToEnum(targetType, value);
            }
            
            if(targetType.isArray()){
                return convertToArray(targetType.getComponentType(),value);
            }
            
            if(List.class.isAssignableFrom(targetType)){
                return convertToList(null == genericType ? Object.class : ReflectUtils.getActualType(genericType), value);
            }
            
            if(targetType.equals(Class.class)){
                return ClassUtils.forName(convertToString(value));
            }
            
            if(Object.class.equals(targetType)){
            	return value;
            }
            
            if(isJavaBean(targetType)){
                return convertToBean(targetType, value);
            }
		}catch(ConvertException e){
			throw e;
		}catch(Exception e){
			throw new ConvertException(MessageFormat.format("error converting type ''{0}'' to ''{1}'' : " + e.getMessage(),
					                                         valueType.getSimpleName(),
					                                         targetType.getSimpleName()), e);
		}

		throw new ConvertUnsupportedException(MessageFormat.format("can not convert type ''{0}'' to ''{1}''",
            								   			             valueType.getSimpleName(),
            								   			             targetType.getSimpleName()));
    }
    
	/**
	 * 把字符串转换成指定类型的对象
	 */
	public static Object convert(Class<?> type,String string) throws ConvertUnsupportedException {
		return convert(type,string,null);
	}
    
    public static Object convert(Class<?> type,String string,String format) throws ConvertUnsupportedException {
		try {
            if(String.class.equals(type)){
            	return string;
            }

            if(null == string || "".equals(string = string.trim())){
            	return defaultValue(type);
            }
            
            if (type == Integer.class || type == Integer.TYPE) {
                return Integer.parseInt(string);
            }
            
            if (type == Short.class || type == Short.TYPE) {
                return Short.parseShort(string);
            }
            
            if (type == Long.class || type == Long.TYPE) {
                return Long.parseLong(string);
            }
            
            if (type == Boolean.class || type == Boolean.TYPE) {
	        	if("1".equals(string) || "on".equalsIgnoreCase(string)){
	        		return Boolean.TRUE;
	        	}else if("0".equals(string) || "off".equalsIgnoreCase(string)){
            		return Boolean.FALSE;
            	}else{
            		return Boolean.parseBoolean(string);	
            	}
            }
            
            if (type == Float.class || type == Float.TYPE) {
                return Float.parseFloat(string);
            }
            
            if (type == Double.class || type == Double.TYPE) {
                return Double.parseDouble(string);
            }
            
            if ((type == Character.class || type == Character.TYPE ) && string.length() == 1) {
                return string.charAt(0);
            }
            
            if (type == Byte.class || type == Byte.TYPE) {
            	if(string.startsWith("0x") || string.startsWith("0X")){//HEX STRING
            		return Byte.parseByte(string.substring(2),16);
            	}else{
            		return Byte.parseByte(string);	
            	}
            }
            
            if (BigDecimal.class == type){
            	return new BigDecimal(string);
            }
            
            if (BigInteger.class == type){
            	return new BigInteger(string);
            }
            
            if(Date.class.isAssignableFrom(type)){
                return convertToDate(type.asSubclass(Date.class),string,format);
            }
            
            if(type.isArray()){
                return stringToArray(type.getComponentType(),string);
            }
            
            if(type.isEnum()){
                return convertToEnum(type, string);
            }
            
            if(Class.class == type){
                return ClassUtils.forName(string);
            }
		} catch(ConvertException e){
		    throw e;
        } catch (Exception e) {
            throw new ConvertException(MessageFormat.format("error converting string ''{0}'' to ''{1}''",
                                                             string,
                                                             type.getSimpleName()), e);
        }
		
		throw new ConvertUnsupportedException(MessageFormat.format("unsupported type ''{0}'' convert from string",type.getName()));
    }
    
	/**
	 * 把对象转换为字符串
	 */
	public static String convertToString(Object value){
		return convertToString(value,null);
	}
	
    public static String convertToString(Object value,String format){
		if(null == value){
			return "";
		}
		
		if(value instanceof String){
			return (String)value;
		}
		
		if(value instanceof StringBuilder){
			return ((StringBuilder)value).toString();
		}
		
		if(value instanceof java.sql.Date){
			return StringUtils.isEmpty(format) ? DateUtils.dateFormater.format((java.sql.Date)value) : 
												  DateUtils.getDateFormat(format).format((java.sql.Date)value);
		}
		
		if(value instanceof Time){
			return StringUtils.isEmpty(format) ? DateUtils.timeFormater.format((Time)value) : 
				    							  DateUtils.getDateFormat(format).format((Time)value);
		}
		
		if(value instanceof Date){
			return StringUtils.isEmpty(format) ? DateUtils.dateTimeFormater.format((Date)value) : 
				  							      DateUtils.getDateFormat(format).format((Date)value);
		}
		
        if (value instanceof Class<?>) {
            return ((Class<?>) value).getName();
        }
		
		if(value.getClass().isArray()){
		    return arrayToString(value);
		}
		
		if(value instanceof Iterable<?>){
		    return iterableToString((Iterable<?>)value);
		}
		
		return value.toString();
    }
	
	public static Date convertToDate(Class<? extends Date> type,Object value){
		return convertToDate(type,value,null);
	}
	
	public static Date convertToDate(Class<? extends Date> type,Object value,String format){
	    Class<?> valueType = value.getClass();
	    if(Long.class == valueType || Long.TYPE == valueType){
	        long time = ((Long)value);
	        if(Time.class == type){
	            return new Time(time);
	        }
	        
	        if(java.sql.Date.class == type){
	            return new java.sql.Date(time);
	        }
	        
	        if(Timestamp.class == type){
	            return new Timestamp(time);
	        }
	        
	        if(Date.class == type){
	            return new Date(time);
	        }
	    }else if(value instanceof Date){
	        long time = ((Date)value).getTime();
	        
           if(Time.class == type){
                return new Time(time);
            }
            
            if(java.sql.Date.class == type){
                return new java.sql.Date(time);
            }
            
            if(Timestamp.class == type){
                return new Timestamp(time);
            }
	    }else if(String.class == valueType){
	        return convertToDate(type, (String)value,format);
	    }
	    throw new ConvertUnsupportedException(MessageFormat.format("unsupported type ''{0}'' convert to Date",valueType.getName()));
	}
	
	public static Date convertToDate(Class<? extends Date> type,String string){
		return convertToDate(type, string,null);
	}
	
	public static Date convertToDate(Class<? extends Date> type,String string,String format){
	    Date date = DateUtils.toDate(type,string,format);
	    
	    if(null == date){
	        throw new ConvertException(MessageFormat.format("invalid date string ''{0}''",string));
	    }
	    
	    return date;
	}
	
    private static Object convertToEnum(Class<?> type, Object value) {
        if (value.getClass().equals(type)) {
            return value;
        } else {
            return ReflectEnum.valueOf(type, value);
        }
    }
	
	@SuppressWarnings({"unchecked","unused"})
    private static Object convertToArray(Class<?> componentType, Object value) {
        if(value.getClass().isArray()){
            Class<?> valueComponentType = value.getClass().getComponentType();
            if(componentType.isAssignableFrom(valueComponentType)){
                return value;
            }else{
                int length = Array.getLength(value);
                Object array = Array.newInstance(componentType, length);
                for(int i=0;i<length;i++){
                    Array.set(array, i, convert(componentType,Array.get(value, i)));
                }
                return array;
            }
        } else if(value instanceof String){
            return stringToArray(componentType, (String)value);
        } else if(value instanceof Iterable){
            int length = 0;
            
            if(value instanceof Collection){
                length = ((Collection)value).size();
            }else{
                for(Object e : (Iterable)value){
                    length++;
                }
            }
            
            Object array = Array.newInstance(componentType, length);
            
            int index = 0;
            for(Object element : (Iterable)value){
                Array.set(array,index++,convert(componentType,element));
            }            
            
            return array;
        }
        
        throw new ConvertUnsupportedException("can't convert type '" + value.getClass().getName() + "' to array '" + componentType.getClass().getName() + "'");
    }
	
	@SuppressWarnings("unchecked")
    private static <T> List<T> convertToList(Class<T> elementType, Object value) {
        List<T> list = new ArrayList<T>();
        
        if(value instanceof List){
            for(Object element : (List)value){
                list.add((T)convert(elementType,element));
            }
        }else {
            Object array = convertToArray(elementType, value);
            int length = Array.getLength(array);
            for(int i=0;i<length;i++){
                list.add((T)Array.get(array, i));
            }
        }
        return list;
    }
	
	@SuppressWarnings("unchecked")
	private static <T> T convertToBean(Class<T> clazz,Object data){
	    if(!(data instanceof Map)){
	        throw new ConvertUnsupportedException("only Map cant convert to java bean, '" + data.getClass().getName() + "' not supported");
	    }
	    
	    Map         map  = (Map)data;
        ReflectType type = ReflectType.get(clazz);
        T           bean = (T)type.newInstance();

        for(Object entryObject : map.entrySet()){
        	Entry entry = (Entry)entryObject;
        	
        	Object key   = entry.getKey();
        	Object param = entry.getValue();
        	
        	String name  = key.getClass().equals(String.class) ? (String)key : key.toString();
        	
        	ReflectField field = type.findField(name);
        	
        	if(null != field){
        		field.setValue(bean, convert(field.getType(),field.getGenericType(),param));
        	}else{
        		SimpleOgnl.bindingField(bean,name,param);
        	}
        }
        
        return bean;
	}
	
	/**
	 * 根据类型返回该类型的默认值，主要是处理原始类型包括常用的数据和集合类型
	 */
	@SuppressWarnings("unchecked")
	public static Object defaultValue(Class<?> type){
		
		if(Integer.TYPE == type){
			return 0;
		}
		
		if(Long.TYPE == type){
			return 0L;
		}
		
		if(Float.TYPE == type){
			return 0.0f;
		}
		
		if(Double.TYPE == type){
			return 0.0d;
		}
		
		if(Boolean.TYPE == type){
			return false;
		}
		
		if(Short.TYPE == type){
			return (short)0;
		}
		
		if(List.class.isAssignableFrom(type)){
		    return new ArrayList();
		}
		
		if(Set.class.isAssignableFrom(type)){
		    return new HashSet();
		}
		
		if(type.isArray()){
		    return Array.newInstance(type.getComponentType(), 0);
		}
		
		if(Map.class.isAssignableFrom(type)){
		    return new HashMap();
		}
		
		return null;
	}
	
	public static Object defaultInstance(Class<?> type){
		Object instance = defaultValue(type);
		
		if(null == instance && ReflectType.isBeanType(type)){
			instance = ReflectType.get(type).newInstance();
		}
		
		return instance;
	}
	
	public static Object stringToArray(Class<?> componentType,String string){
	    if(null == string){
	        return Array.newInstance(componentType, 0);
	    }
	    
        String[] strings = split(string,",");
        
        Object array = Array.newInstance(componentType, strings.length);
        for(int i=0;i<strings.length;i++){
            Array.set(array, i, convert(componentType, strings[i]));
        }
        return array;
    }
	
	private static String arrayToString(Object array){
	    StringBuilder string = new StringBuilder();
	    for(int i=0;i<Array.getLength(array);i++){
	        if(i > 0){
	            string.append(',');
	        }
	        string.append(convertToString(Array.get(array, i)));
	    }
	    return string.toString();
	}
	
	private static String iterableToString(Iterable<?> iterable){
        StringBuilder string = new StringBuilder();
        
        int i = 0;
        for (Object value : iterable) {
            if (i > 0) {
                string.append(',');
            }
            string.append(convertToString(value));
            i++;
        }
        return string.toString();
	}
	
    private static String[] split(String string,String seperator){
        if(null == string){
            return new String[]{};
        }
        StringTokenizer tokens = new StringTokenizer(string.trim(), seperator, false);
        String[] result = new String[ tokens.countTokens() ];
        int i=0;
        while ( tokens.hasMoreTokens() ) {
            result[i++] = tokens.nextToken().trim();
        }
        return result;        
    }
    
    private static boolean isJavaBean(Class<?> type){
        if(Modifier.isPublic(type.getModifiers())){
            String name = type.getName();
            
            if(!(name.startsWith("java.") || name.startsWith("javax."))){
                try {
                    Constructor<?> constructor = type.getConstructor(EMPTY_ARGS);
                    if(Modifier.isPublic(constructor.getModifiers())){
                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    ;
                }
            }
        }
        return false;
    }
}