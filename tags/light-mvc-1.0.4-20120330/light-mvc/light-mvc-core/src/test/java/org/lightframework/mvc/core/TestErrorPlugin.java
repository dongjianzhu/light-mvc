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

import org.lightframework.mvc.test.MvcTestCase;

/**
 * Test Case of {@link ErrorPlugin}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestErrorPlugin extends MvcTestCase {

	@Override
    protected void setUpEveryTest() throws Exception {
		module.setPackagee(packagee);
		createSubClass(Home.class, packagee + ".Home");
    }

	public void testError() {
		try {
	        execute();
        } catch (Exception e) {
        	assertNotNull(e.getCause());
        	assertEquals("error test", e.getCause().getMessage());
        }
        assertEquals("no-cache",response.getHeader("Cache-Control"));
        assertEquals("no-cache",response.getHeader("Pragma"));
	}
	
	
	public static class Home{
		public void index() {
			throw new RuntimeException("error test");
		}
	}
}
