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

import java.text.MessageFormat;

import org.lightframework.mvc.Utils.Messages;

/**
 * main logger of mvc framework
 *
 * @author light.wind(lightworld.me@gmail.com)
 * @since 1.0
 */
public final class Logger {
	
	private static final String NULL_MESSAGE_STRING = "<NULL MESSAGE>";
	
	private static String TRACE = Messages.getString("Logging.Level.Trace");
	private static String DEBUG = Messages.getString("Logging.Level.Debug");
	private static String INFO  = Messages.getString("Logging.Level.Info");
	private static String WARN  = Messages.getString("Logging.Level.Warn");
	private static String ERROR = Messages.getString("Logging.Level.Error");
	private static String FATAL = Messages.getString("Logging.Level.Fatal");
	
	public static boolean isTraceEnabled(){
		return true;
	}
	
	public static boolean isDebugEnabled(){
		return true;
	}
	
	public static boolean isInfoEnabled(){
		return true;
	}
	
	public static boolean isWarnEnabled(){
		return true;
	}
	
	public static void trace(Object message,Object... args){
		if(isTraceEnabled()){
			if(null == message){
				message = NULL_MESSAGE_STRING;
			}
			System.out.println(TRACE + " : " + format(message.toString(),args));
		}
	}

	public static void debug(Object message,Object... args){
		if(isDebugEnabled()){
			if(null == message){
				message = NULL_MESSAGE_STRING;
			}
			System.out.println(DEBUG + " : " + format(message.toString(),args));
		}
	}
	
	public static void info(Object message,Object... args){
		if(isInfoEnabled()){
			if(null == message){
				message = NULL_MESSAGE_STRING;
			}
			System.out.println(INFO + " : " + format(message.toString(),args));
		}
	}
	
	public static void warn(Object message,Object... args){
		if(isWarnEnabled()){
			if(null == message){
				message = NULL_MESSAGE_STRING;
			}
			System.out.println(WARN + " : " + format(message.toString(),args));
		}
	}
	
	public static void warn(Throwable cause, Object message, Object... args){
		if(isWarnEnabled()){
			if(null == message){
				message = NULL_MESSAGE_STRING;
			}
			System.out.println(WARN + " : " + format(message.toString(),args));
			cause.printStackTrace();
		}
	}
	
	public static void error(Object message,Object... args){
		if(null == message){
			message = NULL_MESSAGE_STRING;
		}
		System.out.println(ERROR + " : " + format(message.toString(),args));
	}
	
	public static void error(Throwable cause, Object message, Object... args){
		error(message,args);
		cause.printStackTrace();
	}
	
	public static void fatal(Object message,Object... args){
		if(null == message){
			message = NULL_MESSAGE_STRING;
		}
		System.out.println(FATAL + " : " + format(message.toString(),args));
	}
	
	public static void fatal(Throwable cause, Object message, Object... args){
		fatal(message,args);
		cause.printStackTrace();
	}
	
	private static String format(String message,Object... args){
		if(message.charAt(0) == '@'){
			if(args.length > 0){
				return Messages.getString(message,args);	
			}else{
				return Messages.getString(message);
			}
		}else{
			if(args.length > 0){
				return MessageFormat.format(message,args);
			}else{
				return message;
			}
		}
	}
}
