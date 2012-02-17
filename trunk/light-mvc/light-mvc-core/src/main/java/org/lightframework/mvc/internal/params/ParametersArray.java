/*
 * Copyright 2012 the original author or authors.
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
package org.lightframework.mvc.internal.params;

import java.util.Map;

import org.lightframework.mvc.MvcException;

/**
 * <code>{@link ParametersArray}</code>
 *
 * @author fenghm (fenghm@bingosoft.net)
 *
 * @since 1.0.0
 */
public class ParametersArray {
	protected Object[] array;

	public ParametersArray(){
		this.array = new Object[]{};
	}
	
	public ParametersArray(Object[] array){
		this.array = array;
	}
	
	public Object[] array(){
		return array;
	}
	
	public int length(){
		return array.length;
	}
	
	public boolean isEmpty(){
		return array.length == 0;
	}
	
	public Object get(int index){
		return array[index];
	}
	
	public Object[] getArray(int index){
		Object value = get(index);
		return null == value ? null : (Object[])value;
	}
	
	public String getString(int index){
		Object value = get(index);
		return null == value ? null : value.toString();
	}
	
	public int getInt(int index){
		Object value = get(index);
		return null == value ? 0 : Integer.parseInt(value.toString());
	}
	
	public Integer getInteger(int index){
		Object value = get(index);
		return null == value ? null : Integer.parseInt(value.toString());
	}
	
	public Long getLong(int index){
		Object value = get(index);
		return null == value ? null : Long.parseLong(value.toString());
	}
	
	public Boolean getBoolean(int index){
		Object value = get(index);
		return null == value ? null : Boolean.parseBoolean(value.toString());
	}
	
	@SuppressWarnings("unchecked")
	public Parameters getBean(int index){
		Object value = get(index);
		if(null == value){
			return null;
		}else if(value instanceof Map){
			return new Parameters((Map)value);
		}else if(value.getClass().isArray()){
			return new Parameters((Object[])value);
		}else{
			throw new MvcException(value.getClass() + " is not a map or array");
		}
	}
}