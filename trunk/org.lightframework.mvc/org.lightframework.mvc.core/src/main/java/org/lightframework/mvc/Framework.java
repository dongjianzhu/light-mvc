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
	
	/** represents was framework initialized ?*/
	private static boolean initialized = false;
	
	/** core framework plugins */
	private static final ArrayList<Plugin> plugins = new ArrayList<Plugin>();
	
	/**
	 * start the module
	 */
	protected static void start(Module module){
		if(!initialized){
			log.info("[mvc] -> current version : '{}'",Version.version_string);
			log.info("[mvc:'{}'] -> initializing... ",Version.version_name);
			synchronized (Framework.class) {
	            if(!initialized){
	            	init();
	            	initialized = true;
	            }
            }
			log.info("[mvc:'{}'] -> initialized!",Version.version_name);
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
		Result.reset();
		Request.reset();
	}
	
	/**
	 * is this request ignored by current module
	 */
	protected static boolean ignore(Request request){
		// TODO : implement Framework.ignore
		return false;
	}

	/**
	 * handle a http request in mvc framework <p>
	 * 
	 * after you call this method , you must call {@link #handleFinally(Request, Response)} manual to clear context data.
	 * 
	 * @param request      mvc http request
	 * @param response     mvc http response
	 * @return true if mvc framework has handled this request
	 */
	protected static boolean handle(Request request,Response response) throws Exception {
		Assert.notNull("request.module", request.getModule());
		
		if(log.isTraceEnabled()){
			log.trace("[mvc] -> request path '{}' handling...",request.getPath());
		}
		
		//is hanlded by mvc framework
		boolean managed = false;
		
		try{
			Request.current.set(request);
			
			try{
				//is this request managed by plugins
				managed = PluginInvoker.request(request, response);

				if(!managed){
					//if not managed by plugin , mapping this request to action
					Action[] actions = PluginInvoker.route(request, response);
					
					if(actions.length > 0){
						for(Action action : actions){
							if(log.isTraceEnabled()){
								log.trace("[action] -> route uri '{}' to action '{}'",request.getUriString(),action.getName());
							}							
							action.onRouted();
						}
						
						//resolve action
						Action action = resolveAction(request, response, actions);
						if(null != action){
							Action.Setter.setResolved(action, true);
							action.onResolved();
							
							request.action = action;
							managed = invokeAction(request,response,action);
						}else{
							//TODO : handle not resolved action.
							return false;
						}
					}else{
						if(log.isTraceEnabled()){
							log.trace("[action] -> not found for uri '{}'",request.getUriString());
						}
					}
				}
			}catch(Return e){
				request.result = e.result();
				managed = PluginInvoker.render(request, response, e.result());
			}
		}catch(Exception e){
			//handle exception
			Result.Error error = new Result.Error(e.getMessage(),e);
			request.result     = error;
			if(!PluginInvoker.error(request, response, error)){
				throw e;
			}else{
				managed = true;
			}
		}
		
		if(log.isTraceEnabled()){
			log.trace("[mvc] -> request path '{}' managed is {}",request.getPath(),managed);
		}
		
		return managed;
	}
	
	protected static void handleFinally(Request request,Response response) {
		Result.reset();
		Request.reset();
	}
	
	private static void init(){
		//load core framework plugins
		plugins.add(new CorePlugin());
	}
	
	private static Action resolveAction(Request request,Response response,Action[] actions) throws Exception{
		for(Action action : actions){
			if(action.isResolved()){
				return action;
			}else if(PluginInvoker.resolve(request,response,action)){
				return action;
			}
			if(log.isTraceEnabled()){
				log.trace("[action:'{}'] -> can not resolved",action.getName());
			}			
		}
		return null;
	}
	
	private static boolean invokeAction(Request request,Response response,Action action) throws Exception{
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
		Result result = null;
		try{
			result = PluginInvoker.execute(request, response, action);
		}catch(Return e){
			result = e.result();
		}
		action.onExecuted();
		request.result = result;
		
		//render action result
		boolean rendered = PluginInvoker.render(request, response, result);
		if(!rendered && !action.isResolved()){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * @since 1.0.0 
	 */
	private static final class PluginInvoker{
		static boolean request(Request request,Response response) throws Exception{
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:requet] -> request handling...");
			}
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.request(request, response)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:request] -> request managed by '{}'",plugin.getName());
					}
					return true;
				}
			}
			for(Plugin plugin : plugins){
				if(plugin.request(request, response)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:request] -> request managed by '{}'",plugin.getName());
					}
					return true;
				}
			}
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:request] -> request not managed by any plugins");
			}
			return false;
		}
		
		static Action[] route(Request request,Response response) throws Exception{
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:route] -> routing action...");
			}
			for(Plugin plugin : request.getModule().getPlugins()){
				Action[] actions = plugin.route(request, response);
				if(null != actions && actions.length > 0){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:route] -> routed {} actions",actions.length);
					}
					return actions;
				}
			}			
			for(Plugin plugin : plugins){
				Action[] actions = plugin.route(request, response);
				if(null != actions && actions.length > 0){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:route] -> routed {} actions",actions.length);
					}					
					return actions;
				}
			}
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:route] -> routed 0 actions");
			}
			return Plugin.EMPTY_ACTIONS;
		}
		
		static boolean resolve(Request request,Response response,Action action) throws Exception{
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:resolve] -> resolving action...");
			}
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.resolve(request, response, action)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:resolve] -> resolved by '{}'",plugin.getName());
					}					
					return true;
				}
			}
			for(Plugin plugin : plugins){
				if(plugin.resolve(request, response, action)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:resolve] -> resolved by '{}'",plugin.getName());
					}
					return true;
				}
			}
			
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:resolve] -> resolving not done by any plugins");
			}
			
			return false;
		}
		
		static boolean binding(Request request,Response response,Action action) throws Exception{
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:binding] -> binding arguments...");
			}			
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.binding(request, response, action)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:binding] -> binded by '{}'",plugin.getName());
					}					
					return true;
				}
			}			
			for(Plugin plugin : plugins){
				if(plugin.binding(request, response, action)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:binding] -> binded by '{}'",plugin.getName());
					}					
					return true;
				}
			}
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:binding] -> binding not done by any plugins");
			}			
			return false;
		}
		
		static Result execute(Request request,Response response,Action action) throws Exception{
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:execute] -> executing action...");
			}			
			for(Plugin plugin : request.getModule().getPlugins()){
				Result result = plugin.execute(request, response, action);
				if(null != result){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:execute] -> executed by '{}'",plugin.getName());
					}
					return result;
				}
			}				
			for(Plugin plugin : plugins){
				Result result = plugin.execute(request, response, action);
				if(null != result){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:execute] -> executed by '{}'",plugin.getName());
					}					
					return result;
				}
			}
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:execute] -> executing not done by any plugins");
			}				
			return null;
		}
		
		static boolean render(Request request,Response response,Result result) throws Exception {
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:render] -> rendering result...");
			}
			
			request.result = result;
			
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.render(request, response, result)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:render] -> rendered by '{}'",plugin.getName());
					}					
					return true;
				}
			}			
			for(Plugin plugin : plugins){
				if(plugin.render(request, response, result)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:render] -> rendered by '{}'",plugin.getName());
					}					
					return true;
				}
			}
			
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:render] -> rendering not done by any plugins");
			}			
			
			return false;
		}
		
		static boolean error(Request request,Response response,Result.Error e) throws Exception{
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:error] -> handling error...");
			}			
			for(Plugin plugin : request.getModule().getPlugins()){
				if(plugin.error(request, response, e)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:error] -> handled by '{}'",plugin.getName());					
					}
					return true;
				}
			}			
			for(Plugin plugin : plugins){
				if(plugin.error(request, response, e)){
					if(log.isTraceEnabled()){
						log.trace("[plugin-invoker:error] -> handled by '{}'",plugin.getName());					
					}					
					return true;
				}
			}	
			if(log.isTraceEnabled()){
				log.trace("[plugin-invoker:error] -> handling not done by any plugins");
			}			
			return false;
		}
	}
}
