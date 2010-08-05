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

/**
 * definition of a view render object.
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class Render extends RuntimeException{
	
    private static final long serialVersionUID = -3174254894841072183L;
    
    private static final ThreadLocal<Render> context = new ThreadLocal<Render>();
    
    public static final String OK_STATUS     = "ok";
    public static final String FAILED_STATUS = "failed";

    protected String  redirectTo;
    protected String  forwardTo;
    
	protected String  status   = OK_STATUS;
	protected String  message;
	protected Object  data;
	
	protected boolean rendered;
	
	protected Map<String, Object> attributes = new HashMap<String, Object>();
	
	/**
	 * redirect to the url
	 */
	public static void redirect(String url) {
		current().redirectTo = url;
		_throw();
	}
	
	/**
	 * forward to the request path in current module 
	 */
	public static void forward(String path) {
		current().forwardTo = path;
		_throw();
	}
	
	public static void setAttribute(String key,Object value){
		current().attributes.put(key, value);
	}
	
	public static void failed(){
		failed(null,null);
	}
	
	public static void failed(String message){
		failed(message,null);
	}
	
	public static void failed(String message,Object data){
		result(FAILED_STATUS,message,data);
	}
	
	public static void result(String status){
		result(status,null,null);
	}
	
	public static void result(String status,String message){
		result(status,message,null);
	}
	
	public static void result(String status,String message,Object data){
		Render render  = current();
		render.status  = status;
		render.message = message;
		render.data    = data;
		_throw();
	}

	public static void rendered(){
		current().rendered = true;
		_throw();
	}
	
	public static Render current(){
		Render render = context.get();
		if(null == render){
			render = new Render();
			context.set(render);
		}
		return render;
	}
	
	private static void _throw(){
		Render render = current();
		context.set(null);
		throw render;
	}
	
	Render(){
		
	}

	public boolean isRedirect(){
		return null != redirectTo;
	}
	
	public String getReirectTo(){
		return redirectTo;
	}
	
	public boolean isForward(){
		return null != forwardTo;
	}
	
	public String getForwardTo(){
		return forwardTo;
	}
	
	public boolean isRendered(){
		return rendered;
	}
	
	public Map<String, Object> getAttributes(){
		return attributes;
	}

	public String getStatus() {
    	return status;
    }

	public void setStatus(String status) {
    	this.status = status;
    }

	public String getMessage() {
    	return message;
    }

	public void setMessage(String message) {
    	this.message = message;
    }

	public Object getData() {
    	return data;
    }

	public void setData(Object data) {
    	this.data = data;
    }
}