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

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * represents a mvc application
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class Application {
	private static final String DEFAULT_APPLICATION_NAME = "default";

	protected String              name       = DEFAULT_APPLICATION_NAME;
	protected String              encoding   = "UTF-8";
	
	private   Object              context    = new Object();
	private   Module              root       = null;
	private   LinkedList<Module>  modules    = new LinkedList<Module>();
	private   Map<Object, Object> attributes = new ConcurrentHashMap<Object, Object>();
	
	public Application(){
		
	}
	
	public Application(Object context,Module root){
		this.context = context;
		this.root    = root;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
    	this.name = name;
    }
	
	public String getEncoding() {
    	return encoding;
    }

	public void setEncoding(String encoding) {
    	this.encoding = encoding;
    }

	/**@return External Applicatoin Context Object,such as {@link javax.servlet.ServletContext} */
	public Object getContext() {
		return context;
	}
	
	public Module getRootModule(){
		return root;
	}
	
	public LinkedList<Module> getChildModules(){
		return modules;
	}

	public Map<Object, Object> getAttributes() {
		return attributes;
	}
	
	public void setAttribute(Object name,Object value){
		attributes.put(name,value);
	}
	
	public Object getAttribute(Object name){
		return attributes.get(name);
	}
	
	public Object removeAttribute(Object name){
		return attributes.remove(name);
	}
}
