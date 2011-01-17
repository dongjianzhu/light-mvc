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

import junit.framework.TestCase;

import org.lightframework.mvc.render.json.JSON;
import org.lightframework.mvc.render.json.JSONObject;

/**
 * Test Case to test the decoder of {@link JSON} 
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestJSONDecode extends TestCase {

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
	
}
