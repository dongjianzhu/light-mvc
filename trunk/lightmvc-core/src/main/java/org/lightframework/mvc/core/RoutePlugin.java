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
import java.util.List;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.routing.Match;
import org.lightframework.mvc.routing.Route;

/**
 * core router plugin of mvc framework mapping a request url to action 
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0
 */
public class RoutePlugin extends Plugin {
	
	private static final ArrayList<Route> DEFAULT_ROUTES = new ArrayList<Route>();
	
	static{
		DEFAULT_ROUTES.add(Route.compile("*", "/",                       "home.index"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{controller*}/",         "{controller}.index"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{ActionOrController}",   "home.{ActionOrController},{ActionOrController}.index"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{controller*}/{action}", "{controller}.{action}"));
	}
	
	protected List<Route> routes = new ArrayList<Route>();
	
	public Action route(Request request, Response response) throws Throwable{
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
		
		return matched;
	}
	
	public Collection<Route> routes(){
		return routes;
	}
}
