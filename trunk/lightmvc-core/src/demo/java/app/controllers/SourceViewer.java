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
package app.controllers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.lightframework.mvc.Render;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.utils.ClassUtils;

/**
 * TODO : document me
 *
 * @author rain.yangdy(rain.yangdy@gmail.com)
 */
public class SourceViewer {

	public void viewSource(String sourcePath) throws IOException{
		if(sourcePath != null && !"".equals(sourcePath.trim())){
			if(sourcePath.endsWith(".java") || sourcePath.endsWith(".class")){
				sourcePath = sourcePath.substring(sourcePath.lastIndexOf('.'));
			}
			sourcePath = sourcePath.replace('.', '/');
			String javaSource = "No source found with path ["+sourcePath+"]";
			if(!sourcePath.endsWith(".java")){
				sourcePath += ".java"; 
			}
			InputStream is = ClassUtils.getContextClassLoader().getResourceAsStream(sourcePath);
			if(is != null){
				javaSource = IOUtils.toString(is,"UTF-8");
				IOUtils.closeQuietly(is);
			}

			Request.current().setAttribute("javaSourceCode", javaSource);
		}
		Render.forward("/modules/syntax_high_lighter/java.jsp");

	}

}
