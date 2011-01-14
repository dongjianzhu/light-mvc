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

import java.io.InputStream;

import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.HTTP.Session;
import org.lightframework.mvc.config.Ignore;
import org.lightframework.mvc.config.Name;
import org.lightframework.mvc.file.Download;
import org.lightframework.mvc.render.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * represents the result returned by action after exectuted.
 * 
 * @author fenghm (live.fenghm@bingosoft.net)
 * @since 1.0.0
 */
public abstract class Result {
	private static final Logger log = LoggerFactory.getLogger(Result.class);
	private static final ThreadLocal<Result> context = new ThreadLocal<Result>();
	
	//------public static constatns--------
	public static final int CODE_OK                = 200;
	public static final int CODE_MOVED_TEMPORARILY = 302; //redirect
	public static final int CODE_BAD_REQUEST       = 400;
	public static final int CODE_UNAUTHORIZED      = 401; 
	public static final int CODE_FORBIDDEN         = 403;
	public static final int CODE_NOT_FOUND         = 404;
	public static final int CODE_SERVER_ERROR      = 500;
	
	public static final String RESPONSE_HAS_RENDER     = "$_RESPONSE_HAS_RENDER_$" ;
	
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
	
	static void set(Result result){
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
		Request.current().removeAttribute(name);
	}
	
	public static void renderResponse(){
		Request.current().setAttribute(Result.RESPONSE_HAS_RENDER, true) ;
	}
	
	//request , response
	public static Request getRequest(){
		return Request.current() ;
	}
	
	public static Session getSession(){
		return getRequest().getSession() ;
	}
	
	public static Response getResponse(){
		return Response.current() ;
	}
	
	@SuppressWarnings("unchecked")
    public static <T> T getServletRequest(){
		return (T)getRequest().getExternalRequest() ;
	}
	
