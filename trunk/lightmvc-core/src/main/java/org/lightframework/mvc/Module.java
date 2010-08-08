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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.lightframework.mvc.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents a mvc web module
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class Module {

    private static final long serialVersionUID = 3816496065687999477L;
    
    private static final Logger log = LoggerFactory.getLogger(Module.class);
    
    public static final String DEFAULT_NAME     = "default";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_PACKAGE  = "app";
    public static final String DEFAULT_ROOTPATH = "/";
    public static final String DEFAULT_VIEWPATH = "/modules";
    
    private   boolean  started  = false;
    protected String   name     = DEFAULT_NAME;
    protected String   encoding = DEFAULT_ENCODING;
    protected String[] packages = new String[]{DEFAULT_PACKAGE};
    protected String   rootPath = DEFAULT_ROOTPATH;
    protected String   viewPath = DEFAULT_VIEWPATH;

    protected LinkedList<Plugin> plugins = new LinkedList<Plugin>();
    
    //used to cache classes names
    private long lastFindClassesTime;
    private Collection<String> classNames;
    
    final void start(){
    	if(!started){
    		//TODO : load module plugins
    		started = true;
    	}else{
    		throw new MvcException("module '" + getName() + "' aleady started");
    	}
    }

    final void stop(){
    	if(started){
	    	//unload plugins
	    	for(Plugin plugin : plugins){
	    		try{
	    			plugin.unload();
	    		}catch(Exception e){
	    			log.error("module '{}' -> unload plugin '{}' error", plugin.getName(),e);
	    		}
	    	}
    	}else{
    		throw new MvcException("module '" + getName() + "' not started");
    	}
    }
    
    public boolean isStarted(){
    	return started;
    }
    
    public String getName(){
    	return name;
    }
    
    public String[] getPackages(){
    	return packages;
    }
	
	public String getEncoding(){
		return encoding;
	}
	
	public String getRootPath(){
		return rootPath;
	}
	
	public String getViewPath(){
		return viewPath;
	}
	
	public Class<?> findClass(String name) {
		return findClass(new String[]{name});
	}
	
	public Class<?> findClass(String[] names) {
		//XXX : improve performance in production mode 
		Collection<String> classes = findAllClassNames();
		
		for(String clazz : classes){
			for(String name : names){
				if(clazz.equalsIgnoreCase(name)){
					if(log.isTraceEnabled()){
						log.trace("[module:'{}'] -> found class name '{}'",getName(),clazz);
					}
					return loadClassForName(clazz);
				}
			}
		}
		
		if(log.isTraceEnabled()){
			log.trace("[module:'{}'] -> no class found with the given names",getName());
		}
		
		return null;
	}
	
	/**
	 * @return {@link ClassLoader} in this web module,default is {@link Thread#currentThread()#getClassLoader()};
	 */
	public ClassLoader getClassLoader(){
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if(null == loader){
			loader = Module.class.getClassLoader();
		}
		return loader;
	}
	
	/**
	 * @see Class#forName(String);
	 * 
	 * @return {@link Class} object of the given class name, return null if {@link ClassNotFoundException} occurs.
	 */
	public Class<?> loadClassForName(String className) {
		Class<?> clazz = null;
		try {
	        clazz = getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
        	log.debug("[module:'{}'] -> class '{}' not found",getName(),className);
        }
        return clazz;
	}
	
	public View findView(Action action){
		//XXX : is support controller fromat such as product.category ? 
		String controller = action.getControllerName();
		String actionName = action.getSimpleName();
		
		//guess the path : {module-view-path}/{controller}
		String root  = getViewPath();
		String path = root + (root.endsWith("/") ? ""  : "/") + controller;
		String view = findView(path, controller, actionName);
		
		if(null == view && action.isHome()){
			
			//guess the path of home controller : {module-view-path}
			path = root;
			view = findView(path, controller, actionName);
			
			if(null == view && action.isHome() && !root.equals(getRootPath())){
				//guess the path of home controller : {module-root-path}
				root = getRootPath();
				path = root.endsWith("/") ? root.substring(0,root.length() - 1) : root;
				view = findView(path, controller, actionName);
			}
		}

		if(null != view){
			return new View(view);
		}
		
		return null;
	}
	
	protected String findView(String path,String controller,String action){
		Collection<String> resources = findWebResources(path);
		if(null != resources && !resources.isEmpty()){
			String prefix = path + "/" + action + ".";
			String view   = prefix + "jsp";
			String found  = findMatchedView(resources, view, false);
			
			if(null == found){
				view  = prefix + "html";
				found = findMatchedView(resources, view, false);
			}
			
			if(null == found){
				view  = prefix + "htm";
				found = findMatchedView(resources, view, false);
			}

			if(null == found){
				found = findMatchedView(resources, prefix, true);
			}
		
			return found;
		}
		return null;
	}
	
	protected String findMatchedView(Collection<String> resources,String tofind,boolean startsWith) {
		for(String resource : resources){
			if(startsWith){
				if(resource.startsWith(tofind)){
					return resource;
				}
			}else{
				if(resource.equals(tofind)){
					return resource;
				}
			}
		}
		return null;
	}
	
	protected Collection<String> findWebResources(String path){
		//XXX: implement findWebResources
		log.warn("[module:'{}'] -> not implemented method : findWebResources",getName());
		return new ArrayList<String>();
	}
	
	/**
	 * @return all the class names in this module and the {@link #packages} 
	 */
	protected Collection<String> findAllClassNames() {
		long now = System.currentTimeMillis();
		if(null == classNames || now - lastFindClassesTime > 10000){
			synchronized (this) {
				if(null != classNames){
					classNames.clear();
					classNames = null;
				}
				
				try {
	                for(String pkg : getPackages()){
	                	Collection<String> foundClassNames = ClassUtils.findAllClassNames(getClassLoader(), pkg);
	                	if(null == classNames){
	                		classNames = foundClassNames;
	                	}else{
	                		classNames.addAll(foundClassNames);
	                	}

	                	if(log.isTraceEnabled()){
		                	log.trace("[module:'{}'] -> found {} classes in package '{}'",
		                			  new Object[]{getName(),foundClassNames.size(),pkg}
		                	);
	                	}
	                }
                } catch (IOException e) {
                	throw new MvcException("error find classes in module '" + getName() + "'",e);
                }
				
				lastFindClassesTime = now;
            }
		}
		return classNames;
	}
	
	protected List<Plugin> getPlugins(){
		return plugins;
	}
}
