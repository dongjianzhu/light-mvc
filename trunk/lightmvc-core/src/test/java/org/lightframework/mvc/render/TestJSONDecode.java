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

import java.util.Map;

import org.lightframework.mvc.render.json.JSON;

import junit.framework.TestCase;

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
		Map<String, Object> map = JSON.decodeToMap(source);
		
		assertNotNull(map);
		assertEquals("xiaoming", map.get("name"));
		assertEquals(100l, map.get("age"));
	}
	
}
