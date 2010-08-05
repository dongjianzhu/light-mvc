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

/**
 * mock object of {@link Response} for testing
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class MockResponse extends Response {
	
	private ByteArrayOutputStream bytesOut;

	public String getContent() {
		try {
	        return bytesOut.toString(encoding);
        } catch (UnsupportedEncodingException e) {
        	throw new RuntimeException(e);
        }
	}

	@Override
    public OutputStream getOut() {
		if(null == out){
			bytesOut = new ByteArrayOutputStream();
			out = bytesOut;
		}
		return out;
    }
}
