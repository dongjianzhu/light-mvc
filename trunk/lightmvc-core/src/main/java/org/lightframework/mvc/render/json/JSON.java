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
import org.lightframework.mvc.render.IRenderable;
import org.lightframework.mvc.render.Renderable;

/**
 * json encoder
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class JSON extends Renderable implements IRenderable{
	
	private static final JSON        instance = new JSON();
	private static final JSONContext context  = new JSONContext();
	
	public static String encode(Object value){
		return instance.encode(value, (IRenderContext)context);
	}
	
	public static String encode(Object value,JSONContext context){
		return instance.encode(value, (IRenderContext)context);
	}
}
