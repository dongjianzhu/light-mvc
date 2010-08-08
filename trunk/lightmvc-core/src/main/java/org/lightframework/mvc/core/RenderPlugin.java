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

import org.lightframework.mvc.IRender;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;

/**
 * core plugin to render action result
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class RenderPlugin extends Plugin {
	
	protected RenderAjaxPlugin renderAjax = new RenderAjaxPlugin();
	
	protected RenderViewPlugin renderView = new RenderViewPlugin(renderAjax);

	@Override
    public boolean render(Request request, Response response, Result result) throws Exception {
		
		setAttributes(request,result);
		
		if(result instanceof IRender){
			((IRender) result).render(request, response);
			return true;
		}else{
			return renderResult(request,response,result);
		}
    }

	protected void setAttributes(Request request,Result result){
		request.setAttribute("result", result);
		request.setAttribute("result.status", result.getStatus());
		request.setAttribute("result.description",result.getDescription());
		request.setAttribute("result.value", result.getValue());
	}
	
	protected boolean renderResult(Request request,Response response,Result result) throws Exception {
		if(!renderAjax.render(request, response, result)){
			return renderView.render(request, response, result);
		}
		return true;
	}
}
