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
package org.lightframework.mvc.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Assert;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Routes;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.routing.Match;
import org.lightframework.mvc.routing.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * core router plugin of mvc framework mapping a request url to action 
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class RoutingPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(RoutingPlugin.class);
	
	private static final String INTERNAL_ROUTE_PARAMETER_CONTROLLER           = "controller";
	private static final String INTERNAL_ROUTE_PARAMETER_ACTION               = "action";
	private static final String INTERNAL_ROUTE_PARAMETER_ACTION_OR_CONTROLLER = "ActionOrController";
	
	private static final ArrayList<Route> DEFAULT_ROUTES = new ArrayList<Route>();
	
	static{
		DEFAULT_ROUTES.add(Route.compile("*", "/",                       "home.index"));
		DEFAULT_ROUTES.add(Route.compile("*", "/index",                  "home.index"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{controller*}/",         "{controller}.index"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{ActionOrController}",   "{ActionOrController}.index,home.{ActionOrController}"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{controller*}/{action}", "{controller}.{action}"));
	}
	
	public Action[] route(Request request, Response response) throws Exception{
		String method = request.getMethod();
		String path   = request.getPath();
		
		Match matched = null;
		
		for(Route route : routes()){
			Match match = route.matches(method, path);
			
			if(match.isMatched()){
				matched = match;
				break;
			}
		}
		
		if(null == matched){
			for(Route route : DEFAULT_ROUTES){
				Match match = route.matches(method, path);
				
				if(match.isMatched()){
					matched = match;
					break;
				}
			}
		}
		
		if(null != matched){
			//remove the parameters are internal use only
			Map<String, Object> routedParams = matched.getParameters();
			if(null != routedParams){
				routedParams.remove(INTERNAL_ROUTE_PARAMETER_CONTROLLER);
				routedParams.remove(INTERNAL_ROUTE_PARAMETER_ACTION);
				routedParams.remove(INTERNAL_ROUTE_PARAMETER_ACTION_OR_CONTROLLER);
			}
			
			//create routing actions
			String   routedName  = matched.getName();
			String[] actionNames = routedName.split(",");
			
			if(log.isTraceEnabled()){
				for(String name : actionNames){
					log.trace("[action] -> found matched route '{}'",name);
				}
			}
			
			Action[] actions = new Action[actionNames.length];
			for(int i=0;i<actions.length;i++){
				String actionName = actionNames[i];
				Map<String, Object> actionParams = new HashMap<String, Object>();
				
				if(null != routedParams){
					actionParams.putAll(routedParams);
				}
				actions[i] = parse(new Action(actionName,actionParams));
			}
			return actions;
		}
		
		return EMPTY_ACTIONS;
	}
	
	protected Action parse(Action action){
		String name = action.getName();
		if(name.startsWith("home.")){
			Action.Setter.setHome(action, true);
		}
		if(name.endsWith(".index")){
			Action.Setter.setIndex(action, true);
		}
		
		//resolve controller and action method
		int lastDotIndex = name.lastIndexOf(".");
		
		Assert.isTrue(lastDotIndex > 0, "@ActionName.NoControllerDefined",name);
		
		String controller = name.substring(0,lastDotIndex);
		String methodName = name.substring(lastDotIndex + 1);		
		
		if(log.isTraceEnabled()){
			log.trace("[action:'{}'] -> parsed {controller:'{}',method:'{}'}",
					  new Object[]{name,controller,methodName});
		}
		
		Action.Setter.setControllerName(action, controller);
		Action.Setter.setSimpleName(action, methodName);	
		
		return action;
	}
	
	public Collection<Route> routes(){
		return Routes.table();
	}
}
