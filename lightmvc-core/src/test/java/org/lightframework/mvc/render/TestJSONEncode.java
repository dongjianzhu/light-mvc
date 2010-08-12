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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.lightframework.mvc.render.json.JSON;
import org.lightframework.mvc.render.json.JSONWriter;

import junit.framework.TestCase;

/**
 * Test Case of {@link JSON}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestJSONEncode extends TestCase {
	
	public void testSimpleValue() throws Exception{
		//null
		assertEquals(JSONWriter.NULL_STRING,encode(null));
		
		//simple type
		assertEquals("'test'", encode("test"));
		assertEquals("'c'",    encode('c'));
		assertEquals("'c'",    encode(new Character('c')));
		assertEquals("true",   encode(true));
		assertEquals("true",   encode(new Boolean(true)));
		assertEquals("false",  encode(false));
		assertEquals("false",  encode(new Boolean(false)));
		assertEquals("100",    encode(100));
		assertEquals("100",    encode(new Integer(100)));
		assertEquals("200",    encode(new Long(200)));
		assertEquals("300.0",  encode(new Float(300)));
		assertEquals("100.1",  encode(100.1f));
		assertEquals("100.1",  encode(new Float(100.1f)));
		assertEquals("100.01", encode(new Double(100.01)));
		assertEquals("100",    encode(new BigDecimal(100)));
		assertEquals("100.1",  encode(new BigDecimal("100.1")));
		assertEquals("100000", encode(new BigInteger("100000")));
		assertEquals("0x01",   encode((byte)1));
		assertEquals("'RED'",  encode(Color.RED));
		
		//date type
		Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2010-11-01 10:10:10");
		assertEquals("2010-11-01T10:10:10" + getTimeZone(), encode(date));
	}
	
	public void testSimpleArray() throws Exception {
		assertEquals("['test1','test2']", encode(new String[]{"test1","test2"}));
		assertEquals("['c','d']",         encode(new char[]{'c','d'}));
		assertEquals("[true,false]",      encode(new boolean[]{true,false}));
		assertEquals("[100,101]",         encode(new int[]{100,101}));
		assertEquals("['RED','BLUE']",    encode(new Color[]{Color.RED,Color.BLUE}));
	}
	
	public void testSimpleIterable() throws Exception {
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		
		assertEquals("['1','2']", encode(list));
	}
	
	public void testSimpleMap() throws Exception {
		Map<Object,Object> map = new LinkedHashMap<Object, Object>();
		map.put(1, 1);
		map.put("2", "2");
		map.put(3, "3");
		
		assertEquals("{'1':1,'2':'2','3':'3'}", encode(map));
	}
	
	public void testSimpleBean() throws Exception {
		assertEquals("{'name':'xiaoming','age':100}", encode(new User("xiaoming",100)));
		assertEquals("{'key':'key','name':'xiaoming','age':100}", encode(new SuperUser("xiaoming",100,"key")));
	}
	
	protected String encode(Object value){
		return JSON.encode(value);
	}
	
	private static enum Color {
		RED,
		BLUE;
	}
	
	private static final String getTimeZone(){
		char c = '+';
		int offset = TimeZone.getDefault().getRawOffset();
		if(offset < 0){
			c = '-';
		}
		Date date = new Date(0);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return c + formatter.format(date);
	}
	
	public static class User {
		
		private String name;
		private int    age;
		
		private User(String name,int age){
			this.name = name;
			this.age  = age;
		}

		public String getName() {
        	return name;
        }

		public int getAge() {
        	return age;
        }
	}
	
	public static final class SuperUser extends User{
		
		private String key;
		
		private SuperUser(String name,int age){
			super(name,age);
		}
		
		private SuperUser(String name,int age,String key){
			this(name,age);
			this.key = key;
		}

		public String getKey() {
        	return key;
        }

		public void setKey(String key) {
        	this.key = key;
        }
	}
	
}
