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
package org.lightframework.mvc.test;

import org.lightframework.mvc.HTTP;
import org.lightframework.mvc.Module;
import org.lightframework.mvc.HTTP.Request;

/**
 * mock object of {@link Request} for testing.
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class MockRequest extends Request {
	
	public static final String REMOTE_ADDRESS = "127.0.0.1";
	public static final String METHOD         = "GET";
	public static final String CONTENT_TYPE   = "text/html";
	
	protected MockUrl mockUrl;
	
	public MockRequest(){
		this.mockUrl = new MockUrl();
		this.url = this.mockUrl;
		
		this.remoteAddress = REMOTE_ADDRESS;
		this.method        = METHOD;
		this.contentType   = CONTENT_TYPE;
	}
	
	public MockRequest(Module module){
		this();
		this.module = module;
	}
	
	public MockUrl getMockUrl(){
		return mockUrl;
	}
	
	@Override
    public String getContext() {
		return mockUrl.getContext();
    }

	@Override
    public String getPath() {
	    return mockUrl.getPath();
    }
	
	public void setPath(String path){
		if(!path.startsWith("/")){
			path = "/" + path;
		}
		mockUrl.setPath(path);
	}
	
	public void setAjax(boolean ajax){
		if(ajax){
			setHeader(HTTP.HEADER_NAME_AJAX_REQUEST, HTTP.HEADER_VALUE_AJAX_REQUEST);
		}else{
			removeHeader(HTTP.HEADER_NAME_AJAX_REQUEST);
		}
	}
	
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	public void removeHeader(String name){
		getHeaders().remove(name);
	}

	public void setHeader(String name,String value){
		getHeaders().put(name, new HTTP.Header(name, value));
	}
	
	public void setCookie(String name,String value){
		getCookies().put(name, new HTTP.Cookie(name,value));
	}
	
	public void setParameter(String name,String value){
		getParameters().put(name, new String[]{value});
	}
}
