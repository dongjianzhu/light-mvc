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
package org.lightframework.mvc;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * utitlity class, internal used only
 * 
 * @author light.wind(lightworld.me@gmail.com)
 * @since 0.1
 */
public final class Utils {
	
	/**
	 * replace all 'from' to 'to' in string 'string'
	 * 
	 * <p/>
	 * 
	 * this code was copied from springframework
	 */
	public static String replace(String string, String from, String to) {
		if(null == string || null == from || null == to){
			return string;
		}
		if(string.length() == 0 || from.length() == 0){
			return string;
		}

		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = string.indexOf(from);
		// the index of an occurrence we've found, or -1
		int patLen = from.length();
		while (index >= 0) {
			sb.append(string.substring(pos, index));
			sb.append(to);
			pos = index + patLen;
			index = string.indexOf(from, pos);
		}
		sb.append(string.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}
 
	public final static class Ref<E> {
		public E value;
		public Ref(E value){
			this.value = value;
		}
	}

	/*
	public final static class ReadOnly{
		
		private static Map<Class<?>, Field> cache = new HashMap<Class<?>, Field>();
		
		public static void lock(Object o){
			Field field = cache.get(o.getClass());
			if(null == field){
				synchronized (o.getClass()) {
	                try {
	                    field = o.getClass().getDeclaredField("_readonly");
	                    cache.put(o.getClass(), field);
	                } catch (Exception e) {
	                	Logger.error("@Lang.ReadOnly.get_field_error",o.getClass().getName(),e.getMessage());
	                	return ;
	                }
	            }
			}
			
			try{
				field.set(o, true);
			}catch(Exception e){
				Logger.error("@Lang.ReadOnly.set_field_value_error",o.getClass().getName(),e.getMessage());
			}
		}
		
		public static void protect(Object o,boolean _readonly) throws IllegalAccessException{
			if(_readonly){
				throw new IllegalAccessException(Messages.getString("@Lang.ReadOnly.object_readonly_error",o.getClass().getName(),o.toString()));
			}
		}
	}
	*/
	
	/**
	 * assertion class of mvc framework
	 */
	public static final class Assert extends ExException {
		
        private static final long serialVersionUID = -4499551185792411201L;
        
		private Assert(String message, Object... args) {
	        super(message, args);
        }

		private static void fail(Assert e){
			throw e;
		}
		
		public static void fail(String message,Object... args){
			fail(new Assert(message,args));
		}
		
		public static void isTrue(boolean test,String failMessage,Object... args){
			if(!test){
				fail(failMessage,args);
			}
		}

		public static void notNull(String name,Object value) {
			if(null == value){
				fail(new Assert("@Assert.NotNull",name));
			}
		}
		
		public static void isNull(String name,Object value) {
			if(null != value){
				fail(new Assert("@Assert.Null",name));
			}
		}
		
		public static void notEmpty(String name,String value){
			if(null == value || value.trim().equals("")){
				fail(new Assert("@Assert.NotEmpty",name));
			}
		}
		
		public static void notEquals(String name,Object value,Object equalsTo){
			if(null != value && value.equals(equalsTo)){
				fail(new Assert("@Assert.NotEquals",name,value,equalsTo));
			}
		}
		
		public static void isEquals(String name,Object value,Object equalsTo){
			if(null != value && !value.equals(equalsTo)){
				fail(new Assert("@Assert.Equals",name,value,equalsTo));
			}
		}
	}
	
	public static class Messages {
		private static final String BUNDLE_NAME = "org.lightframework.mvc.messages"; //$NON-NLS-1$
		private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

		public static String getString(String key) {
			try {
				return RESOURCE_BUNDLE.getString(key);
			} catch (MissingResourceException e) {
				return '!' + key + '!';
			}
		}
		
		public static String getString(String key,Object ... arguments){
			if(arguments.length > 0){
				return MessageFormat.format(getString(key), arguments);	
			}else{
				return getString(key);
			}
		}
	}
}
