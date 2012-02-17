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
package org.lightframework.mvc.internal.params;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @since 1.x.x
 */
public class TestParameters extends TestCase{
	
	@SuppressWarnings("unchecked")
    public void testParamObject(){
		Map map = new HashMap() ;
		map.put("int", 123) ;
		map.put("string", "字符") ;
		map.put("bool", true) ;
		map.put("array", new String[]{"A","B"}) ;
		Parameters object = new Parameters() ;
		object.map = map ;
		
		assertEquals(object.isEmpty(), false) ;
		assertEquals(object.getInt("int") , 123) ;
		assertEquals(object.getString("string") , "字符") ;
		assertEquals(object.getBoolean("bool") ,Boolean.TRUE ) ;
		assertEquals(object.getInteger("int") ,new Integer(123) ) ;
		assertEquals(object.getLong("int") ,new Long(123) ) ;
		assertEquals(object.getArray("array")[0] ,"A" ) ;
		assertNotNull(object.getBean("array")) ;
		
		assertTrue( object.contains("array")) ;
		assertEquals(object.map() ,map ) ;
	}
}
