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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.binding.Argument;
import org.lightframework.mvc.binding.Binder;
import org.lightframework.mvc.binding.IBindingContext;
import org.lightframework.mvc.convert.IConverter;
import org.lightframework.mvc.internal.params.Parameters;

/**
 * the core plugin to binding parameters in request of action method.
 *
 * @author fenghm(live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class BindingPlugin extends Plugin {
	@Override
    public boolean binding(Request request, Response response, Action action) throws Exception{
		BindingContext context = new BindingContext(request, action);
		
		if(action.getArguments().length == 1){
			Argument arg = action.getArguments()[0];
			
			if(arg.isBinded()){
				return true;
			}
			
			if(arg.getType().isArray() && request.hasBodyParameters()){
				Parameters params = request.getBodyParameters();
				
				if(params.isArray()){
					arg.binding(Binder.binding(arg, params.array(), context));
					
					return true;
				}
			}
		}
		
		Binder.binding(action.getArguments(), context);
		
		return true;
    }
	
	private static final class BindingContext implements IBindingContext{

		private final Request request;
		private final Action  action;
		private final Map<String, Object> map = new HashMap<String, Object>();
		
		private BindingContext(Request request,Action action){
			this.request = request;
			this.action  = action;
			
			for(String name : request.getParameterNames()){
				String[] value = request.getParameterValues(name);
				if(value.length == 1){
					map.put(name, value[0]);
				}else if(value.length == 0){
					map.put(name, "");
				}else{
					map.put(name, value);
				}
			}
			
			if(request.hasBodyParameters()){
				Parameters params = request.getBodyParameters();
				
				if(params.isMap()){
					map.putAll(params.map());
				}
			}
			
			map.putAll(this.action.getParameters());
		}
		
		public Object getParameter(Class<?> type, String name) {
			if(HttpServletRequest.class.isAssignableFrom(type)){
				return request.getExternalRequest();
			}
			
			if(HttpServletResponse.class.isAssignableFrom(type)){
				return request.getResponse().getExternalResponse();
			}
			
			if(Request.class.isAssignableFrom(type)){
				return request;
			}
			
			if(Map.class.isAssignableFrom(type)){
				return map;
			}
			
	        return getParameter(name);
        }

		public Object getParameter(String name) {
//			//get arg's raw value
//			Object value = action.getParameter(name);
//			if(null == value && request.hasBodyParameters() && request.getBodyParameters().isMap()){
//				value = request.getBodyParameters().get(name);
//			}
//			if(null == value){
//				value = request.getParameter(name);
//			}
//			return value;
			
			for(Entry<String, Object> entry : map.entrySet()){
				if(entry.getKey().equalsIgnoreCase(name)){
					return entry.getValue();
				}
			}
			
			return null;
		}

		public Map<String, Object> getParameters() {
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.putAll(map);
//			params.putAll(action.getParameters());
//			return params;
			return map;
        }
		
		public List<IConverter> getConverters() {
	        return null;
        }
	}
}