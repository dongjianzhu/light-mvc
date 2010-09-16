package org.lightframework.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * a {@link Map} or array wrapper represents a java bean.
 * @author fenghm (fenghm@bingosoft.net)
 * @since 1.0.0
 */
public class ParamsObject {
    
	protected ParamsArray         array;
	protected Map<String, Object> map;
	
	public ParamsObject(){
		this.map = new HashMap<String, Object>();
	}
	
	public ParamsObject(Object[] array){
		this.array = new ParamsArray(array);
	}
	
	public ParamsObject(Map<String, Object> map) {
    	this.map = map;
    }
    
	public boolean isArray(){
		return null != array;
	}
	
	public boolean isMap(){
		return map != null;
	}
	
	public ParamsArray array(){
		return array;
	}
	
    public Set<String> keys(){
    	return map.keySet();
    }
	
	public Map<String, Object> map(){
		return map;
	}
	
	public boolean isEmpty(){
		return null == array ? map.isEmpty() : array.length() == 0;
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
	public ParamsObject getBean(String name){
		Object value = get(name);
		if(null == value){
			return null;
		}else if(value instanceof Map){
			return new ParamsObject((Map)value);
		}else if(value.getClass().isArray()){
			return new ParamsObject((Object[])value);
		}else{
			throw new MvcException(value.getClass() + " is not a map or array");
		}
	}
	
	public static class ParamsArray {
		protected Object[] array;

		public ParamsArray(){
			this.array = new Object[]{};
		}
		
		public ParamsArray(Object[] array){
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
		public ParamsObject getBean(int index){
			Object value = get(index);
			if(null == value){
				return null;
			}else if(value instanceof Map){
				return new ParamsObject((Map)value);
			}else if(value.getClass().isArray()){
				return new ParamsObject((Object[])value);
			}else{
				throw new MvcException(value.getClass() + " is not a map or array");
			}
		}
	}
}