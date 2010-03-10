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
package org.lightframework.mvc.routing;

import org.lightframework.mvc.Action;

/**
 * reprensents a matched action return by {@link Route#matches(String)}
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public final class Match extends Action{
    boolean matched;
	
	public boolean isMatched(){
		return matched;
	}
}