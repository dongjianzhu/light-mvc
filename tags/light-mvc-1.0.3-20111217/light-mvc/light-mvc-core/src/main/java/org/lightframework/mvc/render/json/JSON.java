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

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.lightframework.mvc.render.IRenderContext;
import org.lightframework.mvc.render.DataRender;

/**
 * json encoder and decoder
 * 
 * @author fenghm (live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public class JSON {

	private static final JSONRender  render  = new JSONRender();
	private static final JSONContext context = new JSONContext();
	private static final JSONDecoder decoder = new JSONDecoder(false);

	public static String encode(Object value) {
		return render.encode(value, context);
	}

	public static String encode(Object value, IRenderContext context) {
		return render.encode(value, context);
	}

	public static JSONObject decode(Reader reader) {
		try {
			StringBuilder buf = new StringBuilder();
			char[] cbuf = new char[32];
			int c;
			while ((c = reader.read(cbuf)) >= 0) {
				buf.append(cbuf, 0, c);
			}
			return decode(buf.toString());
		} catch (IOException e) {
			throw new JSONException("error reading json from reader : " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static JSONObject decode(String source) {
		Object result = decoder.decode(source, Map.class);
		if(result instanceof Map){
			return new JSONObject((Map)result);
		}else{
			return new JSONObject((Object[])result);
		}
	}

	private static final class JSONRender extends DataRender {

	}
}
