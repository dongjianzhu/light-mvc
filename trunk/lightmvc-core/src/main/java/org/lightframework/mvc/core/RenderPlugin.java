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

import java.util.Collection;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Render;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;

/**
 * core plugin to render action result
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class RenderPlugin extends Plugin {

	@Override
    public boolean render(Request request, Response response, Render render) throws Throwable {
		if(render.isRedirect()){
			response.redirect(render.getReirectTo());
			return true;
		}else if(render.isForward()){
			request.getAttributes().putAll(render.getAttributes());
			response.forward(render.getForwardTo());
			return true;
		}else{
			String view = findViewPath(request);
			if(null != view){
				request.setAttribute("render.status" , render.getStatus());
				request.setAttribute("render.message", render.getMessage());
				request.setAttribute("render.data"   , render.getData());

				response.forward(view);
				return true;
			}
		}
		return false;
    }
	
	private String findViewPath(Request request) {
		Action action = request.getAction();
		if(null != action){
			String _packpage = request.getApplication().getPackage();
			String className = action.getClazz().getName();
			
			if(null != _packpage && !"".equals(_packpage)){
				//'app.controllers.Product' -> 'controllers.Product' 
				className = className.substring(_packpage.length() + 1);
			}
			
			if(className.startsWith("app.controllers.")){
				//'controllers.Product' -> 'Product'
				className = className.substring("app.controllers.".length());
			}
			
			String controller = className.toLowerCase();
			String actionName = action.getMethod().getName().toLowerCase();
			
			return findDefaultViewPath(request,controller,actionName);
		}
		return null;
	}
	
	private String findDefaultViewPath(Request request, String controller,String action){
		String path = null;
		if("home".equalsIgnoreCase(controller)){
			path = "/";
		}else{
			path = "/" + controller.replace("\\.", "/") + "/";
		}
		
		Collection<String> files = request.getApplication().getViews(path);
		
		if(null != files){
			for(String file : files){
				if(file.toLowerCase().startsWith(path + action + ".")){
					return file;
				}
			}
		}
		
		return null;
	}
}
