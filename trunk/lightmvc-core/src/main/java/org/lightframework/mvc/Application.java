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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

/**
 * represents a mvc web application
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 0.1
 */
public class Application {

    private static final long serialVersionUID = 3816496065687999477L;
    
    public static final String DEFAULT_ENCODING = "UTF-8";
    
    protected ServletContext servletContext;
    
    protected String _package;
    
    protected String rootPath;
   
    protected String encoding = DEFAULT_ENCODING;
    
    protected Map<String, Object> parameters = new HashMap<String, Object>();
    
    protected LinkedList<Plugin> plugins = new LinkedList<Plugin>();
    
    void start(){
    	//TODO : load application plugins
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
	
	public String getRootPath() {
    	return rootPath;
    }
	
	public String getEncoding(){
		return encoding;
	}
	
	public Map<String, Object> getParameters(){
		return parameters;
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getResourcePaths(String path){
		return servletContext.getResourcePaths(path);
	}

	List<Plugin> getPlugins(){
		return plugins;
	}

	/*
	public class Installer {
		public static final String LOCATION_FIRST = "first";
		public static final String LOCATION_LAST  = "last";
		
		public void install(Plugin plugin,String location){
			if(LOCATION_FIRST.equals(location)){
				plugins.addFirst(plugin);
			}else{
				plugins.addLast(plugin);
			}
		}
		
		public void install(List<Plugin> plugins,String location){
			if(LOCATION_FIRST.equals(location)){
				for(int i=plugins.size() - 1;i>=0;i--){
					Application.this.plugins.addFirst(plugins.get(i));
				}
			}else{
				Application.this.plugins.addAll(plugins);
			}
		}
	}
	*/
}
