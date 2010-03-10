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
 * utitlity class, internal used only
 * 
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public final class Utils {
	
	/**
	 * replace all 'from' to 'to' in string 'string'
	 * 
	 * <p/>
	 * 
	 * this code was copied from springframework
	 */
	public static String replace(String string, String from, String to) {
		if(null == string || null == from || null == to){
			return string;
		}
		if(string.length() == 0 || from.length() == 0){
			return string;
		}

		StringBuilder sb = new StringBuilder();
		int pos = 0; // our position in the old string
		int index = string.indexOf(from);
		// the index of an occurrence we've found, or -1
		int patLen = from.length();
		while (index >= 0) {
			sb.append(string.substring(pos, index));
			sb.append(to);
			pos = index + patLen;
			index = string.indexOf(from, pos);
		}
		sb.append(string.substring(pos));
		// remember to append any characters to the right of a match
		return sb.toString();
	}
 
	/**
	 * @return string join with ',' 
	 */
	public static String arrayToString(Object[] array){
		if(null != array){
			if(array.length == 1){
				return array[0].toString();
			}
			StringBuilder builder = new StringBuilder();
			for(int i =0;i<array.length;i++){
				if(i > 0){
					builder.append(",");
				}
				Object value = array[i];
				builder.append(null == value ? "" : value.toString());
			}
			return builder.toString();
		}
		return null;
	}
	
	public static String[] stringToArray(String string){
		return null == string ? null : string.split(",");
	}
	
	public final static class Ref<E> {
		public E value;
		public Ref(E value){
			this.value = value;
		}
	}
}
