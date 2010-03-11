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

import app.models.User;



/**
 * home controller of application
 *
 * @author light.wind(lightworld.me@gmail.com)
 */
public class Home {

	public void index(){
		System.out.println("hello");
	}

	public void hello(String message){
		System.out.println("hello:" + message);
	}
	
	public void hello1(int[] i){
		System.out.println("hello:" + i);
		
	}
	public void helloworld(User u){
		System.out.println(u.getName());
		System.out.println(u.getAddress());
	}
	
}
