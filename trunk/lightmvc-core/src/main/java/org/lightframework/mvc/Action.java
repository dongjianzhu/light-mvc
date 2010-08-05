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

import org.lightframework.mvc.Lang.Type;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * definition of an action. <p>
 * 
 * @author fenghm(live.fenghm@gmail.com)
 * @since  1.0.0
 */
public class Action {
    private static final Argument[] EMPTY_ARGUMENTS   = new Argument[]{};

	protected String             name;
    protected Class<?>           controllerClass;
    protected Object             controllerObject;
	protected Method             method;
	protected Map<String,Object> parameters;
	protected Argument[]         arguments;
    
    private boolean              home;
    private boolean              index;
    private boolean              resolved;
    private boolean              binded;

	public String getName() {
    	return name;
    }

	public boolean isResolved() {
    	return resolved;
    }

	public boolean isBinded() {
    	return binded;
    }

	public boolean isHome() {
    	return home;
    }
	
	public boolean isIndex() {
    	return index;
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
	
	public Class<?> getControllerClass() {
    	return controllerClass;
    }

	public Object getControllerObject() {
    	return controllerObject;
    }

	public Class<?> getReturnType(){
		return null != method ? method.getReturnType() : null;
	}

	public Method getMethod() {
    	return method;
    }

	void onRouted(){
		Assert.notNull("action.name", name);
	}
	
	void onBinded(){
		
	}
	
    void onResolved() {
    	if(resolved){
	        Assert.notNull("controllerClass", controllerClass);
	        Assert.notNull("method", method);
	
	        if(!ClassUtils.isStatic(method)){
	        	Assert.notNull("controllerObject", controllerObject);
	        }
    	}
    }
	
	void onExecuted(){
		
	}
	
	/**
	 * used to set the protected properties of an {@link Action} object.
	 * 
	 * @since 1.0.0
	 */
	public static final class Setter {
		
		public static void setName(Action action,String name) {
			action.name = name;
	    }
		
		public static void setControllerObject(Action action,Object controllerObject) {
			action.controllerObject = controllerObject;
	    }
		
		public static void setMethod(Action action,Method actionMethod) {
			action.method = actionMethod;
	    }
		
		public static void setBinded(Action action, boolean binded) {
			action.binded = binded;
	    }		
		
		public static void setResolved(Action action,boolean resolved) {
			action.resolved = resolved;
	    }
		
		public static void setControllerClass(Action action, Class<?> controllerClass) {
			action.controllerClass = controllerClass;
	    }
		
		public static void setHome(Action action,boolean home){
			action.home = home;
		}
		
		public static void setIndex(Action action,boolean index){
			action.index = index;
		}
	}
	
	/**
	 * represents an action execution argument
	 * @since 1.0.0 
	 */
	public static class Argument extends Type{
		protected Object       value;
		protected boolean      binded;
		
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
}

