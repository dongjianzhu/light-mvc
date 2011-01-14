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

import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.View;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * render {@link Result} to {@link View}
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class RenderViewPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(RenderViewPlugin.class);
	
	protected IViewNotFoundRender viewNotFoundRender;
	
	public RenderViewPlugin(){
		
	}

	public RenderViewPlugin(IViewNotFoundRender renderViewNotFound){
		this.viewNotFoundRender = renderViewNotFound;
	}
	
	@Override
    public boolean render(Request request, Response response, Result result) throws Exception {
		String action = request.getAction().getName();
		
		View view = findView(request, response, result);
		
		if(log.isTraceEnabled()){
			if(null == view){
				log.trace("[action:'{}'] -> view not found",action);
			}else{
				log.trace("[action:'{}'] -> found view '{}'",action,view.getName());
			}
		}
		
		if(null == view){
			if(null != viewNotFoundRender){
				if(log.isTraceEnabled()){
					log.trace("[action:'{}'] -> no view found,render by '{}'",action,viewNotFoundRender.getClass().getName());
				}
				return viewNotFoundRender.renderViewNotFound(request, response, result);
			}
			if(log.isTraceEnabled()){
				log.trace("[action:'{}'] -> not render any view",action);
			}
		}else {
			if(log.isTraceEnabled()){
				log.trace("[action:'{}'] -> view '{}' rendering... ",action,view.getName());
			}
			
			view.render(request, response);
			
			if(log.isTraceEnabled()){
				log.trace("[action:'{}'] -> view '{}' rendered!",action,view.getName());
			}
		}
		
	    return false;
    }
	
	protected View findView(Request request, Response response, Result result) throws Exception {
		return request.getModule().findView(request.getAction());
	}

	public void setViewNotFoundRender(IViewNotFoundRender renderViewNotFound) {
    	this.viewNotFoundRender = renderViewNotFound;
    }
	
	/**
	 * @since 1.0.0
	 */
	public static interface IViewNotFoundRender {
		
		boolean renderViewNotFound(Request request, Response response, Result result) throws Exception;
		
	}
}
