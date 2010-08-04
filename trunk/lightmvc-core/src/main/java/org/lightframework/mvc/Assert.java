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


/**
 * assertion class of mvc framework
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0
 */
public final class Assert extends MvcException {
	
    private static final long serialVersionUID = -4499551185792411201L;
    
	private Assert(String message, Object... args) {
        super(message, args);
    }

	private static void fail(Assert e){
		throw e;
	}
	
	public static void fail(String message,Object... args){
		fail(new Assert(message,args));
	}
	
	public static void isTrue(boolean test,String failMessage,Object... args){
		if(!test){
			fail(failMessage,args);
		}
	}

	public static void notNull(String name,Object value) {
		if(null == value){
			fail(new Assert("@Assert.NotNull",name));
		}
	}
	
	public static void isNull(String name,Object value) {
		if(null != value){
			fail(new Assert("@Assert.Null",name));
		}
	}
	
	public static void notEmpty(String name,String value){
		if(null == value || value.trim().equals("")){
			fail(new Assert("@Assert.NotEmpty",name));
		}
	}
	
	public static void notEquals(String name,Object value,Object equalsTo){
		if(null != value && value.equals(equalsTo)){
			fail(new Assert("@Assert.NotEquals",name,value,equalsTo));
		}
	}
	
	public static void isEquals(String name,Object value,Object equalsTo){
		if(null != value && !value.equals(equalsTo)){
			fail(new Assert("@Assert.Equals",name,value,equalsTo));
		}
	}
}