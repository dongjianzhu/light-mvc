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

import java.util.Set;

import org.lightframework.mvc.Lang.Type;
import org.lightframework.mvc.exceptions.BindingException;

/**
 * the interface to convert string to object of the given {@link Type}
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0
 */
public interface ITypeBinder {
	
	/**
	 * @return all the supported types by this binder,at least return one
	 */
	Set<Class<?>> getSupportedTypes();

	/**
	 * convert string to object of the given type
	 * @param type {@link Type}
	 * @param string string value
	 * @return translated value of the given {@link Type}
	 */
	Object bind(Type type,String string) throws BindingException,Throwable;

}
