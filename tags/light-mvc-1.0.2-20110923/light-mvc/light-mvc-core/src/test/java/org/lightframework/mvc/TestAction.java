/*
 * Copyright 2011 the original author or authors.
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

import java.util.HashMap;
import java.util.Map;

import org.lightframework.mvc.test.MvcTestCase;

/**
 * TODO : document me
 *
 * @author User
 * @since 1.x.x
 */
public class TestAction extends MvcTestCase {
	public void testAction() throws Exception{
		Action action = new Action() ;
		action.method =  ActionTest.class.getMethods()[0] ;
		assertEquals(action.getReturnType(), String.class) ; 
		
		action.setParameter("name", "zhangsan") ;
		Map<String,Object> map = new HashMap<String, Object>() ;
		map.put("name1", "lishi") ;
		action.setParameters(map) ;
		assertEquals(action.getParameter("name"),"zhangsan") ; 
		assertEquals(action.getParameter("name1"),"lishi") ; 
	}
	
	static class ActionTest{
		public String stringMethod(){
			return null ;
		}
	}
}
