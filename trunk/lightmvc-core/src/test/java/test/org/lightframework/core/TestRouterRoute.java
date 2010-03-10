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
package test.org.lightframework.core;

import junit.framework.TestCase;

import org.lightframework.mvc.routing.Match;
import org.lightframework.mvc.routing.Route;

/**
 * Test Case to test {@link Route}
 *
 * @author light.wind(lightworld.me@gmail.com)
 */
public class TestRouterRoute extends TestCase{

	public void testMatch1() {
		Route route = Route.compile("get","/{controller}{id}/{action}","{controller}.{action}");
	    Match match = route.matches("GET", "/user1/get");

	    assertTrue(match.isMatched());
	    assertEquals("user", match.getParameter("controller"));
	    assertEquals("1", match.getParameter("id"));
	    assertEquals("get", match.getParameter("action"));
	    assertEquals("user.get",match.getName());
	    
	    assertFalse(route.matches("post", "/").isMatched());
	    assertFalse(route.matches("get", "/a/b/c").isMatched());
	}
	
	public void testMatch2(){
	    Route route = Route.compile("post", "/{controller}.{action}.do", "{controller}.{action}");
	    Match match = route.matches("/user.list.do");
	    assertTrue(match.isMatched());
	    assertEquals("user", match.getParameter("controller"));
	    assertEquals("list", match.getParameter("action"));
	    
	    assertFalse(route.matches("/user.a/list.do").isMatched());
	}
	
	public void testMatch3(){
	    Route route = Route.compile("post", "/{controller}/{action}.do", "{controller}.{action}");
	    Match match = route.matches("/user/list.do");
	    assertTrue(match.isMatched());
	    assertEquals("user", match.getParameter("controller"));
	    assertEquals("list", match.getParameter("action"));
	}
	
	public void testMatch4(){
	    Route route = Route.compile("*", "/", "home.index");
	    Match match = route.matches("/");
	    assertTrue(match.isMatched());
	    assertEquals("home.index", match.getName());
	}
	
	public void testMatch5(){
		Route route = Route.compile("*", "/{controller*}/{action}", "{controller}.{action}");
		Match match = route.matches("/a/b/c/d");
		assertTrue(match.isMatched());
		assertEquals("a.b.c.d", match.getName());
	}
}
