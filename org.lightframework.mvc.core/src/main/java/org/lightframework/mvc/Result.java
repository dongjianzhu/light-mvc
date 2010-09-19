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

import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.config.Ignore;
import org.lightframework.mvc.config.Name;
import org.lightframework.mvc.render.json.JSON;


/**
 * represents the result returned by action after exectuted.
 * 
 * @author fenghm (live.fenghm@bingosoft.net)
 * @since 1.0.0
 */
public abstract class Result {
	private static final ThreadLocal<Result> context = new ThreadLocal<Result>();
	
	//------public static constatns--------
	public static final int CODE_OK                = 200;
	public static final int CODE_MOVED_TEMPORARILY = 302; //redirect
	public static final int CODE_BAD_REQUEST       = 400;
	public static final int CODE_UNAUTHORIZED      = 401; 
	public static final int CODE_FORBIDDEN         = 403;
	public static final int CODE_NOT_FOUND         = 404;
	public static final int CODE_SERVER_ERROR      = 500;
	
	@Name("returnCode")
	protected int code = CODE_OK;
	
	@Name("returnStatus")
	protected String status;
	
	@Name("returnValue")
	protected Object value;
	
	@Name("returnDesc")
	protected String description;

	public boolean isOk(){
		return CODE_OK == getCode();
	}
	
	public int getCode() {
		if(0 == code){
			code = CODE_OK;
		}
		return code;
	}
	
	public String getStatus() {
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
	static void reset(){
		context.set(null);
	}	
	
	public static void set(Result result){
		context.set(result);
	}
	
	public static Result get(){
		return context.get();
	}	

	public static void setAttribute(String name,Object value){
		//XXX : warning if Request.current() return null;
		Request.current().setAttribute(name, value);
	}
	
	public static Object getAttribute(String name){
		return Request.current().getAttribute(name);
	}
	
	public static void removeAttribute(String name){
		Request.current().removeAttribte(name);
	}
	
	public static void redirect(String url) {
		throw new Return(new Redirect(url));
	}
	
	public static void forward(String path){
		throw new Return(new Forward(path));
	}
	
	public static void content(String text) {
		throw new Return(new Content(text));
	}
	
	public static void content(String text,String contentType){
		throw new Return(new Content(text,contentType));
	}
	
	public static void json(Object value){
		throw new Return(new Content(JSON.encode(value),HTTP.CONTENT_TYPE_JSON));
	}
	
	public static void error(String message){
		throw new Return(new Error(message));
	}
	
	public static void error(String message,Throwable e){
		throw new Return(new Error(message,e));
	}
	
	public static void error(String status,String message){
		throw new Return(new Error(status,message));
	}
	
	public static void error(String status,String message,Throwable e){
		throw new Return(new Error(status,message,e));
	}
	
	public static void error(int code,String message){
		throw new Return(new Error(code,message));
	}
	
	public static void error(int code,String message,Throwable e){
		throw new Return(new Error(code,message,e));
	}
	
	public static void error(int code,String status,String message){
		throw new Return(new Error(code,message));
	}
	
	public static void error(int code,String status,String message,Throwable e){
		throw new Return(new Error(code,status,message,e));
	}
	
	//--------built in interface and classes used by Result class----------
	
//	static final class Context {
//		private static ThreadLocal<Context> ctx = new ThreadLocal<Context>();
//		
//		private Result              result;
//		private Map<String, Object> attributes;
//		
//		public static Object getAttribute(String key){
//			return getAttributes().get(key);
//		}
//		
//		public static Map<String, Object> getAttributes(){
//			Context context = Context.get();
//			if(null == context.attributes){
//				context.attributes = new HashMap<String, Object>();
//			}
//			return context.attributes;
//		}
//		
//		static Context get(){
//			Context context = ctx.get();
//			if(null == context){
//				context = new Context();
//				ctx.set(context);
//			}
//			return context;
//		}
//		
//		static void release(){
//			Context context = ctx.get();
//			if(null != context){
//				if(null != context.attributes){
//					context.attributes.clear();
//				}
//				ctx.set(null);	
//			}
//		}
//	}

	/**
	 * @since 1.0.0
	 */
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
	 * @since 1.0.0
	 */
	public static final class Error extends Result{
		
		@Ignore
		protected Throwable exception;
		
		public Error(String message){
			this.code        = CODE_SERVER_ERROR;
			this.description = message;
		}
		
		public Error(String message,Throwable e){
			this(CODE_SERVER_ERROR,message,e);
		}
		
		public Error(String status,String message){
			this(message);
			this.status = status;
		}
		
		public Error(String status,String message,Throwable e){
			this(message,e);
			this.status = status;
		}
		
		public Error(int code,String message){
			this.code        = code;
			this.description = message;
		}
		
		public Error(int code,String message,Throwable e){
			this(code,message);
			this.exception = e;
		}
		
		public Error(int code,String status,String message){
			this(code,message);
			this.status = status;
		}
		
		public Error(int code,String status,String message,Throwable e){
			this(code,status,message);
			this.exception = e;
		}
		
		public Throwable getException(){
			return exception;
		}
	}
	
	/**
	 * a {@link Result} object represents an object value which stored in {@link Result#value} property
	 * @since 1.0.0
	 */
	public static final class Value extends Result {
		public Value(Object value){
			this.value = value;
		}
	}
	
	/**
	 * a {@link Result} object represents empty output.
	 * @since 1.0.0
	 */
	public static final class Empty extends Result {
		
	}
	
	/**
	 * a {@link Result} object output text contents to browser. 
	 * @since 1.0.0
	 */
	public static final class Content extends Result implements IRender{
		
		protected String content;
		protected String contentType = HTTP.CONTENT_TYPE_TEXT;
		
		public Content(String content){
			this.content = content;
			this.value   = content;
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
	 * @since 1.0.0
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
	 * @since 1.0.0
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