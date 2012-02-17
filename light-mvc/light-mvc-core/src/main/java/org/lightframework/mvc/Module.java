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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.lightframework.mvc.clazz.ClassFinder;
import org.lightframework.mvc.clazz.ClassUtils;
import org.lightframework.mvc.clazz.ClazzLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * represents a mvc web module
 *
 * @author fenghm(live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class Module {

    private static final long serialVersionUID = 3816496065687999477L;
    
    private static final Logger log = LoggerFactory.getLogger(Module.class);
    
    public static final String DEFAULT_NAME     = "root";
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_PACKAGE  = "app";
    public static final String DEFAULT_ROOTPATH = "/";
    public static final String DEFAULT_VIEWPATH = "/modules";
    
    private   boolean  started  = false;
    protected String   name     = DEFAULT_NAME;
    protected String   encoding = DEFAULT_ENCODING;
    protected String   packagee = DEFAULT_PACKAGE;
    protected String   rootPath = DEFAULT_ROOTPATH;
    protected String   viewPath = DEFAULT_VIEWPATH;

    protected ClassLoader classLoader    = null;
    protected String resourceForFinding  = null;
    protected LinkedList<Plugin> plugins = new LinkedList<Plugin>();
    protected ConcurrentHashMap<String, Object> controllers = new ConcurrentHashMap<String, Object>();
    
    //used to cache classes names
    protected long        lastFindClassesTime;
    protected Set<String> classNames;
    
    protected final void start(){
    	if(!started){
    		config();
    		started = true;
    	}else{
    		throw new MvcException("module '" + getName() + "' aleady started");
    	}
    }

    protected final void stop(){
    	if(started){
	    	//unload plugins
	    	for(Plugin plugin : plugins){
	    		try{
	    			plugin.unload();
	    		}catch(Exception e){
	    			log.error("[module:{}] -> unload plugin '{}' error", plugin.getName(),e);
	    		}
	    	}
	    	started = false;
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
    
	public String getPackagee() {
    	return packagee;
    }
	
	public void setAnyOneClassInModule(Class<?> clazz) {
    	this.resourceForFinding = clazz.getName().replace('.', '/') + ".class";
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
		Collection<String> classes = getModuleClassNames();
		
		for(String name : names){
			for(String clazz : classes){
				if(clazz.equalsIgnoreCase(name)){
					if(log.isDebugEnabled()){
						log.debug("[module:{}] -> found class name '{}'",getName(),clazz);
					}
					return loadClassForName(clazz);
				}
			}
		}
		
		if(log.isDebugEnabled()){
			log.debug("[module:{}] -> no class found with the given names",getName());
		}
		
		return null;
	}
	
	public Object getControllerObject(String controllerName,Class<?> controllerClass){
		String key = controllerClass.getName() + "!" + controllerName;
		Object obj = controllers.get(key);
		
		if(null != obj && !obj.getClass().equals(controllerClass)){
			obj = null; //XXX : may be class reloaded
		}
		
		if(null == obj){
			try {
				if(log.isTraceEnabled()){
					log.trace("[module:{}] -> create controller '{}' instance of '{}'",
							  new Object[]{getName(),controllerName,controllerClass.getName()});
				}
	            obj = controllerClass.newInstance();
            } catch (Exception e) {
            	throw new MvcException("[module:" + getName() + "] -> new controller instance error : " + e.getMessage(),e);
            }
			controllers.put(key, obj);
		}
		return obj;
	}
	
	/**
	 * @return {@link ClassLoader} in this web module,default is {@link Thread#currentThread()#getClassLoader()};
	 */
	public ClassLoader getClassLoader(){
		if(null == classLoader){
			synchronized (this) {
				classLoader = new ClazzLoader();    
            }
		}
		return classLoader;
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
        	log.debug("[module:{}] -> class '{}' not found",getName(),className);
        }
        return clazz;
	}
	
	/**
	 * find the controller's path(the folder contains action's view),if not found,return null
	 */
	public String findControllerPath(Action action){
		String controller = action.getControllerName().replaceAll("\\.", "/");
		//guess the path : {module-view-path}/{controller}
		String root  = getViewPath();
		String path = root + (root.endsWith("/") ? "" : "/") + controller + "/";
		
		URL url = findWebResource(path);
		if(null == url && action.isHome()){
			//guess the path of home controller : {module-view-path}
			path = root;
			url = findWebResource(path);
			if(null == url && action.isHome() && !root.equals(getRootPath())){
				//guess the path of home controller : {module-root-path}
				path = getRootPath();
			}		
			if(!path.endsWith("/")){
				path = path + "/";
			}
		}else if(null == url){
			path = null;
		}
		
		return path;
	}
	
	public View findView(Action action){
		String controller = action.getControllerName().replaceAll("\\.", "/");
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
	
	protected void config(){
		//XXX : load module configuration
		boolean loaded = false;
		Class<?> clazz = findClass(packagee + ".home");
		if(null != clazz){
			try {
	            Method method = ClassUtils.findMethodIgnoreCase(clazz, "config");
	            if(null != method){
	            	if(method.getParameterTypes().length > 0){
	            		log.warn("[module:{}] -> config method in class '{}' should had no parameters",getName(),clazz.getName());
	            		return ;
	            	}
	            	
	    			log.debug("[module:{}] -> found config method in home controller : '{}'",getName(),clazz.getName());

	    			if(Modifier.isPublic(method.getModifiers())){
	    				log.debug("[module:{}] -> call the config method to config module",getName());
	    				try {
	                        if(Modifier.isStatic(method.getModifiers())){
	                        	method.invoke(null, new Object[]{});  					
	                        }else{
	                        	Object object = getControllerObject("home", clazz);
	                        	method.invoke(object, new Object[]{});
	                        }
	                        loaded = true;
                        } catch (Exception e) {
                        	log.error("[module:{}] -> error calling config method ",getName(),e);
                        }
	    			}else{
	    				log.debug("[module:{}] -> config method is not public,ignore it",getName());
	    			}
	            }
            } catch (IOException e) {
            	log.warn("[module:{}] -> find config method error",getName(),e);
            }
		}
		
		if(!loaded){
			log.info("[module:{}] -> configuration not found",getName());
		}else{
			log.info("[module:{}] -> configuration was loaded",getName());
		}		
	}
	
	protected String findView(String path,String controller,String action){
		//TODO : improve performance in production mode
		if(log.isTraceEnabled()){
			log.trace("[module:{}] -> try to find view in path '{}'",getName(),path);
		}
		
		Collection<String> resources = findWebResources(path);
		if(null != resources && !resources.isEmpty()){
			
			if(log.isTraceEnabled()){
				log.trace("[module:{}] -> find {} resources",getName(),resources.size());
			}
			
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
			
			if(log.isTraceEnabled()){
				if(null == found){
					log.trace("[module:{}] -> no matched view of action '{}' found",getName(),action);
				}else{
					log.trace("[module:{}] -> found matched view '{}'",getName(),found);
				}
			}
		
			return found;
		}
		
		if(log.isTraceEnabled()){
			log.trace("[module:{}] -> no resources found",getName());
		}
		
		return null;
	}
	
	protected String findMatchedView(Collection<String> resources,String tofind,boolean startsWith) {
		if(log.isTraceEnabled()){
			log.trace("[module:{}] -> to find matched view '{}'",getName(),tofind);
		}
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
	
	protected URL findWebResource(String path){
		//XXX: implement findWebResource
		return null;
	}
	
	protected Collection<String> findWebResources(String path){
		//XXX: implement findWebResources
		log.warn("[module:{}] -> not implemented method : findWebResources",getName());
		return new ArrayList<String>();
	}
	
	/**
	 * @return all the class names in this module and the {@link #packages} 
	 */
	protected Set<String> getModuleClassNames() {
		long now = System.currentTimeMillis();
		if(null == classNames || now - lastFindClassesTime > 10000){
			synchronized (this) {
				if(null != classNames){
					classNames.clear();
					classNames = null;
				}
				
				try {
            		classNames = findModuleClassNames();

                	if(log.isTraceEnabled()){
	                	log.trace("[module:{}] -> found {} classes in package '{}'",
	                			  new Object[]{getName(),classNames.size(),packagee}
	                	);
                	}
                } catch (IOException e) {
                	throw new MvcException("error find classes in module '" + getName() + "'",e);
                }
				
				lastFindClassesTime = now;
            }
		}
		return classNames;
	}
	
	public LinkedList<Plugin> getPlugins(){
		return plugins;
	}
	
	protected Set<String> findModuleClassNames() throws IOException{
		Set<String> names = new HashSet<String>();
		
		String path = packagee.replace('.', '/');
		URL    url1 = null != resourceForFinding ? getClassLoader().getResource(resourceForFinding) : null;

		Enumeration<URL> urls = getClassLoader().getResources(path);
		while(urls.hasMoreElements()){
			URL url = urls.nextElement();
			
			//is the same url ?
			if(null != url1 && url1.getPath().startsWith(url.getPath())){
				url1 = null;
			}
			
			Set<String> found = ClassFinder.findClassNames(url, packagee);
			if(log.isTraceEnabled()){
				log.trace("[module:{}] -> found {} classes in url : '{}'",
						  new Object[]{getName(),found.size(),url.getPath()});
			}
			names.addAll(found);
		}

		if(null != url1){
			Set<String> found = ClassFinder.findClassNames(url1, packagee);
			if(log.isTraceEnabled()){
				log.trace("[module:{}] -> found {} classes in url : '{}'",
						  new Object[]{getName(),found.size(),url1.getPath()});
			}
			names.addAll(found);
		}
		
		return names;
	}
}
