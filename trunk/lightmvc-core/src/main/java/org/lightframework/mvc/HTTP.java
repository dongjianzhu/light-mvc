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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * defines http request and response 
 *
 * @author light.wind(lightworld.me@gmail.com)
 */
public final class HTTP {

	/**
	 * a http cookie
	 */
	public static class Cookie{
		protected String  name;
		protected String  domain;
		protected String  path;
		protected String  value;
		protected int     maxAge = -1;
		protected boolean secure = false;
		
		public String getName() {
	    	return name;
	    }

		public String getDomain() {
	    	return domain;
	    }

		public String getPath() {
	    	return path;
	    }

		public String getValue() {
	    	return value;
	    }

		/**
		 * Returns the maximum age of the cookie, specified in seconds, By default, -1 indicating the cookie will persist until browser shutdown.
		 * 
		 * @return an integer specifying the maximum age of the cookie in seconds; if negative, means the cookie persists until browser shutdown
		 */
		public int getMaxAge() {
	    	return maxAge;
	    }

		public boolean isSecure(){
			return secure;
		}
	}
	
	/**
	 * a http url
	 */
	public static class Url {
		protected String urlString;   //full url 
		protected int    port;
		protected String uriString;   //path without 'http://server:port' and query string
		protected String protocol;    //url protocol such as 'http',no version
		protected String queryString;
		
		public String getUrlString() {
        	return urlString;
        }
		
		public int getPort() {
        	return port;
        }

		public String getUriString() {
        	return uriString;
        }
		
		public String getProtocol() {
        	return protocol;
        }
		
		public String getQueryString() {
        	return queryString;
        }
	}
	
	/**
	 * a http header
	 */
	public static class Header {
		protected String name;
		protected List<String> values;
		
		public Header(){
			
		}
		
		public Header(String name,String value){
			this.name = name;
			this.values = new ArrayList<String>();
			this.values.add(value);
		}
		
		public String getName(){
			return name;
		}
		
		public List<String> getValues(){
			return values;
		}

		/**
		 * @return first value
		 */
		public String value(){
			return getValues() == null ? "" : getValues().get(0);
		}
	}	
	
	/**
	 * a http request
	 */
	public static class Request {
	    protected static final InheritableThreadLocal<Request> current = new InheritableThreadLocal<Request>();

	    protected Url     url;
	    protected String  context;
	    protected String  path;
	    protected String  remoteAddress;
	    protected String  contentType;
	    protected String  method;
	    protected boolean secure;
	    
	    protected Action      action;
	    protected Response    response;
	    protected Application application;

	    protected Map<String, Header> headers;
	    protected Map<String, Cookie> cookies;
	    protected Map<String, String> parameters;
	    protected Map<String, Object> attributes;

	    public static Request current(){
	    	return current.get();
	    }
	    
	    protected Request(){
	    	
	    }
	    
		public Url getUrl() {
	    	return url;
	    }
		
		public String getUrlString(){
			return null != url ? url.getUrlString() : null;
		}
		
		public String getUriString(){
			return null != url ? url.getUriString() : null;
		}
		
		public String getContext() {
	    	return context;
	    }

		/**
		 * path startwith '/'
		 * @return url path without context and query string of this request
		 */
		public String getPath() {
	    	return path;
	    }
		
		public String getQueryString() {
	    	return null != url ? url.getQueryString() : null;
	    }

		public String getRemoteAddress() {
	    	return remoteAddress;
	    }

		public String getContentType() {
	    	return contentType;
	    }

		public String getMethod() {
	    	return method;
	    }

		public boolean isSecure() {
	    	return secure;
	    }
		
		public String getParameter(String name){
			return getParameters().get(name);
		}
		
		public Map<String, String> getParameters() {
			if(null == parameters){
				parameters = new HashMap<String, String>();
			}
	    	return parameters;
	    }
		
		public String[] getParameterValues(String name){
			// TODO : HTTP.Request.getParameterValues
			return null;
		}

		public Header getHeader(String name){
			return getHeaders().get(name);
		}

		public Map<String, Header> getHeaders() {
			if(null == headers){
				headers = new HashMap<String, Header>();
			}
	    	return headers;
	    }

		public Cookie getCookie(String name){
			return getCookies().get(name);
		}

		public Map<String, Cookie> getCookies() {
			if(null == cookies){
				cookies = new HashMap<String, Cookie>();
			}
	    	return cookies;
	    }

		public Application getApplication() {
	    	return application;
	    }
		
		public Action getAction(){
			return action;
		}
		
		public Response getResponse(){
			return response;
		}
	}
	
	/**
	 * a http response
	 */
	public static class Response {

	    protected int          status = 200;
	    protected String       encoding = Application.DEFAULT_ENCODING;
	    protected String       contentType;
	    protected OutputStream out;
	    
	    protected Map<String, Header> headers;
	    protected Map<String, Cookie> cookies;
	    
	    protected Response(){
	    	
	    }
	    
	    public int getStatus() {
	    	return status;
	    }

	    public void setStatus(int status){
	    	this.status = status;
	    }
	    
		public String getContentType() {
	    	return contentType;
	    }

		public void setContentType(String contentType) {
	    	this.contentType = contentType;
	    }

		public Header getHeader(String name){
			return getHeaders().get(name);
		}
		
		public void setHeader(String name,String value){
			getHeaders().put(name, new Header(name,value));
		}

		public Map<String, Header> getHeaders() {
			if(null == headers){
				headers = new HashMap<String, Header>();
			}
	    	return headers;
	    }

		public Cookie getCookie(String name){
			return getCookies().get(name);
		}
		
	    public void setCookie(String name, String value) {
	        setCookie(name, value, (Integer)null);
	    }

	    public void setCookie(String name, String value, Integer maxAge) {
	        setCookie(name, value, null, null, maxAge);
	    }
	    
	    public void setCookie(String name, String value, String domain, String path, Integer maxAge) {
	    	Cookie cookie = getCookie(name);
	        if (null != cookie) {
	            if(null != value ) { cookie.value  = value;  }
	            if(null != domain) { cookie.domain = domain; }
	            if(null != path  ) { cookie.path   = path;   }
	            if(null != maxAge) { cookie.maxAge = maxAge; }
	        } else {
	            cookie = new Cookie();
	            cookie.name   = name;
	            cookie.domain = domain;
	            cookie.path   = (null == path || "".equals(path) ? "/" : path);
	            cookie.maxAge = null == maxAge ?  -1 : maxAge;
	            cookies.put(name, cookie);
	        }
	    }

		public Map<String, Cookie> getCookies() {
			if(null == cookies){
				cookies = new HashMap<String, Cookie>();
			}
	    	return cookies;
	    }
		
		public void redirect(String url){
			
		}
		
		public void forward(String path){
			
		}
		
		public void write(String content) throws IOException{
			if(null != content){
	            out.write(content.getBytes(encoding));
			}
		}
		
		public void writeln(String content) throws IOException{
			if(null != content){
	            out.write(content.getBytes(encoding + "\n"));
			}
		}
	}	
}
