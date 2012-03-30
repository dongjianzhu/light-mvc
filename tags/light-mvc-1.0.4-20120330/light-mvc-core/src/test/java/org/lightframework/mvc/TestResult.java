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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.lightframework.mvc.Result.Forward;
import org.lightframework.mvc.Result.Redirect;
import org.lightframework.mvc.test.MvcTestCase;
import org.lightframework.mvc.utils.FileUtils;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;

/**
 * Test Case of {@link Result}
 * @author fenghm (live.fenghm@gmail.com)
 * @since 1.0.0
 */
public class TestResult extends MvcTestCase {
	
	@SuppressWarnings("static-access")
    public void testRedirect() throws Throwable{
		module.setPackagee(packagee);
		Result result = request("/");
		
		assertNotNull( result.getRequest() ) ;
		assertNotNull( result.getServletRequest() ) ;
		
		assertNotNull( result.getResponse() ) ;
		assertNotNull( result.getServletResponse() ) ;
		
		assertNotNull( result.getSession() ) ;
		assertNotNull( result.getServletSession() ) ;
		
		Redirect redirect = new Redirect("~/test");
		redirect.render(request, response);
		assertEquals(request.getContext() + "/test", response.getRedirectUrl());
		
		redirect = new Redirect("/test");
		redirect.render(request, response);
		assertEquals("/test", response.getRedirectUrl());	
		
		redirect = new Redirect("http://www.sohu.com");
		redirect.render(request, response);
		assertEquals("http://www.sohu.com", response.getRedirectUrl());	
		
		redirect = new Redirect("test");
		redirect.render(request, response);
		assertEquals("/test", response.getRedirectUrl());	
		
		redirect = new Redirect("./test");
		redirect.render(request, response);
		assertEquals("/test", response.getRedirectUrl());
		
		Result.redirect("~/mvc/test") ;
		assertEquals( request.getContext() + "/mvc/test", response.getRedirectUrl());
	}
	
	public void testForward() throws Throwable{
		module.setPackagee(packagee);
		request("/");
		
		Forward foward = new Forward("/test");
		foward.render(request, response);
		assertEquals("/test", response.getForwardPath());	
		
		foward = new Forward("test");
		foward.render(request, response);
		assertEquals("/test", response.getForwardPath());	
		
		foward = new Forward("./test");
		foward.render(request, response);
		assertEquals("/test", response.getForwardPath());
		
		Result.forward("/light_forward") ;
		assertEquals("/light_forward", response.getForwardPath());
	}	
	
	/**
	 * 测试获取request、response、session的公用接口
	 * @throws Exception
	 */
	public void testContainerContext() throws Exception{
		//Object externalRequest = request.getExternalRequest() ;
		assertEquals( request.getExternalRequest().getClass(), MockHttpServletRequest.class);
		assertEquals( response.getExternalResponse().getClass(), MockHttpServletResponse.class);
		assertEquals( session.getExternalSession().getClass(), MockHttpSession.class);
	}
	
	public void testContent()  throws Throwable{
		request("/");
		Result.content("text") ;
		
		MockHttpServletResponse resp = (MockHttpServletResponse)Result.getServletResponse() ;
		assertNotNull(resp.getOutputStreamContent() , "text") ;
	}

	public void testScript()  throws Throwable{
		request("/");
		Result.script("alert(123);") ;
		MockHttpServletResponse resp = (MockHttpServletResponse)Result.getServletResponse() ;
		assertNotNull(resp.getOutputStreamContent()) ;
	}

    public void testFile() throws Throwable{
    	request("/");
    	
    	FileUtils.createDir(new File("C:/mvcTest")) ;  
    	FileUtils.createFile(new File("C:/mvcTest/test.txt")) ;   

		try{
			Result.file("C:/mvcTest/test.txt") ;
		}finally{
			FileUtils.deleteDir(new File("C:/mvcTest")) ;
		}
		MockHttpServletResponse resp = (MockHttpServletResponse)Result.getServletResponse() ;
		assertNotNull(resp.getOutputStream()) ;
		
		String str = "文件下载Demo" ;
		InputStream in = new ByteArrayInputStream(str.getBytes()) ;
		Result.file(in, "test.txt") ;
		resp = (MockHttpServletResponse)Result.getServletResponse() ;
		assertNotNull(resp.getOutputStream()) ;
		
	}
}
