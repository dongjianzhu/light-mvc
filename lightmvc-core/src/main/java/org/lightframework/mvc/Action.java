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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.lightframework.mvc.Utils.Assert;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * definition of an action. <p>
 * 
 * @author light.wind(lightworld.me@gmail.com)
 * @since  1.0
 */
public class Action {
    private static final Argument[]   EMPTY_ARGUMENTS   = new Argument[]{};
	private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[]{};

	protected String             name;
    protected Class<?>           clazz;
	protected Method             method;
	protected Map<String,Object> parameters;
	protected Argument[]         arguments;
    protected Object             controller;
    
	protected boolean            resolved;
	protected boolean            binded;

	public String getName() {
    	return name;
    }

	public void setName(String name) {
    	this.name = name;
    }
	
	public boolean isResolved() {
    	return resolved;
    }

	public void setResolved(boolean resolved) {
    	this.resolved = resolved;
    }
	
	public boolean isBinded() {
    	return binded;
    }

	public void setBinded(boolean binded) {
    	this.binded = binded;
    }

	public Object getParameter(String name){
		return getParameters().get(name);
	}
	
	public void setParameter(String name,Object value){
		getParameters().put(name, value);
	}
	
	public Map<String,Object> getParameters(){
		if(null == parameters){
			parameters = new HashMap<String,Object>();
		}
		return parameters;
	}
	
	public void setParameters(Map<String,Object> parameters){
		this.parameters = parameters;
	}
	
	public Argument getArgument(String name){
		for(Argument arg : getArguments()){
			if(name.equals(arg.name)){
				return arg;
			}
		}
		return null;
	}
	
	public Argument[] getArguments() {
		if(null == arguments){
			arguments = EMPTY_ARGUMENTS;
		}
    	return arguments;
    }

	public void setArguments(Argument[] args) {
    	this.arguments = args;
    }
	
	public Class<?> getClazz() {
    	return clazz;
    }

	public void setClazz(Class<?> controllerClass) {
    	this.clazz = controllerClass;
    }

	public Object getController() {
    	return controller;
    }

	public void setController(Object controllerObject) {
    	this.controller = controllerObject;
    }

	public Class<?> getReturnType(){
		return null != method ? method.getReturnType() : null;
	}

	public Method getMethod() {
    	return method;
    }

	public void setMethod(Method actionMethod) {
    	this.method = actionMethod;
    }

	void afterRouting(){
		Assert.notNull("action.name", name);
	}
	
	void afterBinding(){
		
	}
	
    void afterResolving() {
        Assert.notNull("controllerClass", clazz);
        Assert.notNull("method", method);

        if(!ClassUtils.isStatic(method)){
        	Assert.notNull("controllerObject", controller);
        }
    }
	
	void afterExecuting(){
		
	}
	
	/**
	 * represents an action execution argument
	 * @since 1.0 
	 */
	public static class Argument {
		protected String       name;
		protected Class<?>     type;
		protected Object       value;
		protected Annotation[] configs;
		protected boolean      binded;
		
		public String getName() {
        	return name;
        }
		
		public void setName(String name) {
        	this.name = name;
        }
		
		public Class<?> getType() {
        	return type;
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

		public Annotation[] getConfigs() {
			if(null == configs){
				return EMPTY_ANNOTATIONS;
			}
        	return configs;
        }

		public void setConfigs(Annotation[] annotations) {
        	this.configs = annotations;
        }
	}
}

