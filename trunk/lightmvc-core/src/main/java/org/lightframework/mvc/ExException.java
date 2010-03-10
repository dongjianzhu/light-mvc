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
package org.lightframework.mvc;

import org.lightframework.mvc.Utils.Messages;

/**
 * exception class of mvc framework
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public class ExException extends RuntimeException {
    private static final long serialVersionUID = 1056914628721520096L;

	public ExException() {
		super();
	}
	
	public ExException(Throwable cause){
		super(cause);
	}

	public ExException(String message,Object... args){
		super(i18n(message,args));
	}
	
	public ExException(String message,Throwable cause,Object... args){
		super(i18n(message,args),cause);
	}
	
	protected static String i18n(String message,Object... args){
		if(null != message && message.charAt(0) == '@'){
			return Messages.getString(message,args);
		}else{
			return null == message ? "" : message;
		}
	}
}