	@SuppressWarnings("unchecked")
    public static <T> T getServletSession(){
		return (T)getSession().getExternalSession() ;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getServletResponse(){
		return (T)getResponse().getExternalResponse() ;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getServletResponse(boolean isCommit){
		return (T)getResponse().getExternalResponse(isCommit) ;
	}
	
	public static void redirect(String url) {
		try {
			renderResponse() ;
	        new Redirect(url).render(Request.current(), Request.current().getResponse()) ;
        } catch (Exception e) {
	        log.error("redirect {} :",url,e) ;
	        throw new MvcException("redirect "+url+" :",e) ;
        }
		//throw new Return(new Redirect(url));
	}
	
	public static void forward(String path){
		try {
			renderResponse() ;
	        new Forward(path).render(Request.current(), Request.current().getResponse()) ;
        } catch (Exception e) {
	        log.error("forward {} :",path,e) ;
	        throw new MvcException("forward "+path+" :",e) ;
        }
		//throw new Return(new Forward(path));
	}
	
	public static void content(String text) {
		content(text,HTTP.CONTENT_TYPE_TEXT) ;
		//throw new Return(new ContentResult(text));
	}
	
	public static void content(String text,String contentType){
		try {
			renderResponse() ;
			new ContentResult(text,contentType).render(Request.current(), Request.current().getResponse()) ;
        } catch (Exception e) {
	        log.error("content {}:",text,e) ;
	        throw new MvcException("content "+text+" :",e) ;
        }
		//throw new Return(new ContentResult(text,contentType));
	}
	
	public static void script(String script){
		String text = "<script type='text/javascript'>"+script+"</script>" ;
		content(text) ;
	}
	
	//download file
	public static void file(InputStream in , String fileName){
		file(in, fileName, null) ;
	}
	
	public static void file(String filePath){
		file(filePath, null) ;
	}
	
	public static void file(String filePath , String fileName){
		file(filePath, fileName, null) ;
	}
	
	public static void file(InputStream in , String fileName,String encoding){
		try {
			renderResponse() ;
	        new DownloadResult(in, fileName,encoding).render(Request.current(), Request.current().getResponse()) ;
        } catch (Exception e) {
        	log.error("下载文件异常",e) ;
	        throw new MvcException("下载文件异常",e) ;
        }
	}
	
	public static void file(String filePath , String fileName,String encoding){
		try {
			renderResponse() ;
	        new DownloadResult(filePath, fileName,encoding).render(Request.current(), Request.current().getResponse()) ;
        } catch (Exception e) {
        	log.error("下载文件异常",e) ;
	        throw new MvcException("下载文件异常",e) ;
        }
	}
	
	
	public static void json(Object value){
		content(JSON.encode(value),HTTP.CONTENT_TYPE_JSON) ;
		//throw new Return(new ContentResult(JSON.encode(value),HTTP.CONTENT_TYPE_JSON));
	}
	
	public static void data(Object data){
		throw new Return(new DataResult(data));
	}
	
	public static void error(String message){
		throw new Return(new ErrorResult(message));
	}
	
	public static void error(String message,Throwable e){
		throw new Return(new ErrorResult(message,e));
	}
	
	public static void error(String status,String message){
		throw new Return(new ErrorResult(status,message));
	}
	
	public static void error(String status,String message,Throwable e){
		throw new Return(new ErrorResult(status,message,e));
	}
	
	public static void error(int code,String message){
		throw new Return(new ErrorResult(code,message));
	}
	
	public static void error(int code,String message,Throwable e){
		throw new Return(new ErrorResult(code,message,e));
	}
	
	public static void error(int code,String status,String message){
		throw new Return(new ErrorResult(code,message));
	}
	
	public static void error(int code,String status,String message,Throwable e){
		throw new Return(new ErrorResult(code,status,message,e));
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
	public static final class ErrorResult extends Result{
		
		@Ignore
		protected Throwable error;
		
		public ErrorResult(String message){
			this.code        = CODE_SERVER_ERROR;
			this.description = message;
		}
		
		public ErrorResult(String message,Throwable e){
			this(CODE_SERVER_ERROR,message,e);
		}
		
		public ErrorResult(String status,String message){
			this(message);
			this.status = status;
		}
		
		public ErrorResult(String status,String message,Throwable e){
			this(message,e);
			this.status = status;
		}
		
		public ErrorResult(int code,String message){
			this.code        = code;
			this.description = message;
		}
		
		public ErrorResult(int code,String message,Throwable e){
			this(code,message);
			this.error = e;
		}
		
		public ErrorResult(int code,String status,String message){
			this(code,message);
			this.status = status;
		}
		
		public ErrorResult(int code,String status,String message,Throwable e){
			this(code,status,message);
			this.error = e;
		}
		
		public Throwable getError(){
			return error;
		}
	}
	
	/**
	 * a {@link Result} object represents an data value which stored in {@link Result#value} property
	 * @since 1.0.0
	 */
	public static final class DataResult extends Result {
		public DataResult(Object data){
			this.value = data;
		}
	}
	
	/**
	 * a {@link Result} object represents empty output.
	 * @since 1.0.0
	 */
	public static final class EmptyResult extends Result {
		
	}
	
	/**
	 * 下载文件或流
	 * TODO : document me
	 *
	 * @author User
	 * @since 1.x.x
	 */
	public static final class DownloadResult extends Result implements IRender{
		private InputStream in ;
		private String fileName ;
		private String encoding = "UTF-8" ;
		private String filePath ;
		
		public DownloadResult(InputStream in,String fileName){
			this.in = in ;
			this.fileName = fileName ;
		}
		
		public DownloadResult(InputStream in,String fileName,String encoding){
			this.in = in ;
			this.fileName = fileName ;
			this.encoding = encoding ;
		}
		
		public DownloadResult(String filePath,String fileName){
			this.filePath = filePath ;
			this.fileName = fileName ;
		}
		
		public DownloadResult(String filePath,String fileName,String encoding){
			this.filePath = filePath ;
			this.fileName = fileName ;
			this.encoding = encoding ;
		}

        public void render(Request request, Response response) throws Exception {
	       if(null != this.filePath){
	    	   Download.download(filePath, fileName, encoding,request) ;
	       }else if(null != this.in){
	    	   Download.download(in, fileName, encoding,request) ;
	       }
        }
		
	}
	
	/**
	 * a {@link Result} object output text contents to browser. 
	 * @since 1.0.0
	 */
	public static final class ContentResult extends Result implements IRender{
		
		protected String content;
		protected String contentType = HTTP.CONTENT_TYPE_TEXT;
		
		public ContentResult(String content){
			this.content = content;
			this.value   = content;
		}
		
		public ContentResult(String content,String contentType){
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
	static final class Redirect extends Result implements IRender{
		
		private String url;
		
		Redirect(String url){
			this.url = url;
		}

		public void render(Request request, Response response) throws Exception {
			//handle path starts with "~/" or "./" or path not starts with "/" and not contains "://"
			if(null != url && !url.startsWith("/") && !url.contains("://")){
				if(url.startsWith("~/")){
					url = request.getContext() + url.substring(1);
				}else {
					String newUrl = url.startsWith("./") ? url.substring(2) : url;
					Action action = request.getAction();
					if(null != action){
						Module module = request.getModule();
						String controllerPath = module.findControllerPath(action);
						if(null != controllerPath){
							newUrl = controllerPath + newUrl;
							if(log.isDebugEnabled()){
								log.debug("[redirect] -> change the url '{}' to '{}'",url,newUrl);
							}
							url = newUrl;
						}
					}					
				}
			}
	        response.redirect(url);
        }
	}
	
	/**
	 * a {@link Result} object forward to another request path in server.
	 * @since 1.0.0
	 */
	static final class Forward extends Result implements IRender{
		
		private String path;
		
		Forward(String path){
			this.path = path;
		}

		public void render(Request request, Response response) throws Exception{
			if(null != path && !path.startsWith("/")){
				String newPath = path.startsWith("./") ? path.substring(2) : path;
				
				Action action = request.getAction();
				if(null != action){
					Module module = request.getModule();
					String controllerPath = module.findControllerPath(action);
					if(null != controllerPath){
						newPath = controllerPath + newPath;
						if(log.isDebugEnabled()){
							log.debug("[forward] -> change path '{}' to '{}'",path,newPath);
						}
						path = newPath;
					}
				}
			}
			
			response.forward(path);
        }
	}
}
