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
package org.lightframework.mvc.internal.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>{@link ArrayUtils}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.1.0
 */
public final class ArrayUtils {

    public static final <T> List<T> toList(T[] array){
        List<T> list = new ArrayList<T>();
        
        for(T object : array){
            list.add(object);
        }
        
        return list;
    }
    
    public static final String[] toStrings(Object[] array){
        if(null == array){
            return null;
        }
        
        String[] strings = new String[array.length];
     
        for(int i=0;i<array.length;i++){
            Object object = array[i];

            if(null == object){
                strings[i] = null;
            }else if(object instanceof String){
                strings[i] = (String)object;
            }else{
                strings[i] = object.toString();
            }
        }
        
        return strings;
    }
}
