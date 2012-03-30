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
import java.util.List;

import org.lightframework.mvc.routing.Route;

/**
 * the route table define routing rules mapping a request path to an action
 *
 * @author fenghm(live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public final class RouteManager {
	
	private static final LinkedList<Route> table = new LinkedList<Route>();

	/**
	 * add a routing rule to route table which match all http methods
	 * @see #add(String, String, String) 
	 */
	public static void add(String path,String action){
		table.add(Route.compile("*", path, action));
	}
	
	/**
	 * add a routing rule to route table
	 * @param method http method,ignore case,the value "*" matches all methods 
	 * @param path   request path starts '/' without context path and query string
	 * @param action the action name with controller,such as 'user.list'
	 */
	public static void add(String method,String path,String action){
		table.add(Route.compile(method, path, action));
	}
	
	/**
	 * add a routing rule to route table
	 */	
	public static void add(Route route){
		table.add(route);
	}
	
	/**
	 * @return the routing rules table
	 */
	public static List<Route> table(){
		return table;
	}
}
