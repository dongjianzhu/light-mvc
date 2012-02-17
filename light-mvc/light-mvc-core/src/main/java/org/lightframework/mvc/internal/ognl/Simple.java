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
package org.lightframework.mvc.internal.ognl;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lightframework.mvc.exceptions.InvalidFormatException;


/**
 * simple object graph navigation lanauge parser
 * 
 * @author fenghm (fenghm@bingosoft.net)
 * 
 * @since 1.0.0
 */
public final class Simple {
	
	//TODO : REVIEW SoftReference ?
	private static final Map<String, SoftReference<OGNode[]>> cache = new ConcurrentHashMap<String, SoftReference<OGNode[]>>();

	public static OGNode[] parse(String expression){
		OGNode[] parsed = null;
		
		SoftReference<OGNode[]> ref = cache.get(expression);

		if(null != ref){
			parsed = ref.get();
		}else{
			ref = null;
		}
		
		if(null == parsed){
			char[] chars = expression.toCharArray();
			
			if(!isExpression(chars)){
				return null;
			}
			
			parsed = compile(chars);
			
			cache.put(expression, new SoftReference<OGNode[]>(parsed));
		}
		
		return parsed;
	}
	
	public static String deparse(OGNode[] parsed){
		StringBuilder buf = new StringBuilder();
		
		int len = parsed.length;
		
		for(int i=0;i<len;i++){
			OGNode item = parsed[i];
			
			buf.append(item.prop);
			
			if(item.index != null){
				buf.append('[').append(item.index).append(']');
			}else if(item.name != null){
				buf.append('[').append(item.name).append(']');
			}
			
			if(i<len - 1){
				buf.append('.');
			}
		}
		
		return buf.toString();
	}
	
	private static boolean isExpression(char[] chars){
		int len = chars.length;
		
		for(int i=0;i<len;i++){
			char c = chars[i];
			
			if(c == '.' && i > 0){
				return true;
			}else if(c == '['){
				while(++i < len){
					c = chars[i];
					
					if(c == ']'){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private static OGNode[] compile(char[] chars){
		List<Object> items = new ArrayList<Object>();
		
		StringBuilder buf = new StringBuilder();
		
		int len = chars.length;
		
		for(int i=0;i<len;i++){
			char c = chars[i];
			
			if(c == '.'){
				//check
				if(buf.length() == 0){
					if(items.isEmpty()){
						throw new InvalidFormatException("invalid char '" + c + "' at position " + i);	
					}else{
						continue;
					}
				}
				
				//property
				items.add(new OGNode(buf.toString()));
				
				//clear buf
				buf.delete(0, buf.length()); 
			}else if(c == '['){
				//check
				if(buf.length() == 0){
					throw new InvalidFormatException("invalid char '" + c + "' at position " + i);
				}
				
				//array item
				String property = buf.toString();
				String value    = null;
				
				//clear buf
				buf.delete(0, buf.length());
				
				//look ahead to found item index or name
				while( ++i < len){
					c = chars[i];
					
					if(c == ']'){
						value = buf.toString();
						
						//clear buf
						buf.delete(0, buf.length());
						break;
					}
					
					buf.append(c);
				}
				
				if(null == value || value.length() == 0){
					throw new InvalidFormatException("invalid char '" + c + "' at position " + i);
				}else{
					OGNode item = new OGNode(property);
					
					item.prop = property;
					try{
						item.index = Integer.parseInt(value); 
					}catch(Exception e){
						if(value.startsWith("'") && value.endsWith("'")){
							item.name = value.substring(1,value.length() - 1);
						}else{
							item.name = value;
						}
					}
					
					items.add(item);
				}
			}else{
				buf.append(c);
			}
		}
		
		//property
		if(buf.length() > 0){
			items.add(new OGNode(buf.toString()));
		}
		
		return items.toArray(new OGNode[]{});
	}
	
	public static final class OGNode {
		public String  prop;
		public Integer index;
		public String  name;
		
		private OGNode(String prop){
			this.prop = prop;
		}
	}
}