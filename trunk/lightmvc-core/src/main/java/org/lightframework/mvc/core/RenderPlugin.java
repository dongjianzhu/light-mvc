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
 * @author fenghm(live.fenghm@gmail.com)
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
//			//is ajax ?
//			boolean ajax = request.isAjax();
//			
//			//x-view?
//			String view  = request.getHeader("x-view");
			
			String view = findViewPath(request,render);
			if(null != view){
				request.getAttributes().putAll(render.getAttributes());
				
				request.setAttribute("render.status" , render.getStatus());
				request.setAttribute("render.message", render.getMessage());
				request.setAttribute("render.data"   , render.getData());

				response.forward(view);
				return true;
			}else if(request.getAction().isResolved()){
				//render default view
				
				
			}
		}
		return false;
    }
	
	private String findViewPath(Request request,Render render) {
		Action action = request.getAction();
		if(null != action){
			String _packpage  = request.getModule().getPackage();
			String controller = null;
			String actionName = null;
			
			if(action.isResolved()){
				controller = action.getClazz().getName().toLowerCase();
				actionName = action.getMethod().getName().toLowerCase();
			}else{
				int lastDotIndex = action.getName().lastIndexOf(".");
				controller = action.getName().substring(0,lastDotIndex).toLowerCase();
				actionName = action.getName().substring(lastDotIndex + 1).toLowerCase();
			}
			
			if(null != _packpage && !"".equals(_packpage) && controller.startsWith(_packpage)){
				//'app.controllers.Product' -> 'controllers.Product' 
				controller = controller.substring(_packpage.length() + 1);
			}
			
			if(controller.startsWith("controllers.")){
				//'controllers.Product' -> 'Product'
				controller = controller.substring("controllers.".length());
			}
			
			return findDefaultViewPath(request,render,controller,actionName);
		}
		return null;
	}
	
	private String findDefaultViewPath(Request request,Render render, String controller,String action){
		String path = null;
		String name = action;
		if("home".equalsIgnoreCase(controller)){
			path = "/";
		}else{
			//TODO : hard code 'module'
			path = "/modules/" + controller.replace("\\.", "/") + "/";
		}
		
		if(!Render.OK_STATUS.equals(render.getStatus())){
			name = "_" + render.getStatus();
		}
		
		Collection<String> files = request.getModule().getViews(path);
		
		if(null != files){
			for(String file : files){
				if(file.toLowerCase().startsWith(path + name + ".")){
					return file;
				}
			}
		}
		
		return null;
	}
}
