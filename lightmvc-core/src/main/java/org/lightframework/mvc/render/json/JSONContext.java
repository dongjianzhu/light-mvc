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
package org.lightframework.mvc.render.json;

import org.lightframework.mvc.render.IRenderContext;
import org.lightframework.mvc.render.IRenderWriter;
import org.lightframework.mvc.render.RenderContext;

/**
 * json formatt of {@link IRenderContext}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class JSONContext extends RenderContext implements IRenderContext {
	
	private static final JSONWriter JSON_WRITER = new JSONWriter();
	
	protected JSONWriter writer = JSON_WRITER;

	public IRenderWriter getWriter() {
		return writer;
	}
	
	public void beforeEncodeBegin(String name,Object value, StringBuilder out) {

	}

	public void beforeEncodeEnd(String name,Object value, StringBuilder out) {

	}

	public boolean ignore(String name, Object value) {
	    return false;
    }
}
