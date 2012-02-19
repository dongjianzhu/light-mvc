/*
 * Copyright 2012 the original author or authors.
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
package org.lightframework.mvc.internal.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * <code>{@link ReflectMethod}</code>
 *
 * @author fenghm (fenghm@bingosoft.net)
 *
 * @since 1.1.0
 */
public class ReflectMethod {
	
    private ReflectAccessor accessor;
    private int              index;
    private Method           method;
    
    ReflectMethod(ReflectAccessor accessor,Method method){
        this.accessor = accessor;
        this.method   = method;
        this.index    = accessor.getMethodIndex(method);
    }
    
    public Method method(){
        return method;
    }
    
    public Class<?>[] arguments(){
    	return method.getParameterTypes();
    }
    
    public Class<?> returnType(){
    	return method.getReturnType();
    }
    
    public boolean isStatic(){
        return Modifier.isStatic(method.getModifiers());
    }
    
    public Object invoke(Object instance,Object... args){
        if(index < 0){
            method.setAccessible(true);
            try {
                return method.invoke(instance, args);
            } catch (InvocationTargetException e){
                throw new ReflectException("error invoke method '" + method.getName() + "' : " + e.getCause().getMessage(),e.getCause());
            } catch (Exception e) {
                throw new ReflectException("error invoke method '" + method.getName() + "' : " + e.getMessage(),e);
            }
        }else{
            return accessor.invokeMethod(instance, index, args);
        }
    }
}