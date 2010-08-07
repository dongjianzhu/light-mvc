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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.HTTP;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.config.Ajax;
import org.lightframework.mvc.core.RenderViewPlugin.IViewNotFoundRender;
import org.lightframework.mvc.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * core {@link Plugin} to render {@link Result} to json or xml format if current request is ajax request.
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class RenderAjaxPlugin extends Plugin implements IViewNotFoundRender {
	private static final Logger log = LoggerFactory.getLogger(RenderAjaxPlugin.class);
	
	public static final String RETURN_CODE  = "returnCode";
	public static final String RETURN_DESC  = "returnDesc";
	public static final String RETURN_VALUE = "returnValue";
	public static final String RETURN_ERROR = "error";
	
	public static final String PARAM_AJAX_REQUEST = "x-ajax";
	
	protected JSON json = new JSON();
	
	public RenderAjaxPlugin(){

	}

	@Override
    public boolean render(Request request, Response response, Result result) throws Exception {
		//is requested by ajax , such as jQuery.ajax , which send a http header 'X-Requested-With=XMLHttpRequest'
		boolean isAjax = request.isAjax();
		
		if(!isAjax){
			//is action configed as ajax 
			Action action = request.getAction();
			if(null != action && action.getMethod().isAnnotationPresent(Ajax.class)){
				isAjax = true;
			}
		}
		
		if(!isAjax){
			//is request send a ajax parameter 'x-ajax' 
			isAjax = null != request.getParameter(PARAM_AJAX_REQUEST);
		}

		if(isAjax){
			return renderAjax(request,response,result);
		}
		
	    return false;
    }
	
	public boolean renderViewNotFound(Request request, Response response, Result result) throws Exception {
	    return renderAjax(request, response, result);
    }
	
	protected boolean renderAjax(Request request,Response response,Result result) throws Exception{
		//XXX : xml format support
		
		renderJson(request, response, result);
		
		return true;
	}

	protected void renderJson(Request request,Response response,Result result) throws Exception{
		if(log.isTraceEnabled()){
			log.trace("[ajax:'{}'] -> render json result",request.getPath());
		}
		
		String content = encodeJson(result);
		response.write(content);
		if(request.getUserAgent().isMozilla()){
			response.setContentType(HTTP.CONTENT_TYPE_JSON_TEXT);
		}else{
			response.setContentType(HTTP.CONTENT_TYPE_JSON_RFC);	
		}
	}
	
	protected String encodeJson(Result result) throws Exception{
		JSON.Writer writer = new JSON.Writer();
		
		writer.startObject();
		writer.add(RETURN_CODE,json.encode(result.getStatus()))
		      .add(RETURN_DESC,json.encode(result.getDescription()));
		
		if(result instanceof Result.Error){
			writer.add(RETURN_ERROR,json.encode(generateErrorContent((Result.Error)result)));
		}
		
		writer.add(RETURN_VALUE, json.encode(result.getValue()));
		
		writer.endObject();
		
		return writer.toString();
	}
	
	protected String generateErrorContent(Result.Error error){
		StringWriter writer = new StringWriter();
		PrintWriter printer = new PrintWriter(writer);
		
		error.getException().printStackTrace(printer);
		
		return writer.toString();
	}
}
