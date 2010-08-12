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

/**
 * represents the ajax response result of json format
 * 
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public final class JSONResult {
	private int    code;
	private String status;
	private String error;
	private String value;
	private String description;
	
	private JSONResult() {

	}
	
	public static JSONResult parse(String content) throws Exception{
		JSONResult result        = new JSONResult();
		Map<String, Object> json = JSON.decodeToMap(content);

		
		if(json.containsKey(RenderAjaxPlugin.RETURN_CODE)){
			result.code = Integer.parseInt(String.valueOf(json.get(RenderAjaxPlugin.RETURN_CODE)));
		}
		
		if(json.containsKey(RenderAjaxPlugin.RETURN_STATUS)){
			result.status = (String)json.get(RenderAjaxPlugin.RETURN_STATUS);
		}
		
		if(json.containsKey(RenderAjaxPlugin.RETURN_DESC)){
			result.description = (String)json.get(RenderAjaxPlugin.RETURN_DESC);
		}
		
		if(json.containsKey(RenderAjaxPlugin.RETURN_VALUE)){
			result.value = (String)json.get(RenderAjaxPlugin.RETURN_VALUE);
		}
		
		if(json.containsKey(RenderAjaxPlugin.RETURN_ERROR)){
			result.error = (String)json.get(RenderAjaxPlugin.RETURN_ERROR);
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