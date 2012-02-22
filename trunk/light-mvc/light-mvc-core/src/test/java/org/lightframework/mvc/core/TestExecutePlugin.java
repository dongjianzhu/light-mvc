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
import org.lightframework.mvc.Result;
import org.lightframework.mvc.config.Http.Get;
import org.lightframework.mvc.config.Http.Post;
import org.lightframework.mvc.core.TestBindingPlugin.User;
import org.lightframework.mvc.test.MvcTestCase;

/**
 * Test Case of {@link ExecutePlugin}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestExecutePlugin extends MvcTestCase {
	
	@Override
    protected void setUpEveryTest() throws Exception {
		module.setPackagee(packagee);
		createSubClass(ProductController.class, packagee + ".ProductController");
    }

	public void testStaticAction() throws Throwable {
		request.setPath("/product");
		execute();
		assertNotNull(request.getAttribute("static_action"));
	}
	
	public void testReturnedAction() throws Throwable {
		request.setPath("/product/returned");
		execute();
		assertEquals("http://www.google.com", response.getRedirectUrl());
	}
	
	public void testHttpMethodAction() throws Throwable {
		request.setPath("/product/edit");
		request.setMethod(HTTP.METHOD_POST);
		
		execute();
		
		assertEquals(request.getAttribute("action"),"edit_post");
		
		request.setPath("/product/edit");
		request.setMethod(HTTP.METHOD_GET);
		
		execute();
		
		assertEquals(request.getAttribute("action"),"edit_get");
	}
	
	public static class ProductController {
		
		public static void index(){
			Result.setAttribute("static_action", true);
		}
		
		public static void returned(){
			Result.redirect("http://www.google.com");
		}
		
		@Get
		public void edit(String id){
			Result.setAttribute("action", "edit_get");
		}
		
		@Post
		public void edit(User user){
			Result.setAttribute("action", "edit_post");
		}
	}
}
