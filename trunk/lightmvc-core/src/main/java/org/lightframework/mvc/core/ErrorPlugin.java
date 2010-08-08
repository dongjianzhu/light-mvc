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

import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * core plugin to handle exceptions
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class ErrorPlugin extends Plugin {
	private static final Logger log = LoggerFactory.getLogger(ErrorPlugin.class);

	@Override
    public boolean error(Request request, Response response, Throwable exception)  {
		if(log.isInfoEnabled()){
			log.error("[mvc:error] -> {}",exception.getMessage(),exception);
		}
		
	    // TODO : implement ErrorPlugin.error
		//route by exception 
		/**
		 * 1.route excepton (no action found)
		 * 2.exception while param binding()  detail excepton for debug
		 * 2.forward exception(no render page found)
		 * 
		 * 4.Action invokation exception (app exception )
		 */
	    return super.error(request, response, exception);
    }
	
}
