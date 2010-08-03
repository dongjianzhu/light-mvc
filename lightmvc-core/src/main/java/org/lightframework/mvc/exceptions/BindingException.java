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
package org.lightframework.mvc.exceptions;

import org.lightframework.mvc.MVCException;

/**
 * represents the error at binding value to action's argument
 *
 * @author fenghm(live.fenghm@gmail.com)
 */
public class BindingException extends MVCException {

    private static final long serialVersionUID = -8713208104815817L;

	public BindingException() {
		
	}

	public BindingException(Throwable cause) {
		super(cause);
	}

	public BindingException(String message, Object... args) {
		super(message, args);
	}

	public BindingException(String message, Throwable cause, Object... args) {
		super(message, cause, args);
	}
}
