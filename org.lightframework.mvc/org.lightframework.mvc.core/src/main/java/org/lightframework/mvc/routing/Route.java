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
package org.lightframework.mvc.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lightframework.mvc.Utils;

/**
 * represents a routing rule of url to action mapping
 * 
 * @author fenghm(live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class Route {
	private static Pattern PARAMS_PATTERN = Pattern.compile("\\{([a-zA-Z0-9_]+[\\*]?)\\}");
	
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
				Ref<String> expr    = new Ref<String>(path);
				
				compiled.pathParams   = findParams(expr,true);
				compiled.actionParams = findParams(new Ref<String>(action),false);
				compiled.pattern      = Pattern.compile(expr.value.replaceAll("\\*", "\\[^/]\\*"));
				//replace "*" chartacter to regex "[^/]*"
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
				match.name    = this.action;
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
	            	match.matched    = true;
	            	match.name       = this.action;
	            	match.parameters = params;
	            	
	            	//replace action params to values
	            	if(compiled.actionParams.length > 0){
	            		for(String param : compiled.actionParams){
	            			String value = (String)params.get(param);
	            			if(null != value){
	            				match.name = Utils.replace(match.getName(), "{" + param + "}", value);
	            			}
	            		}
	            		//replace all '/' characters to '.' characters
	            		match.name = Utils.replace(match.getName(), "/", ".");
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
				if(param.endsWith("*")){
					text.value = text.value.replace("{" + param + "}","([a-zA-Z_0-9/]+)");
					param = param.substring(0,param.length() - 1);
				}else{
					//replace "{param}" to regex "([a-zA-Z_0-9]+)"
					text.value = text.value.replace("{" + param + "}","([a-zA-Z_0-9]+)");
				}
			}
			params.add(param.trim());
		}
		
		return params.toArray(new String[]{});
	}
	
	private static final class Compiled {
		Pattern  pattern;
		String[] pathParams;
		String[] actionParams;
	}
	
	private final static class Ref<E> {
		private E value;
		private Ref(E value){
			this.value = value;
		}
	}	
}