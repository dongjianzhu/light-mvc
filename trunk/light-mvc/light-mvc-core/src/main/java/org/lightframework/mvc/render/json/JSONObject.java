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
    
    private Object value;

	JSONObject(Object value){
	    this.value = value;
	}
	
	public boolean isNull(){
	    return null == value;
	}
    
	@SuppressWarnings("unchecked")
    public boolean isArray(){
    	return null != value && (value.getClass().isArray() || value instanceof List);
    }
    
    @SuppressWarnings("unchecked")
    public boolean isMap(){
        return null != value && value instanceof Map;
    }
    
    public Object value(){
        return value;
    }
    
    public JSONArray jsonArray(){
    	return isArray() ? new JSONArray(array()) : null;
    }
    
    @SuppressWarnings("unchecked")
    public Object[] array(){
    	return ((List)value).toArray();
    }
    
    @SuppressWarnings("unchecked")
    public List<Object> arraylist(){
        return (List)value;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> map(){
        return (Map<String,Object>)value;
    }
    
	public boolean contains(String name){
		return map().containsKey(name);
	}
	
	public Object get(String name){
		return map().get(name);
	}
	
	public JSONArray getJSONArray(String name){
		Object[] array = getArray(name);
		return null == array ? null : new JSONArray(array);
	}
	
	@SuppressWarnings("unchecked")
	public Object[] getArray(String name){
		Object value = get(name);
		return null == value ? null : ((List)value).toArray();
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
		if(null == value){
			return null;
		}else if(value instanceof Map){
			return new JSONObject((Map)value);
		}else if(value.getClass().isArray()){
			return new JSONObject((Object[])value);
		}else{
			throw new JSONException(value.getClass() + " is not a map or array");
		}
	}    
}