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
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * core plugin to handle exceptions
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class ErrorPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(ErrorPlugin.class);

	private RenderAjaxPlugin renderAjaxPlugin;
	
	public ErrorPlugin(){
		
	}
	
	public ErrorPlugin(RenderAjaxPlugin renderAjaxPlugin){
		this.renderAjaxPlugin = renderAjaxPlugin;
	}
	
	
	@Override
    public boolean error(Request request, Response response, Result.Error error) throws Exception {
		if(log.isInfoEnabled()){
			log.error("[mvc:error] -> {}",error.getDescription(),error.getException());
		}
		
	    // TODO : implement ErrorPlugin.error
		
		return renderAjaxPlugin.render(request, response, error);
    }

	public void setRenderAjaxPlugin(RenderAjaxPlugin renderAjaxPlugin) {
    	this.renderAjaxPlugin = renderAjaxPlugin;
    }
}
