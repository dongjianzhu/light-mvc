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
package org.lightframework.mvc;

import java.util.HashMap;
import java.util.Map;

import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;


/**
 * represents the result returned by action after exectuted.
 * 
 * @author fenghm (live.fenghm@bingosoft.net)
 * @since 1.0.0
 */
public abstract class Result {
	//------public static constatns--------
	public static final String STATUS_OK                = "200";
	public static final String STATUS_MOVED_TEMPORARILY = "302"; //redirect
	public static final String STATUS_NOT_MODIFIED      = "304";
	public static final String STATUS_BAD_REQUEST       = "400";
	public static final String STATUS_UNAUTHORIZED      = "401"; 
	public static final String STATUS_FORBIDDEN         = "403";
	public static final String STATUS_NOT_FOUND         = "404";
	public static final String STATUS_SERVER_ERROR      = "500";
	
	protected String status = STATUS_OK;
	protected String description;
	protected Object value;

	public String getStatus() {
		if(null == status){
			status = STATUS_OK;
		}
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Object getValue() {
    	return value;
    }

	public void setValue(Object value) {
    	this.value = value;
    }
	
	//--------static methods of Result classs-----------

	
	//TODO : ADD SOME STATUS METHODS HERE
	
	
	//--------built in interface and classes used by Result class----------
	
	public static final class Context {
		private static ThreadLocal<Context> ctx = new ThreadLocal<Context>();
		
		private Result              result;
		private Map<String, Object> attributes;
		
		public static void setResult(Result result){
			get().result = result;
		}
		
		public static Result getResult(){
			return get().result;
		}
		
		public static void setAttribute(String key,Object value){
			getAttributes().put(key, value);
		}
		
		public static void removeAttribute(String key){
			getAttributes().remove(key);
		}
		
		public static Object getAttribute(String key){
			return getAttributes().get(key);
		}
		
		public static Map<String, Object> getAttributes(){
			Context context = get();
			if(null == context.attributes){
				context.attributes = new HashMap<String, Object>();
			}
			return context.attributes;
		}
		
		static Context get(){
			Context context = ctx.get();
			if(null == context){
				context = new Context();
				ctx.set(context);
			}
			return context;
		}
		
		static void release(){
			Context context = ctx.get();
			if(null != context){
				if(null != context.attributes){
					context.attributes.clear();
				}
				ctx.set(null);	
			}
		}
	}

	public static interface IRender {
		void render(Request request,Response response) throws Exception;
	}
	
	public static final class Return extends RuntimeException{
		
        private static final long serialVersionUID = -4405493230296254488L;
        
		private Result result;
		
		Return(Result result){
			this.result = result;
		}
		
		public Result result(){
			return result;
		}
	}
	
	//--------built in sub-classes of Result-------------
	
	/**
	 * a {@link Result} object represents an error.
	 */
	public static final class Error extends Result{
		
		protected String    status = STATUS_SERVER_ERROR;
		protected Throwable exception;
		
		public Error(Throwable e){
			this.description = e.getMessage();
			this.exception   = e;
		}
		
		public Throwable getException(){
			return exception;
		}
	}
	
	/**
	 * a {@link Result} object represents empty output.
	 */
	public static final class Empty extends Result {
		
	}
	
	/**
	 * a {@link Result} object output text contents to browser. 
	 */
	public static final class Content extends Result implements IRender{
		
		protected String content;
		protected String contentType = HTTP.CONTENT_TYPE_TEXT_PLAIN;
		
		public Content(String content){
			this.content = content;
		}
		
		public Content(String content,String contentType){
			this(content);
			this.contentType = contentType;
		}

		public void render(Request request, Response response) throws Exception {
			response.setContentType(contentType);
			if(null != content) {
				response.write(content);
			}
        }
	}
	
	/**
	 * a {@link Result} object sends redirect to browser of the given url.
	 */
	public static final class Redirect extends Result implements IRender{
		
		private String url;
		
		public Redirect(String url){
			this.url = url;
		}

		public void render(Request request, Response response) throws Exception {
	        response.redirect(url);
        }
	}
	
	/**
	 * a {@link Result} object forward to another request path in server.
	 */
	public static final class Forward extends Result implements IRender{
		
		private String path;
		
		public Forward(String path){
			this.path = path;
		}

		public void render(Request request, Response response) throws Exception{
			response.forward(path);
        }
	}
}
