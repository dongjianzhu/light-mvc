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
package org.lightframework.mvc.core;

import org.lightframework.mvc.HTTP;
import org.lightframework.mvc.test.MvcTestCase;

/**
 * Test Case of {@link RequestPlugin}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestRequestPlugin extends MvcTestCase {

	public void testJsonBody() throws Exception{
		request.setMethod(HTTP.METHOD_GET);
		
		execute();
		
		assertFalse(request.isContainsParameter("name"));
		
		request.setMethod(HTTP.METHOD_POST);
		request.setContentType(HTTP.CONTENT_TYPE_JSON);
		request.setContent("{name:'xiaoming'}");
		
		execute();
		
		assertEquals("xiaoming", request.getParameter("name"));
	}
}
