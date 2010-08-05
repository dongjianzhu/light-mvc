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
package org.lightframework.mvc.json;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Case of {@link JSON}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestJSON extends TestCase {
	private static final Logger log = LoggerFactory.getLogger(TestJSON.class);

	public void testPrimitiveEncode(){
		JSON json = new JSON();
		
		assertEquals("'hello'", json.encode("hello"));
		assertEquals("true", json.encode(true));
		assertEquals("1", json.encode(1));
	}
	
	public void testMapEncode() throws Exception{
		JSON json = new JSON();
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		assertEquals("{}", json.encode(map));
		
		map.put("name", "xiaoming");
		map.put("age", 100);
		
		Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2010-11-01");
		
		map.put("birthday", date);
		
		String encoded = json.encode(map);
		
		log.info("encoded : {}",encoded);
		
		JSONObject jsonObject = new JSONObject(encoded);
		assertEquals("xiaoming", jsonObject.get("name"));
		assertEquals(100, jsonObject.get("age"));
		assertEquals(JSON.toRfcString(date), jsonObject.get("birthday"));
	}
	
	public void testObjectEncode() throws Exception{
		JSON json = new JSON();
		
		json.alias("props", "properties");
		
		TestBean bean = new TestBean();
		bean.setName("xiaoming");
		bean.setAge(100);
		bean.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("2010-11-01"));
		bean.getProperties().put("name", "xiaoming");
		bean.getProperties().put("age", 100);
		
		String encoded = json.encode(bean);
		
		log.info("encoded : {}",encoded);
		
		JSONObject jsonObject = new JSONObject(encoded);
		assertEquals("xiaoming", jsonObject.get("name"));
		assertEquals(100, jsonObject.get("age"));
		assertEquals(JSON.toRfcString(bean.getBirthday()), jsonObject.get("birthday"));
		
		JSONObject props = jsonObject.getJSONObject("props");
		assertNotNull(props);
		assertEquals("xiaoming", props.get("name"));
		assertEquals(100, props.get("age"));
	}
	
	public static final class TestBean {
		
		private String name;
		private int    age;
		private Date   birthday;
		private Map<String, Object> properties = new HashMap<String, Object>();
		public String getName() {
        	return name;
        }
		public void setName(String name) {
        	this.name = name;
        }
		public int getAge() {
        	return age;
        }
		public void setAge(int age) {
        	this.age = age;
        }
		public Date getBirthday() {
        	return birthday;
        }
		public void setBirthday(Date birthday) {
        	this.birthday = birthday;
        }
		public Map<String, Object> getProperties() {
        	return properties;
        }
	}
}
