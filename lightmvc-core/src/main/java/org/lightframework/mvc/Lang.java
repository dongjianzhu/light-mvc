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

import java.lang.annotation.Annotation;

/**
 * utility class,internal used only
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public final class Lang {

	public final static class Ref<E> {
		public E value;
		public Ref(E value){
			this.value = value;
		}
	}
	
	public static class Type {
		public static final Annotation[] EMPTY_CONFIGS = new Annotation[0];
		
		public Type(){
			
		}
		
		public Type(String name,Class<?> type){
			this.name = name;
			this.type = type;
		}
		
		public Type(String name,Class<?> type,Annotation[] configs){
			this.name    = name;
			this.type    = type;
			this.configs = configs;
		}
		
		protected String       name;
		protected Class<?>     type;
		protected Annotation[] configs = EMPTY_CONFIGS;
		public String getName(){
			return name;
		}
		public Class<?> getType() {
        	return type;
        }
		public Annotation[] getConfigs() {
			if(null == configs){
				return EMPTY_CONFIGS;
			}
        	return configs;
        }
	}
}
