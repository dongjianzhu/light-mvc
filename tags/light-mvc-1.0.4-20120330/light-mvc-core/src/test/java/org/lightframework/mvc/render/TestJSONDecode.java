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
package org.lightframework.mvc.render;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

import org.lightframework.mvc.render.json.JSON;
import org.lightframework.mvc.render.json.JSONArray;
import org.lightframework.mvc.render.json.JSONObject;

/**
 * Test Case to test the decoder of {@link JSON} 
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestJSONDecode extends TestCase {
	public void testReader(){
		
		String source = "{name:'xiaoming',childs:[{name:'c1'},{name:'c2'}]}";
		Reader reader = new StringReader(source) ;
		JSONObject json = JSON.decode(reader);
		
		assertNotNull(json);
		assertEquals("xiaoming", json.get("name"));
		
		JSONArray array = json.getJSONArray("childs");
		assertNotNull(array);
		assertEquals(2, array.length());
		
		JSONObject child1 = array.getObject(0);
		JSONObject child2 = array.getObject(1);
		
		assertNotNull(child1);
		assertEquals("c1", child1.get("name"));
		
		assertNotNull(child2);
		assertEquals("c2", child2.get("name"));
	}
	

	public void testMap() {
		String source = "{\"name\":'xiaoming','age':100};";
		JSONObject json = JSON.decode(source);
		
		assertNotNull(json);
		assertNotNull(json.map());
		assertEquals("xiaoming", json.get("name"));
		assertEquals("xiaoming", json.getString("name"));
		assertEquals(100, json.get("age"));
		assertEquals(new Integer(100), json.getInteger("age"));
	}
	
	public void testArray() {
		
		String source = "[1,2,3]";
		JSONObject json = JSON.decode(source);
		
		assertNotNull(json);
		assertTrue(json.isArray());
		assertNotNull(json.array());
		
		JSONArray array = json.jsonArray();
		assertEquals(1, array.get(0));
		assertEquals(2, array.get(1));
		assertEquals(3, array.get(2));
	}

	public void testMapArray() {
		String source = "{name:'xiaoming',childs:[{name:'c1'},{name:'c2'}]}";
		JSONObject json = JSON.decode(source);
		
		assertNotNull(json);
		assertEquals("xiaoming", json.get("name"));
		
		JSONArray array = json.getJSONArray("childs");
		assertNotNull(array);
		assertEquals(2, array.length());
		
		JSONObject child1 = array.getObject(0);
		JSONObject child2 = array.getObject(1);
		
		assertNotNull(child1);
		assertEquals("c1", child1.get("name"));
		
		assertNotNull(child2);
		assertEquals("c2", child2.get("name"));
	}

}
