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
package org.lightframework.mvc.plugin.spring;

import org.lightframework.mvc.RouteManager;

import junit.framework.TestCase;

/**
 * Test Case of {@link RouteTable}
 * @author fenghm (fenghm@bingosoft.net)
 * @since 1.0.0
 */
public class TestRouteTable extends TestCase {

	public void test(){
		
		String string = 
			"*		/user/{id}			user.view\n" + 
			"get	/user/delete/{id}	user.delete\r\n" + 
			"post	/					home.index";
		
		
		RouteTable table = new RouteTable();
		table.setRoutes(string);
		
		assertEquals(3,   table.routes().size());
		assertEquals("*", table.routes().get(0).getMethod());
		assertEquals("/user/{id}", table.routes().get(0).getPath());
		assertEquals("user.view", table.routes().get(0).getAction());
		assertEquals("get", table.routes().get(1).getMethod());
		assertEquals("/user/delete/{id}", table.routes().get(1).getPath());
		assertEquals("user.delete", table.routes().get(1).getAction());	
		assertEquals("post", table.routes().get(2).getMethod());
		assertEquals("/", table.routes().get(2).getPath());
		assertEquals("home.index", table.routes().get(2).getAction());			
		
		table.register();
		System.out.println(RouteManager.table().size());
		assertEquals(10, RouteManager.table().size());
	}
}
