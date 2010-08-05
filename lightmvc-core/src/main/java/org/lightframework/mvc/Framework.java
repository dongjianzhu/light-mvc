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

/**
 * the main class of mvc framework
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
class Framework {
	
	//mvc framework version
	public static final String VERSION = getVersion();
	
	/** represents was framework initialized ?*/
	private static boolean initialized = false;
	
	/** core framework plugins */
	private static final ArrayList<Plugin> plugins = new ArrayList<Plugin>();
	
	/**
	 * start the module
	 */
	public static void start(Module module){
		if(!initialized){
			synchronized (Framework.class) {
	            if(!initialized){
	            	init();
	            	initialized = true;
	            }
            }
		}
		module.start();
	}
	
	/**
	 * stop the module
	 */
	public static void stop(Module module){
		module.stop();
	}
	
	/**
	 * is this request ignored by current module
	 */
	public static boolean ignore(Request request){
		return false;
	}

	/**
	 * handle a http request in mvc framework
	 * @param request      mvc http request
	 * @param response     mvc http response
	 * @return true if mvc framework has handled this request
	 */
	public static boolean handle(Request request,Response response) throws Throwable {
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
						request.action = action;
						
						action.onRouted();
						
						managed = invokeAction(request,response,action);
					}
				}
				return managed;
			}catch(Return e){
				return PluginInvoker.render(request, response, e.result());
			}
		}catch(Throwable e){
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
	
	private static boolean invokeAction(Request request,Response response,Action action) throws Throwable{
		//resolving action method
		if(!action.isResolved()){
			action.setResolved(PluginInvoker.resolve(request,response,action));
		}
		action.onResolved();
		
		if(action.isResolved()){
			//binding method arguments
			if(!action.isBinded()){
				PluginInvoker.binding(request, response, action);
				action.setBinded(true);
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
		}
		return false;
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
	 * @since 1.0 
	 */
	private static final class PluginInvoker{
		static boolean request(Request request,Response response) throws Throwable{
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.request(request, response)){
					return true;
				}
			}
			for(Plugin plugin : plugins){
				if(plugin.request(request, response)){
					return true;
				}
			}
			return false;
		}
		
		static Action route(Request request,Response response) throws Throwable{
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
		
		static boolean resolve(Request request,Response response,Action action) throws Throwable{
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
		
		static boolean binding(Request request,Response response,Action action) throws Throwable{
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
		
		static Result execute(Request request,Response response,Action action) throws Throwable{
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
		
		static boolean render(Request request,Response response,Result result) throws Throwable{
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
		
		static boolean exception(Request request,Response response,Throwable e) throws Throwable{
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
