package org.lightframework.mvc.internal.params;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.lightframework.mvc.MvcException;

/**
 * a {@link Map} or array wrapper represents a java bean.
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class Parameters {
    
	protected Object[]     	   array;
	protected Map<String, Object> map;
	
	public Parameters(){
		this.map = new HashMap<String, Object>();
	}
	
	public Parameters(Object[] array){
		this.array = array;
	}
	
	public Parameters(Map<String, Object> map) {
    	this.map = map;
    }
    
	public boolean isArray(){
		return null != array;
	}
	
	public boolean isMap(){
		return map != null;
	}
	
	public Object[] array(){
		return array;
	}
	
    public Set<String> keys(){
    	return map.keySet();
    }
	
	public Map<String, Object> map(){
		return map;
	}
	
	public boolean isEmpty(){
		return null == array ? map.isEmpty() : array.length == 0;
	}
	
	public boolean contains(String name){
		return map.containsKey(name);
	}
	
	public Object get(String name){
		return map.get(name);
	}
	
	public Object[] getArray(String name){
		Object value = get(name);
		return null == value ? null : (Object[])value;
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
	
	public Long getLong(String name){
		Object value = get(name);
		return null == value ? null : Long.parseLong(value.toString());
	}
	
	public Boolean getBoolean(String name){
		Object value = get(name);
		return null == value ? null : Boolean.parseBoolean(value.toString());
	}
	
	@SuppressWarnings("unchecked")
	public Parameters getBean(String name){
		Object value = get(name);
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