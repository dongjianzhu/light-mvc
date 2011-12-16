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
package org.lightframework.mvc.core;

import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;

/**
 * core response plugin to handle {@link #response(org.lightframework.mvc.HTTP.Request, org.lightframework.mvc.HTTP.Response, org.lightframework.mvc.Result)} .
 * 
 * <br/>
 * 
 * set no cache control in response header
 * 
 * @author fenghm (fenghm@bingosoft.net)
 * 
 * @since 1.0.2
 */
public class ResponsePlugin extends Plugin {

	@Override
    public boolean response(Request request, Response response, Result result) throws Exception {
		response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
		response.setHeader("Pragma","no-cache"); //HTTP 1.0
		response.setHeader("Expires", "-1"); //prevents caching at the proxy server
		return false;
    }
	
}
