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
package org.lightframework.mvc.internal.json;

import java.io.Reader;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import org.lightframework.mvc.internal.convert.Converter;


/**
 * json encoder and decoder
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class JSON {
    
    private static int ENCODE_MAXIMIZED = 0;
    private static int ENCODE_MINIMIZED = JSONSettings.IGNORE_NULL | JSONSettings.IGNORE_BLANK | JSONSettings.KEY_NON_QUOTE;
    
    private static final JSONEncoder encoderMinimized  = new JSONEncoder(new JSONSettings(ENCODE_MINIMIZED));
    private static final JSONEncoder encoderMaximized  = new JSONEncoder(new JSONSettings(ENCODE_MAXIMIZED));
    private static final JSONDecoder decoderPermissive = new JSONDecoder();
	
    public static Map<String,Object> map(String json){
        if(null == json || json.trim().equals("")){
            return new HashMap<String, Object>();
        }
        
        if(!json.startsWith("{")){
            json = "{" + json;
        }
        
        if(!json.endsWith("}")){
            json = json + "}";
        }
        
        return decode(json).map();
    }
    
    public static Object[] array(String json){
        if(null == json || json.trim().equals("")){
            return new Object[]{};
        }
        
        if(!json.startsWith("[")){
            json = "[" + json;
        }
        
        if(!json.endsWith("]")){
            json = json + "]";
        }
        
        return decode(json).array();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T[] array(Class<T> componentType, String json){
        T[] a = (T[])Array.newInstance(componentType, 0);
        
        if(null == json || json.trim().equals("")){
            return a;
        }
        
        if(!json.startsWith("[")){
            json = "[" + json;
        }
        
        if(!json.endsWith("]")){
            json = json + "]";
        }
        
        return (T[])Converter.convert(a.getClass(), decode(json).arraylist());
    }
    
	public static String encode(Object value){
	    return encoderMinimized.encode(value);
	}

	public static String encode(Object value,boolean minimized){
	    if(minimized){
	        return encoderMinimized.encode(value);
	    }
	    return encoderMaximized.encode(value);
	}
	
	public static JSONObject decode(Reader reader) {
	    return new JSONObject(decoderPermissive.decode(reader));
	}

	public static JSONObject decode(String string) {
		return new JSONObject(decoderPermissive.decode(string));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T decode(Class<? extends T> type,String string){
	    return (T)Converter.convert(type, decoderPermissive.decode(string));
	}
	
    @SuppressWarnings("unchecked")
    public static <T> T decode(Class<? extends T> type,Reader reader){
        return (T)Converter.convert(type, decoderPermissive.decode(reader));
    }
}