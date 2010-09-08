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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.lightframework.mvc.HTTP.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mock object of {@link Response} for testing
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class MockResponse extends Response {
	private static final Logger log = LoggerFactory.getLogger(MockResponse.class);

	protected String forwardPath;
	protected String redirectUrl;
	protected ByteArrayOutputStream bytesOut;
	
	public MockResponse(){
		
	}
	
	public MockResponse(MockRequest request){
		this.setRequest(request);
	}
	
	public void setRequest(MockRequest request){
		this.request = request;
	}

	public String getContent() {
		if (null == bytesOut) {
			return null;
		}
		try {
			return bytesOut.toString(encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public MockJSONResult getJSONResult() {
		String content = getContent();
		if (null != content) {
			try {
				return MockJSONResult.parse(content);
			} catch (Exception e) {
				log.info("content is not json format : {}", e.getMessage());
			}
		}
		return null;
	}

	@Override
	public OutputStream getOut() {
		if (null == out) {
			bytesOut = new ByteArrayOutputStream();
			out = bytesOut;
		}
		return out;
	}

	@Override
	protected void forwardTo(String path) {
		this.forwardPath = path;
	}

	@Override
	protected void redirectTo(String url) {
		this.redirectUrl = url;
	}

	public String getForwardPath() {
		return forwardPath;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}
}
