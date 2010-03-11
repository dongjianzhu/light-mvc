/**
 * 
 */
package org.lightframework.mvc.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * utility class for bean property injection
 * 
 * @author hm.fong
 */
public final class BeanUtils {
	
    public static void setPropety(Object bean, String property, Object value) 
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException{
        
        BeanProperties beanPropeties = BeanProperties.forClass(bean.getClass());
        
        Class<?> type = null;
        Method method = null;
        if(null == method){
            method = beanPropeties.findSetterMethod(property,value);
            
            if(null != method){
                type = method.getParameterTypes()[0];
            }
        }
        
        if(null != type && null != method){
            if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
                method.setAccessible(true);
            }
            
            value = convert(type,value);
            
            method.invoke(bean, new Object[] { value });
        }
    }
    
    public static Object getProperty(Object bean,String property) 
    	throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException{
    	
    	BeanProperties beanProperties = BeanProperties.forClass(bean.getClass());

        Method method = beanProperties.findGetterMethod(property);
    	if(null != method){
    		return method.invoke(beanProperties,(Object[])null);
    	}
        return null;
    }
    
    public static Object convert(Class<?> type,Object value){
    	if(null == value){
    		return null;
    	}

    	if (value instanceof String && type != String.class) {
            String string = (String) value;
            if (type == Integer.TYPE || type == Integer.class) {
                value = Integer.parseInt(string);
            } else if (type == Short.TYPE || type == Short.class) {
                value = Short.parseShort(string);
            } else if (type == Long.TYPE || type == Long.class) {
                value = Long.parseLong(string);
            } else if (type == Boolean.TYPE || type == Boolean.class) {
                value = Boolean.parseBoolean(string);
            } else if (type == Float.TYPE || type == Float.class) {
                value = Float.parseFloat(string);
            } else if (type == Double.TYPE || type == Double.class) {
                value = Double.parseDouble(string);
            } else if (type == Character.TYPE || type == Character.class && string.length() == 0) {
                value = string.charAt(0);
            } else if (type == Byte.TYPE || type == Byte.class) {
                value = Byte.parseByte(string);
            } else if (type.isAssignableFrom(Date.class)){
            	value = parseDate(string);
            	if(null == value){
            		throw new RuntimeException("invalid date string '" + string + "'");
            	}
            }
        }else{
        	if(type == BigDecimal.class && value.getClass() != BigDecimal.class){
        		value = new BigDecimal(value.toString());
        	}else if(type.isArray()){
				try {
            		String arrayClassName = getArrayClassName(type);
					Class<?> arrayClass   = createClassByName(arrayClassName);
					value = convertArrayType((Object[])value,arrayClass);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e.getMessage(),e);
				}
        	}else if(!type.equals(value.getClass())){
                if (type == Integer.TYPE || type == Integer.class) {
                    value = Integer.parseInt(value.toString());
                } else if (type == Short.TYPE || type == Short.class) {
                    value = Short.parseShort(value.toString());
                } else if (type == Long.TYPE || type == Long.class) {
                    value = Long.parseLong(value.toString());
                } else if (type == Boolean.TYPE || type == Boolean.class) {
                    value = Boolean.parseBoolean(value.toString());
                } else if (type == Float.TYPE || type == Float.class) {
                    value = Float.parseFloat(value.toString());
                } else if (type == Double.TYPE || type == Double.class) {
                    value = Double.parseDouble(value.toString());
                }
        	}
        }
        return value;
    }

    public static Method findMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            return findDeclaredMethod(clazz, methodName, paramTypes);
        }
    }

    public static Method findDeclaredMethod(Class<?> clazz, String methodName, Class<?>[] paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null) {
                return findDeclaredMethod(clazz.getSuperclass(), methodName, paramTypes);
            }
            return null;
        }
    }

    public static Method findDeclaredMethodWithMinimalParameters(Class<?> clazz, String methodName) throws IllegalArgumentException {
        Method targetMethod = doFindMethodWithMinimalParameters(clazz.getDeclaredMethods(), methodName);
        if (targetMethod == null && clazz.getSuperclass() != null) {
            return findDeclaredMethodWithMinimalParameters(clazz.getSuperclass(), methodName);
        }
        return targetMethod;
    }

    private static Method doFindMethodWithMinimalParameters(Method[] methods, String methodName) throws IllegalArgumentException {
        Method targetMethod = null;
        int numMethodsFoundWithCurrentMinimumArgs = 0;
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(methodName)) {
                int numParams = methods[i].getParameterTypes().length;
                if (targetMethod == null || numParams < targetMethod.getParameterTypes().length) {
                    targetMethod = methods[i];
                    numMethodsFoundWithCurrentMinimumArgs = 1;
                } else {
                    if (targetMethod.getParameterTypes().length == numParams) {
                        // Additional candidate with same length.
                        numMethodsFoundWithCurrentMinimumArgs++;
                    }
                }
            }
        }
        if (numMethodsFoundWithCurrentMinimumArgs > 1) {
            throw new IllegalArgumentException("Cannot resolve method '" + methodName
                    + "' to a unique method. Attempted to resolve to overloaded method with "
                    + "the least number of parameters, but there were " + numMethodsFoundWithCurrentMinimumArgs + " candidates.");
        }
        return targetMethod;
    }
    
    public static final class BeanProperties {
        
        private final BeanInfo beanInfo;
        
        private final Map<String, PropertyDescriptor> propertiyDescriptors;
        
        private final Map<String, Method> setterMethods;
        
        private final Map<String, Method> getterMethods;
        
        public static BeanProperties forClass(Class<?> clazz) throws IntrospectionException{
            return new BeanProperties(clazz);
        }
        
        private BeanProperties(Class<?> clazz) throws IntrospectionException{
            
            this.beanInfo = Introspector.getBeanInfo(clazz);
            this.propertiyDescriptors = new HashMap<String, PropertyDescriptor>();
            this.setterMethods = new HashMap<String, Method>();
            this.getterMethods = new HashMap<String, Method>();
            
            // from spring source:
            // Immediately remove class from Introspector cache, to allow for proper
            // garbage collection on class loader shutdown - we cache it here anyway,
            // in a GC-friendly manner. In contrast to CachedIntrospectionResults,
            // Introspector does not use WeakReferences as values of its WeakHashMap!
            Class<?> classToFlush = clazz;
            do {
                Introspector.flushFromCaches(classToFlush);
                classToFlush = classToFlush.getSuperclass();
            }
            while (classToFlush != null);      
            
            // This call is slow so we do it once.
            PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
            for (int i = 0; i < pds.length; i++) {
                PropertyDescriptor pd = pds[i];
                this.propertiyDescriptors.put(pd.getName(), pd);
            }
        
            MethodDescriptor[] mds = this.beanInfo.getMethodDescriptors();
            for(int i=0;i<mds.length;i++){
                MethodDescriptor md = mds[i];
                Method method = md.getMethod();
                String name   = md.getName();
                
                if(name.startsWith("set") && name.length() > 3 && method.getParameterTypes() != null && method.getParameterTypes().length == 1){
                    String property = name.substring(3);
                    
                    property = property.substring(0,1).toLowerCase() + property.substring(1);
                    
                    if(setterMethods.containsKey(property)){
                    	property = property + "-" + i;
                    	setterMethods.put(property, method);
                    }else{
                    	setterMethods.put(property, method);   	
                    }
                }else if(method.getParameterTypes() == null || method.getParameterTypes().length == 0){
                	String property = null;
                	if(name.startsWith("get") && name.length() > 3){
                		property = name.substring(3);
                	}else if(name.startsWith("is") && name.length() > 2){
                		property = name.substring(2);
                	}
                	if(null != property){
                		property = property.substring(0,1).toLowerCase() + property.substring(1);
                		getterMethods.put(property, method);
                	}
                }
            }
        }
        
        public Method findSetterMethod(String property){
            return setterMethods.get(property);
        }
        
        public Method findSetterMethod(String property,Object value){
        	if(null == value) return findSetterMethod(property);
        	
            Set<String> keys = setterMethods.keySet();
            for(String key : keys){
            	if(key.equals(property) || key.startsWith(property + "-")){
            		Method method = findSetterMethod(key);
            		if(value.getClass().isAssignableFrom(method.getParameterTypes()[0])){
            			return method;
            		}
            	}
            }
            return findSetterMethod(property);
        }
        
        public Method findGetterMethod(String property){
        	return getterMethods.get(property);
        }
    }
    
    public static Date parseDate(String dateString){
    	Date d = toDate(dateString, "yyyy-MM-dd HH:mm:ss");
    	if(null == d){
    		d = toDate(dateString, "yyyy-MM-dd");
    	}
    	return d;
    }
    
    private static Object convertArrayType(Object[] array,Class<?> converToType){
    	Object object = Array.newInstance(converToType, array.length);
    	Object[] newArray = (Object[])object;
    	for(int i=0;i<newArray.length;i++){
    		newArray[i] = array[i];
    	}
    	return object;
    }
    
    private static Class<?> createClassByName(String className) throws ClassNotFoundException{
		return ClassUtils.forName(className);
    }
    
    private static String getArrayClassName(Class<?> type){
    	//[Ljava.lang.String;
    	String name = type.getName();
    	return name.substring(2,name.length()-1);
    }
    
    private static Date toDate(String date, String format) {
        if (date == null) {
            return null;
        }
        
        Date d = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
        	ParsePosition pos = new ParsePosition(0);
            formater.setLenient(false);
            d = formater.parse(date, pos);
            if(null != d && pos.getIndex() != date.length()){
                d = null;
            }
        } catch (Exception e) {
            d = null;
        }
        return d;
    }    
}
