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
package org.lightframework.mvc.test;

import java.util.Map;

import org.lightframework.mvc.core.RenderAjaxPlugin;
import org.lightframework.mvc.render.json.JSON;
import org.lightframework.mvc.render.json.JSONObject;

/**
 * represents the ajax response result of json format
 * 
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public final class MockJSONResult {
	private int    code;
	private String status;
	private String error;
	private String value;
	private String description;
	
	private MockJSONResult() {

	}
	
	public static MockJSONResult parse(String content) throws Exception{
		MockJSONResult result = new MockJSONResult();
		JSONObject json   = JSON.decode(content);

		Map<String, Object> map = json.map();
		
		if(map.containsKey(RenderAjaxPlugin.RETURN_CODE)){
			result.code = Integer.parseInt(map.get(RenderAjaxPlugin.RETURN_CODE).toString());
		}
		
		if(map.containsKey(RenderAjaxPlugin.RETURN_STATUS)){
			result.status = (String)map.get(RenderAjaxPlugin.RETURN_STATUS);
		}
		
		if(map.containsKey(RenderAjaxPlugin.RETURN_DESC)){
			result.description = (String)map.get(RenderAjaxPlugin.RETURN_DESC);
		}
		
		if(map.containsKey(RenderAjaxPlugin.RETURN_VALUE)){
			result.value = (String)map.get(RenderAjaxPlugin.RETURN_VALUE);
		}
		
		if(map.containsKey(RenderAjaxPlugin.RETURN_ERROR)){
			result.error = (String)map.get(RenderAjaxPlugin.RETURN_ERROR);
		}

		return result;
	}

	public int getCode() {
		return code;
	}
	
	public String getStatus(){
		return status;
	}

	public String getDescription() {
		return description;
	}

	public String getError() {
		return error;
	}

	public String getValue() {
		return value;
	}
}