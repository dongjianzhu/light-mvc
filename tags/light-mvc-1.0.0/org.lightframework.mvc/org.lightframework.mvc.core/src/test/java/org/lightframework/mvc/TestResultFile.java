/*
 * Copyright 2011 the original author or authors.
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.lightframework.mvc.Result.DownloadResult;
import org.lightframework.mvc.file.Download;
import org.lightframework.mvc.test.MvcTestCase;

import com.mockrunner.mock.web.MockHttpServletResponse;


/**
 * TODO : document me
 *
 * @author User
 * @since 1.x.x
 */
public class TestResultFile  extends MvcTestCase {
	
	public void testDownloadStream() throws Exception{
		
		String str = "文件下载Demo" ;
		String fileName = "demo.txt" ;
		String encoding = null ;
		InputStream in = new ByteArrayInputStream(str.getBytes()) ;
		new DownloadResult(in, fileName,encoding).render(request, response) ;
		
		assertEquals("application/x-download", ((MockHttpServletResponse)request.getResponse().getExternalResponse()).getContentType() );
	}
	
	public void testDownloadStream1() throws Exception{
		
		String str = "文件下载Demo" ;
		InputStream in = new ByteArrayInputStream(str.getBytes()) ;
		try{
			new DownloadResult(in, null).render(request, response) ;
		}catch(Exception e){
			assertEquals(true , e.getLocalizedMessage().contains("下载文件名不能为空")) ;
		}
		
	}
	
	
	
	public void testDownloadStream2() throws Exception{
		
		String fileName = "demo.txt" ;
		String encoding = null ;
		InputStream in  ;
		
		try{
			in =  null ;
			Download.download(in, fileName, encoding, request) ;
		}catch(Exception e){
			assertEquals(true , e.getLocalizedMessage().contains("文件下载出现异常")) ;
		}
	}
	
	public void testDownloadFile() throws Exception{
		String filePath = "E:/test.txt" ;
		String fileName = null ;
		String encoding = null ;
		
		new DownloadResult(filePath, fileName,encoding).render(request, response) ;
		
		assertEquals("application/x-download", ((MockHttpServletResponse)request.getResponse().getExternalResponse()).getContentType() );
	}
	
	public void testDownloadFile1() throws Exception{
		String filePath = "E:/test.txt" ;
		String fileName = null ;
		
		try{
			filePath = "" ;
			new DownloadResult(filePath, fileName).render(request, response) ;
		}catch(Exception e){
			assertEquals(true , e.getLocalizedMessage().contains("文件下载出现异常")) ;
		}
	}
	
	
	public void testDownloadFile2() throws Exception{
		String filePath = "E:/test.txt" ;
		String fileName = null ;
		String encoding = null ;
		
		try{
			filePath = null ;
			Download.download(filePath, fileName, encoding, request)  ;
		}catch(Exception e){
			assertEquals(true , e.getLocalizedMessage().contains("文件路径不能为空")) ;//
		}
	}
}
