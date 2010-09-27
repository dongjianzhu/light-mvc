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
package org.lightframework.mvc.render;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * abstract class of {@link IRenderWriter}
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public abstract class RenderWriter implements IRenderWriter {
	
	public static final SimpleDateFormat RFC_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public static final char[] HEX_CHARS = new char[]{
        '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'
    }; 
    public static final String HEX_PREFIX = "0x";

	public void writeBoolean(Boolean bool, StringBuilder out) {
		out.append(String.valueOf(bool));
    }

	public void writeByte(Byte b, StringBuilder out) {
		out.append(HEX_PREFIX);
        out.append(HEX_CHARS[(b >>> 4) & 0x0F]);
        out.append(HEX_CHARS[b & 0x0F]);
    }
	
	public void writeBytes(Byte[] bytes, StringBuilder out) {
		out.append(HEX_PREFIX);
		for(Byte b : bytes){
	        out.append(HEX_CHARS[(b >>> 4) & 0x0F]);
	        out.append(HEX_CHARS[b & 0x0F]);
		}
    }
	
	public void writeName(String name, StringBuilder out) {
	    writeString(name,out);
    }

	public void writeCharacter(Character c, StringBuilder out) {
		writeString(String.valueOf(c),out);
    }

	public void writeNumber(Number number, StringBuilder out) {
		out.append(String.valueOf(number));
    }

	public void writeDate(Date date, StringBuilder out) {
		StringBuffer temp = new StringBuffer(RFC_DATE_FORMAT.format(date)); //rfc3339
		temp.insert(temp.length() - 2, ':');	
		writeString(temp.toString(),out) ;	    
    }
}
