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
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Utils;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.Utils.Ref;

/**
 * core router plugin of mvc framework
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 0.1
 */
public class Router extends Plugin {
	
	private static final ArrayList<Route> DEFAULT_ROUTES = new ArrayList<Route>();
	
	static{
		DEFAULT_ROUTES.add(Route.compile("*", "/", "home.index"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{action}", "home.{action}"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{module}_{controller}/{action}", "{module}.{controller}.{action}"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{module}/{controller}/{action}", "{module}.{controller}.{action}"));
		DEFAULT_ROUTES.add(Route.compile("*", "/{controller}/{action}", "{controller}.{action}"));
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
	
	public static final class Route {
		private static Pattern PARAMS_PATTERN = Pattern.compile("\\{([a-zA-Z0-9]+)\\}");
		
		protected String   method;
		protected String   path;
		protected String   action;
		protected Compiled compiled;
		
		public static Route compile(String method,String path,String action){
			Route route = new Route();
			route.method = method;
			route.path   = path;
			route.action = action;
			route.compile();
			return route;
		}
		
		public void compile() {
			if(null == compiled){
				if(null == action || "".equals(action = action.trim())){
					throw new IllegalStateException("'action' required in route");
				}
				
				if(null == method || "".equals(method = method.trim())){
					method = "*";
				}
				
				compiled = new Compiled();
				if(null == path || "".equals(path = path.trim())){
					path = "*";
				}else{
					//replace "*" chartacter to regex "[^/]*"
					Ref<String> expr    = new Ref<String>(path.replaceAll("\\*", "\\[^/]\\*"));
					
					compiled.pathParams   = findParams(expr,true);
					compiled.actionParams = findParams(new Ref<String>(action),false);
					compiled.pattern      = Pattern.compile(expr.value);
				}
			}
		}
		
		public Match matches(String path){
			return matches(null,path);
		}
		
		@SuppressWarnings("unchecked")
		public Match matches(String method,String path){
			compile();
			
			Match match = new Match();
			
			if(null == method || this.method.equals("*") || this.method.equalsIgnoreCase(method.trim())){
				if(this.path.equals("*")){
					match.matched = true;
					match.setName(this.action);
				}else{
					if(null == path || "".equals(path = path.trim())){
						path = "/";
					}
					
					Matcher matcher = compiled.pattern.matcher(path);
		            if (matcher.matches()) {
		            	Map params = new HashMap();
		            	
		            	for(int i=0;i<compiled.pathParams.length;i++){
		            		String param = compiled.pathParams[i];
		            		params.put(param, matcher.group(i+1));
		            	}
		            	match.matched = true;
		            	match.setName(this.action);
		            	match.setParameters(params);
		            	
		            	//replace action params to values
		            	if(compiled.actionParams.length > 0){
		            		for(String param : compiled.actionParams){
		            			String value = (String)params.get(param);
		            			if(null != value){
		            				match.setName(Utils.replace(match.getName(), "{" + param + "}", value));
		            			}
		            		}
		            	}
		            }					
				}
			}
			
			return match;
		}
		
		private static String[] findParams(Ref<String> text,boolean translate){
			Matcher matcher = PARAMS_PATTERN.matcher(text.value);
			
			ArrayList<String> params = new ArrayList<String>();
			while(matcher.find()){
				String param = matcher.group(1);
				
				if(translate){
					//replace "{param}" to regex "([a-zA-Z_0-9]+)"
					text.value = text.value.replace("{" + param + "}","([a-zA-Z_0-9]+)");
				}
				params.add(param.trim());
			}
			
			return params.toArray(new String[]{});
		}
	}
	
	private static final class Compiled {
		private Pattern  pattern;
		private String[] pathParams;
		private String[] actionParams;
	}
	
	public static final class Match extends Action{
        private boolean matched;
		
		public boolean isMatched(){
			return matched;
		}
	}
}
