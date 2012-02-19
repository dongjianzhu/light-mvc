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
import org.lightframework.mvc.test.MockRequest;
import org.lightframework.mvc.test.MvcTestCase;

/**
 * Test Case of {@link Request}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestRequest extends MvcTestCase {

	public void testIsAjax(){
		MockRequest request = new MockRequest();

		assertFalse(request.isAjax());

		request.setHeader(HTTP.HEADER_NAME_AJAX_REQUEST, HTTP.HEADER_VALUE_AJAX_REQUEST);
		assertTrue(request.isAjax());
	}
	
	public void testBase() throws Throwable{
		execute();
		Request request = Request.current();
		assertNotNull(request);
		assertNotNull(request.getResponse());
		assertNotNull(request.getSession());
	}
}
