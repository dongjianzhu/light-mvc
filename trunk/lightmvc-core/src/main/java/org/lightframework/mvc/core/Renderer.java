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
import org.lightframework.mvc.View;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;

/**
 * core plugin to render action result
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class Renderer extends Plugin {

	@Override
    public boolean render(Request request, Response response, Render render) throws Throwable {
		if(render.isRedirect()){
			response.redirect(render.getReirectTo());
		}else if(render.isForward()){
			response.forward(render.getForwardTo());	
		}else{
			View view = render.getView();
			
			if(null == view){
				String defaultViewPath = findDefaultViewPath(request);
				if(null != defaultViewPath){
					view = new View(defaultViewPath);
				}
			}

			if(null != view){
				view.render(request, response, render.getData());
				return true;
			}
		}
		return false;
    }
	
	private String findDefaultViewPath(Request request) {
		Action action = request.getAction();
		if(null != action){
			String _packpage = request.getApplication().getPackage();
			String className = action.getClazz().getName();
			
			if(null != _packpage && !"".equals(_packpage)){
				//'demoapp.controllers.Product' -> 'controllers.Product' 
				className = className.substring(_packpage.length() + 1);
			}
			
			if(className.startsWith("controllers.")){
				//'controllers.Product' -> 'Product'
				className = className.substring("controllers.".length());
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
