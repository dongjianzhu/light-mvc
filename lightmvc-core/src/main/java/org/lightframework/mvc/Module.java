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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.lightframework.mvc.utils.ClassUtils;

/**
 * represents a mvc web module
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0
 */
public class Module {

    private static final long serialVersionUID = 3816496065687999477L;
    
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String DEFAULT_PACKAGE  = "app";
    
    protected ServletContext servletContext;
    
    protected String _package = DEFAULT_PACKAGE;
    
    protected String encoding = DEFAULT_ENCODING;
    
    protected Map<String, Object> parameters = new HashMap<String, Object>();
    
    protected LinkedList<Plugin> plugins = new LinkedList<Plugin>();
    
    void start(){
    	//TODO : load module plugins
    	
    }

    void stop(){
    	//unload plugins
    	for(Plugin plugin : plugins){
    		try{
    			plugin.unload();
    		}catch(Throwable e){
    			Logger.error(e,"@Plugin.UnloadError", plugin.getClass().getName());
    		}
    	}
    }
    
	public String getPackage() {
    	return _package;
    }
	
	public String getEncoding(){
		return encoding;
	}
	
	public Map<String, Object> getParameters(){
		return parameters;
	}
	
	/**
	 * load {@link Clazz} by name ignore case.
	 * @return a {@link Clazz} object that contains the {@link Class} matched the name ignorecase,else null
	 */
	public Clazz getClazz(String name){
		try {
	        String className = ClassUtils.findClassNameIgnoreCase(name);
	        if(null != className){
	        	return new Clazz(ClassUtils.forName(className));
	        }
	        return null;
        } catch (IOException e) {
        	throw new MVCException(e);
        }
	}
	
	public Collection<Clazz> getClasses(){
		return getClasses(null);
	}
	
	public Collection<Clazz> getClasses(String _package){
		return getClasses(_package,false);
	}

	public Collection<Clazz> getClasses(String _package,boolean recursion) {
		// TODO : Module.getClasses
		return null;
	}
	
	public Collection<String> getClassNames(String _package,boolean recursion) {
		try {
	        return ClassUtils.findAllClassNames(_package);
        } catch (IOException e) {
        	throw new MVCException(e);
        }
	}
	
	public String getView(String path){
		return null;
	}
	
	public Collection<String> getViews(){
		return getViews(null);
	}
	
	public Collection<String> getViews(String path){
		return getViews(path,false);
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getViews(String path,boolean recursion){
		// TODO : Module.getViews
		return servletContext.getResourcePaths(path);
	}

	protected List<Plugin> getPlugins(){
		return plugins;
	}

	/**
	 * @since 1.0
	 */
	public static final class Clazz {
		protected Class<?> _class;
		protected long lastReloaded;
		protected long lastModified;
		
		Clazz(Class<?> _class){
			this._class = _class;
		}
		
		public Class<?> Class(){
			return _class;
		}
		
		public boolean isModified(){
			return lastModified != lastReloaded;
		}
		
		public boolean isPlugin(){
			return Plugin.class.isAssignableFrom(_class);
		}
	}
}
