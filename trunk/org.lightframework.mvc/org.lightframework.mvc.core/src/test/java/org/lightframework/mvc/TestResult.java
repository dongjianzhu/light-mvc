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

import org.lightframework.mvc.Result.Forward;
import org.lightframework.mvc.Result.Redirect;
import org.lightframework.mvc.test.MvcTestCase;

/**
 * Test Case of {@link Result}
 * @author fenghm (live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class TestResult extends MvcTestCase {
	
	public void testRedirect() throws Exception{
		module.setPackagee(packagee);
		request("/");
		
		Redirect redirect = new Redirect("~/test");
		redirect.render(request, response);
		assertEquals(request.getContext() + "/test", response.getRedirectUrl());
		
		redirect = new Redirect("/test");
		redirect.render(request, response);
		assertEquals("/test", response.getRedirectUrl());	
		
		redirect = new Redirect("http://www.sohu.com");
		redirect.render(request, response);
		assertEquals("http://www.sohu.com", response.getRedirectUrl());	
		
		redirect = new Redirect("test");
		redirect.render(request, response);
		assertEquals("/test", response.getRedirectUrl());	
		
		redirect = new Redirect("./test");
		redirect.render(request, response);
		assertEquals("/test", response.getRedirectUrl());
	}
	
	public void testForward() throws Exception{
		module.setPackagee(packagee);
		request("/");
		
		Forward foward = new Forward("/test");
		foward.render(request, response);
		assertEquals("/test", response.getForwardPath());	
		
		foward = new Forward("test");
		foward.render(request, response);
		assertEquals("/test", response.getForwardPath());	
		
		foward = new Forward("./test");
		foward.render(request, response);
		assertEquals("/test", response.getForwardPath());
	}	
}
