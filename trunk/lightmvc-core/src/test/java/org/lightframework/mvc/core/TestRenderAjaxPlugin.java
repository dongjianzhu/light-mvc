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

import org.lightframework.mvc.HTTP;
import org.lightframework.mvc.MvcException;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.Result.Content;
import org.lightframework.mvc.config.Ajax;
import org.lightframework.mvc.json.JSONObject;
import org.lightframework.mvc.test.MvcTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Case of {@link RenderAjaxPlugin}.
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class TestRenderAjaxPlugin extends MvcTestCase {
	private static final Logger log = LoggerFactory.getLogger(TestRenderAjaxPlugin.class);

	protected String packages;
	
	@Override
    protected void setUpEveryTest() throws Exception {
		packages = TestRenderAjaxPlugin.class.getPackage().getName();
		module.setPackages(packages);
    }

	public void testRenderResult() throws Exception{
		RenderAjaxPlugin plugin = new RenderAjaxPlugin();
		Content content = new Content("text");
		plugin.renderJson(request, response, content);
		
		String json = plugin.encodeJson(content);
		
		assertEquals(HTTP.CONTENT_TYPE_JSON_RFC, response.getContentType());
		assertEquals(json, response.getContent());
		
		JSONObject jsonObject = new JSONObject(json);
		assertEquals("200", jsonObject.get(RenderAjaxPlugin.RETURN_CODE));
		assertEquals(JSONObject.NULL, jsonObject.get(RenderAjaxPlugin.RETURN_DESC));
		assertEquals("text", jsonObject.get(RenderAjaxPlugin.RETURN_VALUE));
	}
	
	public void testNotAjaxRequest() throws Exception{
		execute();
		assertNull(response.getContent());
	}
	
	public void testAjaxRequestByHeader() throws Exception {
		request.setHeader(HTTP.HEADER_NAME_AJAX_REQUEST, HTTP.HEADER_VALUE_AJAX_REQUEST);

		executeAjaxRequest();
	}
	
	public void testAjaxRequestByQueryString() throws Exception {
		request.setParameter(RenderAjaxPlugin.PARAM_AJAX_REQUEST, "true");
		
		executeAjaxRequest();
	}
	
	public void testAjaxRequestByAnnotation() throws Exception {
		request.setPath("/hello");
		
		executeAjaxRequest();
	}
	
	public void testAjaxRequestOfReturnError() throws Exception {
		request.setAjax(true);
		request.setPath("/error");
		
		newCopiedClass(Home.class, packages + ".Home");
		
		execute();
		
		log.info("result : {}",response.getContent());
		
		assertNotNull(response.getContent());
		JSONObject jsonObject = new JSONObject(response.getContent());
		assertEquals("500", jsonObject.get(RenderAjaxPlugin.RETURN_CODE));
		assertEquals("hello", jsonObject.get(RenderAjaxPlugin.RETURN_DESC));
		assertNotNull(jsonObject.get(RenderAjaxPlugin.RETURN_ERROR));
	}
	
	protected void executeAjaxRequest() throws Exception {
		newCopiedClass(Home.class, packages + ".Home");
		
		execute();
		
		assertNotNull(response.getContent());
		JSONObject jsonObject = new JSONObject(response.getContent());
		assertEquals("200", jsonObject.get(RenderAjaxPlugin.RETURN_CODE));
		assertEquals(JSONObject.NULL, jsonObject.get(RenderAjaxPlugin.RETURN_DESC));
		assertEquals("hello", jsonObject.get(RenderAjaxPlugin.RETURN_VALUE));
		
		log.info("result : {}",response.getContent());
	}
	
	public static class Home {
		public String index(){
			log.info("executed");
			return "hello";
		}
		
		@Ajax
		public String hello(){
			return "hello";
		}
		
		public Result.Error error() {
			return new Result.Error(new MvcException("hello"));
		}
	}
}
