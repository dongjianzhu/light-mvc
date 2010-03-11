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
package app.models;

import java.util.Date;

/**
 * TODO : document me
 *
 * @author light.wind(lightworld.me@gmail.com)
 */
public class User {
	private String name;
	private String [] address;
	
	private Date birthDay;
	/**
     * @return the birthDay
     */
    public Date getBirthDay() {
    	return birthDay;
    }
	/**
     * @param birthDay the birthDay to set
     */
    public void setBirthDay(Date birthDay) {
    	this.birthDay = birthDay;
    }
	/**
     * @return the name
     */
    public String getName() {
    	return name;
    }
	/**
     * @param name the name to set
     */
    public void setName(String name) {
    	this.name = name;
    }
	/**
     * @return the address
     */
    public String[] getAddress() {
    	return address;
    }
	/**
     * @param address the address to set
     */
    public void setAddress(String[] address) {
    	this.address = address;
    }

}
