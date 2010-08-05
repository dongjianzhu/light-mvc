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
package org.lightframework.mvc.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.Result;
import org.lightframework.mvc.Action.Argument;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.lightframework.mvc.Result.Return;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * core plugin to resolve and execute an {@link Action}
 *
 * @author fenghm(live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class ExecutePlugin extends Plugin {
	
	private static final Object[] EMPTY_EXECUTE_ARGS = new Object[]{};
	
	@Override
    public Result execute(Request request, Response response, Action action) throws Exception{
		//execute java method
		Method method = action.getMethod();
		
		try{
			Object value  = null;
			Object[] args = getExecuteArgs(action.getArguments());
			
			if(ClassUtils.isStatic(action.getMethod())){
				value = method.invoke(null, args); //static invoke
			}else{
				value = method.invoke(action.getControllerObject(), args);
			}
			
			if(value instanceof Result){
				return (Result)value;
			}else{
				return new Result.Empty();
			}
		} catch(Return returned){
			return returned.result();
		} catch(InvocationTargetException e){
			if(e.getTargetException() instanceof Return){
				return ((Return)e.getTargetException()).result();
			}else{
				throw e;
			}
		}catch(Exception e){
			throw e;
		}
    }
	
	private static Object[] getExecuteArgs(Argument[] args){
		if(null == args || args.length == 0){
			return EMPTY_EXECUTE_ARGS;
		}else{
			Object[] array = new Object[args.length];
			for(int i=0; i<args.length; i++){
				array[i] = args[i].getValue();
			}
			return array;
		}
	}
}
