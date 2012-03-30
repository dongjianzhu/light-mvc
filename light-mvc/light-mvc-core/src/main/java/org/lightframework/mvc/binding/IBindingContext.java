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
package org.lightframework.mvc.binding;

import java.util.List;
import java.util.Map;

import org.lightframework.mvc.convert.IConverter;

/**
 * interface used by {@link Binder} to get argument's value
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public interface IBindingContext {

	/**
	 * get parameter value by the given name
	 *  
	 * @param name parameter name
	 * 
	 * @return {@link Object} value if found,else null
	 */
	Object getParameter(String name);
	
	/**
	 * @return all the parameters in context
	 */
	Map<String,Object> getParameters();
	
	/**
	 * @return external converters
	 */
	List<IConverter> getConverters();
	
}
