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
package org.lightframework.mvc.render.json;

import java.util.List;
import java.util.Map;

/**
 * represents a json value. 
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class JSONObject {

	private Map<String, Object> map;
	
    JSONObject(Map<String, Object> map) {
    	this.map = map;
    }
	
	public Map<String, Object> map(){
		return map;
	}
	
	public boolean exists(String name){
		return map.containsKey(name);
	}
	
	public Object get(String name){
		return map.get(name);
	}
	
	public Object[] getArray(String name){
		Object value = get(name);
		return null == value ? null : ((List<?>)value).toArray();
	}
	
	public String getString(String name){
		Object value = get(name);
		return null == value ? null : value.toString();
	}
	
	public int getInt(String name){
		Object value = get(name);
		return null == value ? 0 : Integer.parseInt(value.toString());
	}
	
	public Integer getInteger(String name){
		Object value = get(name);
		return null == value ? null : Integer.parseInt(value.toString());
	}
	
	public Boolean getBoolean(String name){
		Object value = get(name);
		return null == value ? null : Boolean.parseBoolean(value.toString());
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getObject(String name){
		Object value = get(name);
		return value instanceof Map ? new JSONObject((Map)value) : null;
	}
}
