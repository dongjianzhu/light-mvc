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

import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.Result.Return;
import org.lightframework.mvc.core.CorePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the main class of mvc framework
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class Framework {
	private static final Logger log = LoggerFactory.getLogger(Framework.class);
	
	//mvc framework version
	public static final String VERSION = getVersion();
	
	/** represents was framework initialized ?*/
	private static boolean initialized = false;
	
	/** core framework plugins */
	private static final ArrayList<Plugin> plugins = new ArrayList<Plugin>();
	
	/**
	 * start the module
	 */
	protected static void start(Module module){
		if(!initialized){
			log.info("[mvc] -> initializing... ");
			synchronized (Framework.class) {
	            if(!initialized){
	            	init();
	            	initialized = true;
	            }
            }
			log.info("[mvc] -> initialized!");
		}
		log.debug("[module:'{}'] -> starting...",module.getName());
		module.start();
		log.debug("[module:'{}'] -> started!",module.getName());
	}
	
	/**
	 * stop the module
	 */
	protected static void stop(Module module){
		log.debug("[module:'{}'] -> stopping...",module.getName());
		module.stop();
		log.debug("[module:'{}'] -> stopped!",module.getName());
		
		//release all context data
		Result.Context.release();
	}
	
	/**
	 * is this request ignored by current module
	 */
	protected static boolean ignore(Request request){
		// TODO : Framework.ignore
		return false;
	}

	/**
	 * handle a http request in mvc framework
	 * @param request      mvc http request
	 * @param response     mvc http response
	 * @return true if mvc framework has handled this request
	 */
	protected static boolean handle(Request request,Response response) throws Exception {
		Assert.notNull("request.module", request.getModule());
		
		//is hanlded by mvc framework
		boolean managed = false;
		
		try{
			Request.current.set(request);
			
			try{
				//is this request managed by plugins
				managed = PluginInvoker.request(request, response);

				if(!managed){
					//if not managed by plugin , mapping this request to action
					Action action = PluginInvoker.route(request, response);
					
					if(null != action){
						if(log.isTraceEnabled()){
							log.trace("[action] -> route uri '{}' to action '{}'",request.getUriString(),action.getName());
						}
						
						request.action = action;
						
						action.onRouted();
						
						managed = invokeAction(request,response,action);
						
						
					}else{
						if(log.isTraceEnabled()){
							log.trace("[action] -> not found for uri '{}'",request.getUriString());
						}
					}
				}
				return managed;
			}catch(Return e){
				return PluginInvoker.render(request, response, e.result());
			}
		}catch(Exception e){
			//handle exception
			if(!PluginInvoker.exception(request, response, e)){
				throw e;
			}
		}finally {
			Request.current.set(null);
		}
		
		return managed;
	}
	
	private static void init(){
		//load core framework plugins
		plugins.add(new CorePlugin());
	}
	
	private static boolean invokeAction(Request request,Response response,Action action) throws Exception{
		//resolving action method
		if(!action.isResolved()){
			Action.Setter.setResolved(action,PluginInvoker.resolve(request,response,action));
		}
		action.onResolved();
		
		if(action.isResolved()){
			if(log.isTraceEnabled()){
				log.trace("[action:'{}'] -> resolved as '{}${}'",
						  new Object[]{
							  action.getName(),
							  action.getControllerClass().getName(),
							  action.getMethod().getName()
						  });
			}
			
			//binding method arguments
			if(!action.isBinded()){
				PluginInvoker.binding(request, response, action);
				Action.Setter.setBinded(action,true);
			}
			action.onBinded();
			
			//executing action method
			Result result = PluginInvoker.execute(request, response, action);
			action.onExecuted();
			
			//render action result
			boolean rendered = PluginInvoker.render(request, response, result);
			if(!rendered && !action.isResolved()){
				return false;
			}else{
				return true;
			}
		}else{
			//TODO : handle action cant not resolved.
			if(log.isTraceEnabled()){
				log.trace("[action:'{}'] -> can not resolved",action.getName());
			}
			return false;
		}
	}
	
	private static String getVersion(){
		//read version string from /org/lightframework/mvc/version.txt 
		try {
	        return Utils.readFromResource("/" + Framework.class.getPackage().getName().replaceAll("\\.", "/") + "/version").trim();
        } catch (IOException e) {
        	throw new MvcException("error reading version",e);
        }
	}
	
	/**
	 * @since 1.0.0 
	 */
	private static final class PluginInvoker{
		static boolean request(Request request,Response response) throws Exception{
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.request(request, response)){
					log.trace("request managed by plugin '{}'",plugin.getName());
					return true;
				}
			}
			for(Plugin plugin : plugins){
				if(plugin.request(request, response)){
					log.trace("request managed by plugin '{}'",plugin.getName());
					return true;
				}
			}
			return false;
		}
		
		static Action route(Request request,Response response) throws Exception{
			for(Plugin plugin : request.getModule().getPlugins()){
				Action action = plugin.route(request, response);
				if(null != action){
					return action;
				}
			}			
			for(Plugin plugin : plugins){
				Action action = plugin.route(request, response);
				if(null != action){
					return action;
				}
			}
			return null;
		}
		
		static boolean resolve(Request request,Response response,Action action) throws Exception{
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.resolve(request, response, action)){
					return true;
				}
			}
			for(Plugin plugin : plugins){
				if(plugin.resolve(request, response, action)){
					return true;
				}
			}
			return false;
		}
		
		static boolean binding(Request request,Response response,Action action) throws Exception{
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.binding(request, response, action)){
					return true;
				}
			}			
			for(Plugin plugin : plugins){
				if(plugin.binding(request, response, action)){
					return true;
				}
			}
			return false;
		}
		
		static Result execute(Request request,Response response,Action action) throws Exception{
			for(Plugin plugin : request.getModule().getPlugins()){
				Result result = plugin.execute(request, response, action);
				if(null != result){
					return result;
				}
			}				
			for(Plugin plugin : plugins){
				Result result = plugin.execute(request, response, action);
				if(null != result){
					return result;
				}
			}				
			return null;
		}
		
		static boolean render(Request request,Response response,Result result) throws Exception {
			request.result = result;
			
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.render(request, response, result)){
					return true;
				}
			}			
			for(Plugin plugin : plugins){
				if(plugin.render(request, response, result)){
					return true;
				}
			}
			return false;
		}
		
		static boolean exception(Request request,Response response,Throwable e) throws Exception{
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.error(request, response, e)){
					return true;
				}
			}			
			for(Plugin plugin : plugins){
				if(plugin.error(request, response, e)){
					return true;
				}
			}			
			return false;
		}
	}
}
