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

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.binding.Argument;
import org.lightframework.mvc.binding.Binder;
import org.lightframework.mvc.binding.IBindingContext;
import org.lightframework.mvc.convert.IConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * the core plugin to binding parameters in request of action method.
 *
 * @author fenghm(live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class BindingPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(BindingPlugin.class);
	
	@Override
    public boolean binding(Request request, Response response, Action action) throws Exception{
		BindingContext context = new BindingContext(request, action);
		for(Argument arg : action.getArguments()){
			if(!arg.isBinded()){
				Object value = context.getParameter(arg.getName());
				
				if(null == value && request.hasBodyParameters() && action.getArguments().length == 1){
					value = request.getBodyParameters().isArray() ? 
								request.getBodyParameters().array().array() : 
								request.getBodyParameters().map();
				}
				
				if(log.isTraceEnabled()){
					String type = value == null ? "null" : value.getClass().getName();
					log.trace("[arg:'{}'] -> tryto binding : '{}'-'{}'",
							   new Object[]{arg.getName(),type,value});
				}
				arg.binding(Binder.binding(arg,value,context));
				if(log.isTraceEnabled()){
					if(arg.isBinded()){
						Object binded = arg.getValue();
						String type   = null != binded ? binded.getClass().getName() : "null";
						log.trace("[arg:'{}'] -> binding value : '{}'-'{}'",
								  new Object[]{arg.getName(),type,binded});
					}else{
						log.trace("[arg:'{}'] -> not binded",arg.getName());
					}
				}
			}
		}
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
		}
		
		public Object getParameter(String name) {
			//get arg's raw value
			Object value = action.getParameter(name);
			if(null == value && request.hasBodyParameters() && request.getBodyParameters().isMap()){
				value = request.getBodyParameters().get(name);
			}
			if(null == value){
				value = request.getParameter(name);
			}
			return value;
        }

		public Map<String, Object> getParameters() {
			Map<String, Object> params = new HashMap<String, Object>();
			params.putAll(map);
			params.putAll(action.getParameters());
			return params;
        }
		
		public List<IConverter> getConverters() {
	        return null;
        }
	}
}