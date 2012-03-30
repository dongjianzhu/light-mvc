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

import org.lightframework.mvc.Application;
import org.lightframework.mvc.Framework;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;


/**
 * mock object of {@link Framework} for testing.
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class MockFramework extends Framework {

	public static void mockStart(Application app){
		Framework.start(app);
	}
	
	public static void mockStop(Application app){
		Framework.stop(app);
	}
	
	public static boolean mockIgnore(Request request) throws Throwable {
		return Framework.ignore(request);
	}
	
	public static boolean mockHandle(Request request, Response response) throws Throwable {
		return Framework.handle(request, response);
	}
	
	public static void mockHandleFinally(Request request,Response response) {
		Framework.handleFinally(request, response);
	}
}
