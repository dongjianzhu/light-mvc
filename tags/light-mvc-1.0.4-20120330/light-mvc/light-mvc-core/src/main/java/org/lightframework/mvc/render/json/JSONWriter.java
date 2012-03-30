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

import org.lightframework.mvc.render.IRenderWriter;
import org.lightframework.mvc.render.RenderWriter;

/**
 * json format writer
 *
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class JSONWriter extends RenderWriter implements IRenderWriter {
	public static final String NULL_STRING   = "null";
	public static final String EMPTY_STRING  = "\"\"";
	public static final char   OPEN_ARRAY    = '[';
	public static final char   CLOSE_ARRAY   = ']';
	public static final char   OPEN_OBJECT   = '{';
	public static final char   CLOSE_OBJECT  = '}';
	public static final char   CLOSE_NAME    = ':';
	public static final char   SINGLE_QUOTE  = '"';
	public static final char   COMMA_CHAR    = ',';
	
	public void openArray(StringBuilder out) {
		out.append(OPEN_ARRAY);
    }
	public void closeArray(StringBuilder out) {
		out.append(CLOSE_ARRAY);
    }
	
	public void openObject(StringBuilder out) {
		out.append(OPEN_OBJECT);
    }
	public void closeObject(StringBuilder out) {
		out.append(CLOSE_OBJECT);
    }
	
	public void openName(StringBuilder out) {
		//do nothing
	}
	public void closeName(StringBuilder out) {
		out.append(CLOSE_NAME);
    }
	
	public void openValue(String name, StringBuilder out) {
		//do nothing
    }
	public void closeValue(String name, StringBuilder out) {
		//do nothing
    }

	public void writeNull(StringBuilder out) {
		out.append(NULL_STRING);
    }
	
	public void writeArrayValueSeperator(StringBuilder out) {
		out.append(COMMA_CHAR);
    }
	
	public void writePropertyValueSeperator(StringBuilder out) {
		out.append(COMMA_CHAR);
    }
	public void writeString(String string, StringBuilder out) {
        if (string == null) {
        	writeNull(out);
        }else if(string.length() == 0){
        	out.append(EMPTY_STRING);
        }else{
            char c   = 0;
            int  len = string.length();

            out.append(SINGLE_QUOTE);
            for (int i = 0; i < len; i++) {
                c = string.charAt(i);
                switch (c) {
                case '\\':
                    out.append("\\\\");
                    break;
                case '"':
                	out.append("\\\"");
                    break;
                case '\b':
                	out.append("\\b");
                    break;
                case '\t':
                	out.append("\\t");
                    break;
                case '\n':
                	out.append("\\n");
                    break;
                case '\f':
                	out.append("\\f");
                    break;
                case '\r':
                	out.append("\\r");
                    break;
                default:
                	out.append(c);
                }
            }
            out.append(SINGLE_QUOTE);
        }
    }
}
