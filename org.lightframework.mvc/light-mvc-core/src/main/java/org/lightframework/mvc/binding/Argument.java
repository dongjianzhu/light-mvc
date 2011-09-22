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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * represents an action execution argument
 * @since 1.0.0 
 */
public class Argument {
	protected Object       value;
	protected boolean      binded;
	public static final Annotation[] EMPTY_CONFIGS = new Annotation[0];
	
	protected String       name;
	protected Class<?>     type;
	protected Type         genericType;
	protected String       format;
	protected String       defaultValue;		
	protected Annotation[] configs = EMPTY_CONFIGS;
	
	public Argument() {
        super();
    }

	public Argument(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }
	
	public Argument(String name, Class<?> type, Annotation[] configs) {
        this(name,type);
        this.configs = configs;
    }		

	public String getName(){
		return name;
	}
	
	public Class<?> getType() {
    	return type;
    }
	
	public boolean isParameterizedType(){
		return genericType instanceof ParameterizedType;
	}
	
	/**
	 * @see ParameterizedType#getActualTypeArguments()
	 */
	public Type[] getActualTypeArguments(){
		return ((ParameterizedType)genericType).getActualTypeArguments();
	}
	
	public Type getGenericType() {
    	return genericType;
    }

	public void setGenericType(Type genericType) {
    	this.genericType = genericType;
    }

	public String getFormat() {
    	return format;
    }

	public void setFormat(String format) {
    	this.format = format;
    }

	public String getDefaultValue() {
    	return defaultValue;
    }

	public void setDefaultValue(String defaultValue) {
    	this.defaultValue = defaultValue;
    }

	public Annotation[] getConfigs() {
		if(null == configs){
			return EMPTY_CONFIGS;
		}
    	return configs;
    }	

	public void setName(String name) {
    	this.name = name;
    }
	
	public void setType(Class<?> type) {
    	this.type = type;
    }
	
	public Object getValue() {
    	return value;
    }
	
	public void binding(Object value){
		this.value  = value;
		this.binded = true;
	}
	
	public boolean isBinded(){
		return binded;
	}

	public void setConfigs(Annotation[] annotations) {
    	this.configs = annotations;
    }
}
