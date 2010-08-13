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
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.render.json.JSON;
import org.lightframework.mvc.render.json.JSONObject;
import org.lightframework.mvc.render.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * built-in {@link Plugin} to prepare {@link Request}.
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class RequestPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(RequestPlugin.class);

	@Override
    public boolean request(Request request, Response response) throws Exception {
		parseJson(request, response);
		
	    return false;
    }
	
	protected void parseJson(Request request, Response response) throws Exception {
		if(request.isPost() && HTTP.CONTENT_TYPE_JSON.equalsIgnoreCase(request.getContentType())){
			String body = request.getContent();
			if(null != body && !"".equals((body = body.trim()))){
				if(log.isTraceEnabled()){
					log.trace("[mvc:request] -> found json body : '{}'",body);
				}
				
				if(body.charAt(0) == JSONWriter.OPEN_OBJECT){
					try {
		                JSONObject json = JSON.decode(request.getContent());
		                for(String name : json.keys()){
		                	request.setParameter(name, json.getString(name));
		                }
	                } catch (Exception e) {
	                	Result.error(Result.CODE_BAD_REQUEST,"invalid json params");
	                }	
				}else{
					if(log.isTraceEnabled()){
						log.trace("[mvc:request] -> json is not a map params(starts with '{'),ignore it");
					}
				}
			}
		}
	}
}
