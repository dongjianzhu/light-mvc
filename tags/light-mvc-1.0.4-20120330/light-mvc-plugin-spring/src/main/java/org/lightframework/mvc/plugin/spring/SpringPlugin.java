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
package org.lightframework.mvc.plugin.spring;

import org.lightframework.mvc.Action;
import org.lightframework.mvc.Plugin;
import org.lightframework.mvc.HTTP.Request;
import org.lightframework.mvc.HTTP.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * a mvc {@link Plugin} to resolve spring beans as controller.
 * 
 * @author fenghm (fenghm@bingosoft.net)
 * 
 * @since 1.0.0
 */
public class SpringPlugin extends Plugin implements BeanFactoryAware{
	private static final Logger log = LoggerFactory.getLogger(SpringPlugin.class);
	
	private BeanFactory beanFactory;

	@Override
    public boolean resolve(Request request, Response response, Action action) throws Throwable {
		String controllerName  = action.getControllerName();
		
        Object controller = null;
        
        if(controllerName.endsWith("Controller") || controllerName.endsWith("Service")){
        	controller = getControllerBean(controllerName);
        }else{
        	controller = getControllerBean(controllerName + "Controller");
        	
        	if(null == controller){
        		controller = getControllerBean(controllerName + "Service");
        	}
        	
        	if(null == controller){
        		controller = getControllerBean(controllerName);
        	}
        }
        
        if(null != controller){
        	Action.Setter.setControllerObject(action, controller);    	
        }
		
        //return false because this plugin just resolve controller,action method not resolved.
	    return false;
    }
	
	protected Object getControllerBean(String beanName){
		try {
			//bean转化 ，如 user_manage
			beanName = resolveBeanName(beanName) ;
			
	        Object controller = beanFactory.getBean(beanName);
	        
	        log.debug("[mvc:spring] -> resolved spring bean '{}' as controller",beanName);
	        
	        return controller;
        } catch (NoSuchBeanDefinitionException e) {
        	log.debug("[mvc:spring] -> no such bean '{}'",beanName);
        	return null;
        }
	}
	
	private String resolveBeanName(String beanName){
		String[] bn = beanName.split("_") ;
		if( bn.length<=1 )
			return beanName ;
		String _beanName = null ;
		for(String str:bn){
			if( _beanName == null )
				_beanName = str ;
			else
				_beanName += upperFirstChar(str) ;
		}
		return _beanName ;
	}
	
	private static String upperFirstChar(String string){
			return string.substring(0,1).toUpperCase() + string.substring(1);	
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
    }
}