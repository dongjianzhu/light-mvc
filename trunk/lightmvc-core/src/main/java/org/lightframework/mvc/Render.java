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

import java.util.Map;


/**
 * TODO : document me 
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 0.1
 */
public class Render extends RuntimeException{
	
    private static final long serialVersionUID = -3174254894841072183L;

	private String   redirectTo;
	private String   forwardTo;
	
	protected String view;;
	protected Object data;
	
	/**
	 * redirect to the url
	 */
	public static void redirect(String url) {
		Render render = new Render();
		render.redirectTo = url;
		throw render;
	}
	
	/**
	 * forward to the request path in current application 
	 */
	public static void forward(String path) {
		Render render = new Render();
		render.forwardTo = path;
		throw render;
	}
	
	public static void render(String view){
		render(view,null);
	}
	
	public static void render(String view,Object data){
		if(data instanceof Map<?,?>){
			render(view,(Map<?,?>)data);
		}else{
			// TODO : Render.render
		}
	}
	
	public static void render(String view,Map<?,?> data){
		// TODO : Render.render
	}
	
	public Render(){
		
	}
	
	public Render(Object dataObject){
		this.data = dataObject;
	}
	
	public Object getData() {
    	return data;
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
}