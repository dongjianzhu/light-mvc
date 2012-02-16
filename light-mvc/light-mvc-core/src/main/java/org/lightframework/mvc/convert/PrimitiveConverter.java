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
package org.lightframework.mvc.convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.lightframework.mvc.binding.Argument;
import org.lightframework.mvc.binding.BindingException;

/**
 * class to convert primitive type itself and primitive type's wrapper type. 
 *
 * @author fenghm(live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class PrimitiveConverter implements IConverter {

	private static final Set<Class<?>> types = new HashSet<Class<?>>();
	
	static{
		types.add(Boolean.TYPE);
		types.add(Boolean.class);
		types.add(Character.TYPE);
		types.add(Character.class);
		types.add(Byte.TYPE);
		types.add(Byte.class);
		types.add(Short.TYPE);
		types.add(Short.class);
		types.add(Integer.TYPE);
		types.add(Integer.class);
		types.add(Long.TYPE);
		types.add(Long.class);
		types.add(Float.TYPE);
		types.add(Float.class);
		types.add(Double.TYPE);
		types.add(Double.class);
		types.add(String.class);
		types.add(BigDecimal.class);
		types.add(BigInteger.class);
	}
	
	public Set<Class<?>> getSupportedTypes() {
		return types;
	}

	public Object convertToObject(Argument type, String string) throws BindingException, Exception {
		Object   value = null;
		Class<?> clazz = type.getType();
		
		if(String.class == clazz){
			value = string;
		}else if(!"".equals((string = string.trim()))){
			if (clazz == Integer.class || clazz == Integer.TYPE) {
	            value = Integer.parseInt(string);
	        } else if (clazz == Short.class || clazz == Short.TYPE) {
	            value = Short.parseShort(string);
	        } else if (clazz == Long.class || clazz == Long.TYPE) {
	            value = Long.parseLong(string);
	        } else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
	        	if("1".equals(string)){
	        		value = Boolean.TRUE;
	        	}else if("0".equals(string)){
	        		value = Boolean.FALSE;
	        	}else{
	        		value = Boolean.parseBoolean(string);	
	        	}
	        } else if (clazz == Float.class || clazz == Float.TYPE) {
	            value = Float.parseFloat(string);
	        } else if (clazz == Double.class || clazz == Double.TYPE) {
	            value = Double.parseDouble(string);
	        } else if ((clazz == Character.class || clazz == Character.TYPE ) && string.length() == 1) {
	            value = string.charAt(0);
	        } else if (clazz == Byte.class || clazz == Byte.TYPE) {
	        	if(string.startsWith("0x") || string.startsWith("0X")){//HEX STRING
	        		value = Byte.parseByte(string.substring(2),16);
	        	}else{
	        		value = Byte.parseByte(string);	
	        	}
	        } else if (BigDecimal.class == clazz){
	        	value = new BigDecimal(string);
	        } else if (BigInteger.class == clazz){
	        	value = new BigInteger(string);
	        }
		}
		return value;
	}
}
