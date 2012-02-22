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
package org.lightframework.mvc.render.json;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.lightframework.mvc.internal.reflect.ReflectField;
import org.lightframework.mvc.internal.reflect.ReflectType;
import org.lightframework.mvc.internal.reflect.ReflectUtils;


/**
 * <code>{@link JSONH}</code>
 *
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public final class JSONH {
    
    public static Object pack(Object object){
        return pack(object,(Class<?>)null);
    }
    
    public static Object pack(Object value,Type genericType){
        if (null == value) {
            return null;
        }else if (value instanceof Object[]) {
            if(null != genericType){
                return doPack(ReflectUtils.getActualType(genericType),(Object[])value);
            }else{
                return doPack((Object[])value);
            }
        } else if (value instanceof Iterable<?>) {
            if(null != genericType){
                return doPack(ReflectUtils.getActualType(genericType),(Iterable<?>)value);
            }else{
                return doPack((Iterable<?>)value);
            }
        } else if (value.getClass().isArray()) {
            if(null != genericType){
                return doPackArray(ReflectUtils.getActualType(genericType),value);
            }else{
                return doPackArray(value);
            }            
        }
        
//        else if (value instanceof Enumeration<?>) {
//            if(null != genericType){
//                return doPack(getActualType(genericType),(Enumeration<?>)value);
//            }else{
//                return doPack((Enumeration<?>)value);
//            }
//        }
        return value;
    }
    
    public static Object pack(Object value,Class<?> componentType){
        if (null == value) {
            return null;
        }else if (value instanceof Object[]) {
            if(null != componentType){
                return doPack(componentType,(Object[])value);
            }else if(!value.getClass().getComponentType().equals(Object.class)){
                return doPack(value.getClass().getComponentType(),(Object[])value);
            }else{
                return doPack((Object[])value);
            }
        } else if (value.getClass().isArray()) {
            if(null != componentType){
                return doPackArray(componentType,value);
            }else{
                return doPackArray(value);
            }
        } else if (value instanceof Iterable<?>) {
            if(null != componentType){
                return doPack(componentType,(Iterable<?>)value);
            }else{
                return doPack((Iterable<?>)value);
            }
        }
        return value;
    }
    
    @SuppressWarnings("unchecked")
    public static Object unpack(Object json){
        if(json == null){
            return null;
        }
        
        if(json instanceof JSONObject){
            return unpack(((JSONObject)json).value());
        }
        
        Class<?> clazz = json.getClass();
        
        if(Map.class.isAssignableFrom(clazz)){
            Map map = (Map)json;
            
            if(map.keySet().size() == 1){
                String key = (String)map.keySet().iterator().next();
                if(key.equals("jsonh")){
                    Object packed = map.get(key);
                    
                    if(packed instanceof List){
                        return doUnpack((List)packed);
                    }
                    throw new JSONException("unsupported packed data : " + packed);
                }
            }
            
            return json;
        }
        
        if(List.class.isAssignableFrom(clazz)){
            List list = (List)json;
            for(int i=0;i<list.size();i++){
                Object item = list.get(i);
                Object unpack = unpack(item);
                if(item != unpack){
                    list.set(i, unpack);
                }
            }
            return json;
        }
        
        if(json instanceof Object[]){
            Object[] list = (Object[])json;
            for(int i=0;i<list.length;i++){
                Object item = list[i];
                Object unpack = unpack(item);
                if(item != unpack){
                    list[i] = unpack;
                }
            }
            return json;
        }
        
        return json;
    }
    
    private static Object doUnpack(List<?> packed){
        int keyLength = (Integer)packed.get(0);
        
        String[] keys  = new String[keyLength];
        
        int index = 1;
        
        //keys
        for(;index <= keyLength;index++){
            keys[index - 1] = (String)packed.get(index);
        }
        
        //rows
        int rows = (packed.size() - keyLength - 1) / keyLength;
        
        Object[] unpacked = new Object[rows];
        for(int i=0;i<rows;i++){
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            for(int j=0;j<keyLength;j++){
                row.put(keys[j], unpack(packed.get(index++)));
            }
            unpacked[i] = row;
        }
        
        return unpacked;
    }
    
    private static Object doPack(Iterable<?> rows){
        List<Object> compressed = new ArrayList<Object>(); 
        
        Iterator<?> iterator = rows.iterator();

        if(iterator.hasNext()){
           Object row = null;
           
           while(iterator.hasNext()) {
               row = iterator.next();
               if(null != row){
                   break;
               }
           }
           
           if(null != row){
               if(!ReflectType.isBeanType(row.getClass())){
                   return rows;
               }
               
               ReflectField[] fields = doPackKeys(compressed,row.getClass());
               
               //rows
               while(iterator.hasNext()){
                   row = iterator.next();
                   if(null == row){
                       continue;
                   }
                   for(ReflectField field : fields){
                       if(field.isArray() || field.isIterable()){
                           compressed.add(pack(field.getValue(row),field.getCompoenentType())); 
                       }else{
                           compressed.add(field.getValue(row));   
                       }
                   }
               }
           }
        }
        return wrapPackObject(compressed);
    }
    
    private static Object doPack(Class<?> clazz,Iterable<?> rows){
        if(!ReflectType.isBeanType(clazz)){
            return rows;
        }
        
        List<Object> compressed = new ArrayList<Object>(); 
        
        ReflectField[] fields = doPackKeys(compressed,clazz);
        
        //rows
        for(Object row : rows){
            if(null == row){
                continue;
            }
            for(ReflectField field : fields){
                if(field.isArray() || field.isIterable()){
                    compressed.add(pack(field.getValue(row),field.getCompoenentType())); 
                }else{
                    compressed.add(field.getValue(row));   
                }
            }
        }
        return wrapPackObject(compressed);
    }
    
    private static Object doPack(Object[] rows){
        if(rows.length > 0){
           Object row = null;
           
           int i = 0;
           for(;i<rows.length;i++){
               row = rows[i];
               if(null != row){
                   break;
               }
           }
           
           if(null != row){
               if(!ReflectType.isBeanType(row.getClass())){
                   return rows;
               }
               
               ReflectType type        = ReflectType.get(row.getClass());
               ReflectField[] fields   = type.getPublicFields();
               List<Object> compressed = new ArrayList<Object>(fields.length * rows.length + fields.length + 1); 
               
               //key length
               compressed.add(fields.length);
               
               //keys
               for(ReflectField field : fields){
                   compressed.add(field.getName());
               }
               
               //rows
               for(;i<rows.length;i++){
                   row = rows[i];
                   if(null == row){
                       continue;
                   }
                   for(ReflectField field : fields){
                       if(field.isArray() || field.isIterable()){
                           compressed.add(pack(field.getValue(row),field.getCompoenentType())); 
                       }else{
                           compressed.add(field.getValue(row));   
                       }
                   }
               }
               return wrapPackObject(compressed);
           }
        }
        return rows;
    }
    
    private static Object doPack(Class<?> clazz,Object[] rows){
        if(!ReflectType.isBeanType(clazz)){
            return rows;
        }
        
        ReflectType type        = ReflectType.get(clazz);
        ReflectField[] fields   = type.getPublicFields();
        List<Object> compressed = new ArrayList<Object>(fields.length * rows.length + fields.length + 1); 
        
        //key length
        compressed.add(fields.length);
        
        //keys
        for(ReflectField field : fields){
            compressed.add(field.getName());
        }
        
        //rows
        for(Object row : rows){
            if(null == row){
                continue;
            }
            for(ReflectField field : fields){
                if(field.isArray() || field.isIterable()){
                    compressed.add(pack(field.getValue(row),field.getCompoenentType())); 
                }else{
                    compressed.add(field.getValue(row));   
                }
            }
        }
        return wrapPackObject(compressed);
    }
    
//    private static Object doPack(Enumeration<?> rows){
//        List<Object> compressed = new ArrayList<Object>(); 
//        
//        if(rows.hasMoreElements()){
//           Object row = null;
//           
//           while(rows.hasMoreElements()) {
//               row = rows.nextElement();
//               if(null != row){
//                   break;
//               }
//           }
//           
//           if(null != row){
//               if(!ReflectType.isBeanType(row.getClass())){
//                   return rows;
//               }
//               
//               ReflectField[] fields = doPackKeys(compressed,row.getClass());
//               
//               //rows
//               while(rows.hasMoreElements()){
//                   row = rows.nextElement();
//                   if(null == row){
//                       continue;
//                   }
//                   for(ReflectField field : fields){
//                       compressed.add(pack(field.getValue(row),field.genericType()));
//                   }
//               }
//           }
//        }
//        return wrapPackObject(compressed);
//    }
//    
//    private static Object doPack(Class<?> clazz,Enumeration<?> rows){
//        if(!ReflectType.isBeanType(clazz)){
//            return rows;
//        }
//        
//        List<Object> compressed = new ArrayList<Object>(); 
//        
//        ReflectField[]  fields = doPackKeys(compressed,clazz);
//        
//        //rows
//        while(rows.hasMoreElements()){
//            Object row = rows.nextElement();
//            if(null == row){
//                continue;
//            }
//            for(ReflectField field : fields){
//                compressed.add(pack(field.getValue(row),field.genericType()));
//            }
//        }
//        return wrapPackObject(compressed);
//    }
    
    private static Object doPackArray(Object rows){
        List<Object> compressed = new ArrayList<Object>(); 
        
        int length = Array.getLength(rows);
        if(length > 0){
           Object row = null;
           
           int i = 0;
           for(;i<length;i++){
               row = Array.get(rows, i);
               if(null != row){
                   break;
               }
           }
           
           if(null != row){
               if(!ReflectType.isBeanType(row.getClass())){
                   return rows;
               }
               
               ReflectField[] fields = doPackKeys(compressed,row.getClass());
               
               //rows
               for(;i<length;i++){
                   row = Array.get(rows, i);
                   if(null == row){
                       continue;
                   }
                   for(ReflectField field : fields){
                       if(field.isArray() || field.isIterable()){
                           compressed.add(pack(field.getValue(row),field.getCompoenentType())); 
                       }else{
                           compressed.add(field.getValue(row));   
                       }
                   }
               }
           }
        }
        return wrapPackObject(compressed);
    }
    
    private static Object doPackArray(Class<?> clazz,Object rows){
        if(!ReflectType.isBeanType(clazz)){
            return rows;
        }
        
        List<Object> compressed = new ArrayList<Object>(); 
        
        ReflectField[]  fields = doPackKeys(compressed,clazz);
        
        //rows
        int length = Array.getLength(rows);
        for(int i=0;i<length;i++){
            Object row = Array.get(rows, i);
            if(null == row){
                continue;
            }
            for(ReflectField field : fields){
                if(field.isArray() || field.isIterable()){
                    compressed.add(pack(field.getValue(row),field.getCompoenentType())); 
                }else{
                    compressed.add(field.getValue(row));   
                }
            }
        }
        return wrapPackObject(compressed);
    }
    
    private static ReflectField[] doPackKeys(List<Object> compressed, Class<?> clazz){
        ReflectType type      = ReflectType.get(clazz);
        ReflectField[] fields = type.getPublicFields();
        
        //key length
        compressed.add(fields.length);
        
        //keys
        for(ReflectField field : fields){
            compressed.add(field.getName());
        }
        
        return fields;
    }
    
    private static Object wrapPackObject(Object pack){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("jsonh", pack);
        return map;
    }
}