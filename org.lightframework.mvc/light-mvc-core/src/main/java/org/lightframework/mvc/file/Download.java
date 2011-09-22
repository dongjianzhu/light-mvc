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
package org.lightframework.mvc.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.lightframework.mvc.HTTP.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO : document me
 * 
 * @author User
 * @since 1.x.x
 */
public class Download {
	private static final Logger log = LoggerFactory.getLogger(Download.class);
	protected static int buffeSize = 1024 ;

	public static void download(InputStream in, String fileName, String encoding,Request request) throws IOException {
		if (null == fileName) {
			throw new RuntimeException("下载文件名不能为空");
		}

		HttpServletResponse response = (HttpServletResponse)request.getResponse().getExternalResponse() ;
		
		response.reset();
		response.setCharacterEncoding(encoding);
		response.setContentType("application/x-download");
		
		response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

		try {
			OutputStream out = response.getOutputStream();
			byte[] b = new byte[buffeSize];
			int i = 0;
			while ((i = in.read(b)) > 0) {
				out.write(b, 0, i);
			}
			out.flush();
		} catch (Exception e) {
			log.error("文件下载出现异常", e);
			throw new RuntimeException("文件下载出现异常",e) ;
		} finally {
			if (null != in) {
				in.close();
				in = null;
			}
		}
	}
	
	public static void download(String filePath, String fileName, String encoding,Request request) throws IOException {

		if (null == filePath) {
			throw new RuntimeException("文件路径不能为空");
		}

		if (null == fileName || "".equals(fileName = fileName.trim())) {
			fileName = getClientName(filePath);
		}

		FileInputStream in = null;
		try {
			in = new FileInputStream(filePath);
			download(in, fileName, encoding,request) ;
		} catch (Exception e) {
			log.error("文件下载出现异常", e);
			throw new RuntimeException("文件下载出现异常",e) ;
		} finally {
			if (null != in) {
				in.close();
				in = null;
			}
		}
	}

	/** 客户端文件名 */
	public static String getClientName(String clientPath) {
		if (null != clientPath) {
			int index1 = clientPath.lastIndexOf("/");
			int index2 = clientPath.lastIndexOf("\\");
			if (index2 > index1) {
				index1 = index2;
			}
			return clientPath.substring(index1 + 1, clientPath.length());
		}
		return null;
	}
}
