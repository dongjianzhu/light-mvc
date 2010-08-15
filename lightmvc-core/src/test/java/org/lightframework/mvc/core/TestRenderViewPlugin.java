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

import org.lightframework.mvc.test.JSONResult;
import org.lightframework.mvc.test.MvcTestCase;

/**
 * Test Case of {@link RenderViewPlugin}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestRenderViewPlugin extends MvcTestCase {
	
	@Override
    protected void setUpEveryTest() throws Exception {
		module.setPackagee(packagee);
		module.setFindWebRoot(false);
		createSubClass(Home.class, packagee + ".Home");
    }

	public void testHomeViewNotFound() throws Exception {
		execute();
		JSONResult result = response.getJSONResult();
		assertNotNull(result);
	}
	
	public void testHomeViewInRoot() throws Exception {
		module.addWebResource("/index.htm");
		module.addWebResource("/index.jsp");
		execute();
		assertEquals("/index.jsp", response.getForwardPath());
	}
	
	public void testHomeViewInViewRoot() throws Exception {
		module.addWebResource("/index.jsp");
		module.addViewResource("/index.html");
		execute();
		
		assertEquals(module.getViewResourcePath("/index.html"), response.getForwardPath());
	}
	
	public void testHomeViewInViewHome() throws Exception {
		module.addWebResource("/index.jsp");
		module.addViewResource("/index.html");
		module.addViewResource("/home/index.vm");
		execute();
		assertEquals(module.getViewResourcePath("/home/index.vm"), response.getForwardPath());
	}
	
	public void testNonHomeView() throws Exception {
		createSubClass(Product.class, packagee + ".Product");
		
		request.setPath("/product");
		module.addViewResource("/product/index.jsp");
		execute();
		assertEquals(module.getViewResourcePath("/product/index.jsp"), response.getForwardPath());
	}
	
	public static class Home {
		public void index(){
			
		}
	}
	
	public static class Product {
		public void index(){
			
		}
	}
}
