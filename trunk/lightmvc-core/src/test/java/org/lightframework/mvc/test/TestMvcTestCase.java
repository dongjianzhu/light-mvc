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

import java.util.concurrent.atomic.AtomicInteger;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;

/**
 * Test Case of {@link MvcTestCase}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestMvcTestCase extends MvcTestCase {
	
	protected String packages;
	
	@Override
    protected void setUpEveryTest() throws Exception {
		packages = TestMvcTestCase.class.getPackage().getName();
		module.setPackages(packages);
		createSubClass(Home.class, packages + ".Home");
    }

	public void testSetUp() throws Exception{
		assertNotNull(module);
		assertNotNull(request);
		assertNotNull(response);

		assertNotNull(request.getModule());
		assertNotNull(request.getMockUrl());
		assertNotNull(request.getMockUrl().getUriString());

		assertEquals(MockUrl.CONTEXT, request.getContext());
		assertEquals(MockUrl.PATH, request.getPath());
		
		assertEquals(request.getMockUrl().getUriString(), request.getUriString());
	}
	
	public void testHome() throws Exception {
		request.setPath("");
		
		execute();
		
		assertFalse(isIgnored());
		assertTrue(isManaged());
		
		Action action = request.getAction();
		assertNotNull(action);
		assertTrue(action.isResolved());
		assertTrue(action.isBinded());
		assertEquals("executed", Result.getAttribute("just_for_test"));
		
		Result result = request.getResult();
		assertNotNull(result);
		assertEquals(Result.STATUS_OK, result.getStatus());
		assertNull(result.getDescription());
		assertNull(result.getValue());
	}
	
	public void testModulePlugin() throws Exception {

		final AtomicInteger counter = new AtomicInteger();
		
		Plugin plugin = new Plugin() {
			@Override
            public boolean request(Request request, Response response) throws Exception {
				counter.incrementAndGet();
				return false;
            }

			@Override
            public Action[] route(Request request, Response response) throws Exception {
				counter.incrementAndGet();
	            return super.route(request, response);
            }

			@Override
            public boolean resolve(Request request, Response response, Action action) throws Exception {
				counter.incrementAndGet();
	            return super.resolve(request, response, action);
            }

			@Override
            public boolean binding(Request request, Response response, Action action) throws Exception {
				counter.incrementAndGet();
	            return super.binding(request, response, action);
            }

			@Override
            public Result execute(Request request, Response response, Action action) throws Exception {
				counter.incrementAndGet();
	            return super.execute(request, response, action);
            }
		};
		
		module.addPlugin(plugin);
		
		execute();
		
		assertEquals(5, counter.intValue());
	}
	
	public static class Home {
		public void index(){
			Result.setAttribute("just_for_test", "executed");
		}
	}
}
