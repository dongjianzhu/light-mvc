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
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * internal used json decoder
 * 
 * <p>
 * copied from google project <a href="http://code.google.com/p/lite/">lite</a> 
 * 
 * @author original author : jindwcn (jindwcn@gmail.com)
 * @author mofified by     : fenghm  (live.fenghm@gmail.com)
 * @since 1.0.0
 */
class JSONDecoder {
	private static final Logger log = LoggerFactory.getLogger(JSONDecoder.class);
	
	private static JSONDecoder decoder = new JSONDecoder(false);
	private boolean strict = false;

	public JSONDecoder(boolean strict) {
		this.strict = strict;
	}

	@SuppressWarnings("unchecked")
	public static <T> T decode(Reader value) throws IOException {
		StringBuilder buf = new StringBuilder();
		char[] cbuf = new char[32];
		int c;
		while ((c = value.read(cbuf)) >= 0) {
			buf.append(cbuf, 0, c);
		}
		return (T)decode(buf.toString());
	}

	@SuppressWarnings("unchecked")
	public static <T> T decode(String value) {
		return (T) decoder.decode(value, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T decode(String value, Class<T> type) {
		Object result = new JSONTokenizer(value, strict).parse();
		if (type != null && type != Object.class) {
			result = toValue(result, type);
		}
		return (T) result;
	}

	@SuppressWarnings("unchecked")
	protected <T> T toValue(Object value, Class<T> type) {
		try {
			boolean isPrimitive = type.isPrimitive();
			type = (Class<T>) JSONUtil.toWrapper(type);
			if (Number.class.isAssignableFrom(type)) {
				if (isPrimitive && value == null) {
					value = 0;
				}
				return (T) JSONUtil.toValue((Number) value, type);
			} else if (Boolean.class == type) {
				if (isPrimitive && value == null) {
					value = false;
				}
				return (T) value;
			} else if (String.class == type || type == null || value == null || Map.class.isAssignableFrom(type)
			        || Collection.class.isAssignableFrom(type)) {
				return (T) value;
			} else if (Character.class == type) {
				if (value instanceof String) {// OK
					value = ((String) value).charAt(0);
				} else if (value instanceof Number) {// invalid data
					value = (char) ((Number) value).intValue();
				} else if (isPrimitive && value == null) {// invalid data
					value = '\0';
				}
				return (T) value;
			} else if (type.isArray()) {
				List<Object> list = (List<Object>) value;
				Object result = Array.newInstance(type.getComponentType(), list.size());
				for (int i = 0, len = list.size(); i < len; i++) {
					Array.set(result, i, toValue(list.get(i), type.getComponentType()));
				}
				return (T) result;
			} else if (value instanceof String) {
				if (type == Class.class) {
					return (T) Class.forName((String) value);
				} else {
					return type.getConstructor(String.class).newInstance(value);
				}
			} else if (value instanceof Map) {
				Map map = (Map) value;
				String className = (String) map.get("class");
				Class clazz = className != null ? Class.forName(className) : type;
				Object result = clazz.newInstance();
				for (Object key : map.keySet()) {
					Class atype = JSONUtil.getPropertyType(clazz, key);
					JSONUtil.setValue(result, key, toValue(map.get(key), atype));
				}
				return (T) result;
			}
			log.warn("unknow json type : {}" + type);
			return null;
		} catch (Exception e) {
			log.warn("unknow json type", e);
			return null;
		}
	}
}
