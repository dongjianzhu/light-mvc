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

import org.lightframework.mvc.HTTP.Url;

/**
 * mock object of {@link Url} for testing.
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class MockUrl extends Url {
	
	public static final String PROTOCOL       = "HTTP";
	public static final String SERVER_NAME    = "localhost";
	public static final int    SERVER_PORT    = 8080;
	public static final String CONTEXT        = "/test";
	public static final String PATH           = "";
	
	protected String server;
	protected String context;
	protected String path;
	
	public MockUrl(){
		this.protocol = PROTOCOL;
		this.server   = SERVER_NAME;
		this.port     = SERVER_PORT;
		this.context  = CONTEXT;
		this.path     = PATH;
	}
	
	@Override
    public String getUrlString() {
		return "http://" + server + ":" + port + getUriString();
    }
	
	@Override
    public String getUriString() {
		return context + path;
	}
	
	public String getContext() {
    	return context;
    }

	public String getPath() {
    	return path;
    }

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void setContext(String context) {
    	this.context = context;
    }

	public void setPath(String path){
		this.path = path;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public void setServer(String server) {
    	this.server = server;
    }

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}
