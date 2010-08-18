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
package org.lightframework.mvc.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Home Controller
 * @author fenghm (live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class Home {
	
	private static final Logger log = LoggerFactory.getLogger(Home.class);

	/**
	 * index action , mapping request path '/' and view '/index.jsp' 
	 */
	public void index() {
		log.info("** index action of home controller executed **");
	}
	
}
