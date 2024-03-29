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

import javax.servlet.ServletContext;

import org.lightframework.mvc.Application;
import org.lightframework.mvc.Module;

/**
 * mock object of {@link Application}
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class MockApplication extends Application {
	
	public MockApplication() {
	    super();
    }
	
	public MockApplication(Object context, Module root) {
	    super(context, root);
    }

	public MockApplication(ServletContext context, Module root) {
	    super(context, root);
    }

	static void mockSetCurrent(MockApplication application){
		Application.setCurrent(application);
	}
}
