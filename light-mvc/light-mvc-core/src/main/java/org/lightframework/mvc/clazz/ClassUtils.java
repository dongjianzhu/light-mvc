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
package org.lightframework.mvc.clazz;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.lightframework.mvc.binding.Argument;

/**
 * class utility,used by mvc framework only
 *
 * @author fenghm(live.fenghm@gmail.com)
 * 
 * @since 1.0.0
 */
public final class ClassUtils {
    public static final String FILE_URL_PREFIX    = "file:";
    public static final String URL_PROTOCOL_FILE  = "file";
    public static final String URL_PROTOCOL_JAR   = "jar";
    public static final String URL_PROTOCOL_ZIP   = "zip";
    public static final String URL_PROTOCOL_WSJAR = "wsjar";
    public static final String JAR_URL_SEPARATOR  = "!/";	
    
	private static final String[] EMPTY_PARAMETER_NAMES = new String[]{};     
	private static final Argument[] EMPTY_METHOD_PARAMS = new Argument[]{}; 
	private static final Map<Class<?>, Class<?>> types  = new HashMap<Class<?>, Class<?>>();
	private static final Map<Class<?>, Object> defaults = new HashMap<Class<?>, Object>();
	
	static {
		//Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE
		types.put(Boolean.TYPE, Boolean.class);
		types.put(Character.TYPE, Character.class);
		types.put(Byte.TYPE, Byte.class);
		types.put(Short.TYPE, Short.class);
		types.put(Integer.TYPE, Integer.class);
		types.put(Long.TYPE, Long.class);
		types.put(Float.TYPE, Float.class);
		types.put(Double.TYPE, Double.class);
		
		//default value of primitive type
		/*
			byte 	0
			short 	0
			int 	0
			long 	0L
			float 	0.0f
			double 	0.0d
			char 	'\u0000'
			boolean 	false
		 */
		defaults.put(Boolean.TYPE,   false);
		defaults.put(Character.TYPE, '\u0000');
		defaults.put(Byte.TYPE,      (byte)0);
		defaults.put(Short.TYPE,     0);
		defaults.put(Integer.TYPE,   0);
		defaults.put(Long.TYPE,      0L);
		defaults.put(Float.TYPE,     0.0f);
		defaults.put(Double.TYPE,    0.0d);
	}
	
	public static boolean isJdkClass(Class<?> clazz){
		String name = clazz.getName();
		if(name.startsWith("java.") || 
				name.startsWith("javax.") || 
				name.startsWith("org.w3c.") || 
				name.startsWith("org.xml.") || 
				name.startsWith("org.omg.") || clazz.isPrimitive()){
			return true;
		}else{
			return false;
		}
	}
	
	public static Object getDefaultValue(Class<?> primitiveType){
		return defaults.get(primitiveType);
	}
	
	public static Class<?> getWrapperType(Class<?> primitiveType){
		Class<?> type = types.get(primitiveType);
		return null == type ? primitiveType : type;
	}
	
	public static Object newInstance(Class<?> clazz) throws Exception{
		return clazz.newInstance();
	}
	
	public static List<Field> getDeclaredFields(Class<?> clazz,Class<?> stopClass){
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			if (stopClass != null && c == stopClass) {
				break;
			}else{
				for(Field field : c.getDeclaredFields()){
					//exclude final and static field
					if(!( Modifier.isFinal(field.getModifiers())
							||Modifier.isStatic(field.getModifiers()))){
						fields.add(field) ;
					}
				}
				
				//fields.addAll(Arrays.asList(c.getDeclaredFields()));
			}
		}
		return fields;
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
	
	public static ClassLoader getContextClassLoader(){
		return Thread.currentThread().getContextClassLoader();
	}
	
	public static String extractPackageName(String className){
    	String pkgName   = "";
    	
    	int lastDotIndex = className.lastIndexOf(".");
    	
    	if(lastDotIndex > 0){
    		pkgName   = className.substring(0,lastDotIndex);
    	}
    	
    	return pkgName;
	}
	
	/**
	 * find class by name
	 * @param name class name with packages
	 * @return {@link Class} object if found,else return null
	 */
	public static Class<?> forName(String name) {
		Class<?> clazz = null;
		try {
	        clazz = getContextClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
        	//do nothing
        }
        if(null == clazz){
    		try {
    	        clazz = ClassUtils.class.getClassLoader().loadClass(name);
            } catch (ClassNotFoundException e) {
            	//do nothing
            }        	
        }
		return clazz;
	}
	
	public static Argument[] getMethodParameters(Method method) throws IOException{
		Class<?>[] types    = method.getParameterTypes();
		Type[] genericTypes = method.getGenericParameterTypes();
		
		if(types.length > 0){
			Class<?> clazz   = method.getDeclaringClass();
			
			ClassReader cr = null;
			try{
				cr = new ClassReader(clazz);
				
				Argument[] args  = new Argument[types.length];
				String[]   names = cr.getParameterNames(method);
				
				Annotation[][] annotations = method.getParameterAnnotations();
				for(int i=0;i<args.length;i++){
					Class<?> type    = types[i];
					Type genericType = genericTypes[i];					
					
					Argument arg = new Argument();
					arg.setName(names[i]);
					arg.setType(type);
					arg.setGenericType(genericType);
					arg.setConfigs(annotations[i]);
					args[i] = arg;
				}
				
				return args;
			}finally{
				if(null != cr){
					cr.close();
					cr = null;
				}
			}
		}else{
			return EMPTY_METHOD_PARAMS;
		}
	}
	
	public static String[] getMethodParameterNames(Method method) throws IOException{
		Class<?>[] types = method.getParameterTypes();
		
		if(types.length > 0){
			Class<?> clazz = method.getDeclaringClass();
			ClassReader cr = null;
			try{
				cr = new ClassReader(clazz);
				
				return cr.getParameterNames(method);
			}finally{
				if(null != cr){
					cr.close();
					cr = null;
				}
			}
		}else{
			return EMPTY_PARAMETER_NAMES;
		}
	}
	
	public static boolean isStatic(Method method){
		return Modifier.isStatic(method.getModifiers());
	}
	
    public static Method findMethodIgnoreCase(Class<?> clazz,String name) throws IOException{
        while (!clazz.getName().equals("java.lang.Object")) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equalsIgnoreCase(name) && Modifier.isPublic(m.getModifiers())) {
                	return m;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
    
    public static String findClassNameIgnoreCase(String name) throws IOException {
    	List<String> classes = findAllClassNames(ClassUtils.extractPackageName(name));
    	
    	for(String className : classes){
    		if(className.equalsIgnoreCase(name)){
    			return className;
    		}
    	}
    	return null;
    }
    
    public static List<String> findAllClassNames(String pkgName) throws IOException{
    	return findAllClassNames(ClassUtils.getContextClassLoader(),pkgName);
    }

	public static List<String> findAllClassNames(ClassLoader loader, String pkgName) throws IOException{
		List<String> list = new ArrayList<String>();
		
		if(null == pkgName){
			pkgName = "";
		}
		
		pkgName = pkgName + ".";
		String pkgPath = pkgName.replace('.', '/');
	
		Enumeration<URL> urls = loader.getResources(pkgPath);
		
		Set<String> paths = new HashSet<String>();
		while(urls.hasMoreElements()){
			URL url = urls.nextElement();
			
			if(!paths.contains(url.getPath())){
				if(isJarURL(url)){
					URLConnection connection = url.openConnection();
			        JarFile jarFile = null;
			        if(connection instanceof JarURLConnection){
			            jarFile = ((JarURLConnection)connection).getJarFile();
			        }else{
			            // No JarURLConnection -> need to resort to URL file parsing.
			            // We'll assume URLs of the format "jar:path!/entry", with the protocol
			            // being arbitrary as long as following the entry format.
			            // We'll also handle paths with and without leading "file:" prefix.
			            String urlFile = url.getFile();
			            int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
			            String jarFileUrl = urlFile.substring(0, separatorIndex);
			            if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
			                jarFileUrl = jarFileUrl.substring(FILE_URL_PREFIX.length());
			            }
			            jarFile = new JarFile(jarFileUrl);
			        }	
			        
					Enumeration<JarEntry> e = jarFile.entries();

					while (e.hasMoreElements()) {
						String name = e.nextElement().getName();

						if (!name.startsWith(pkgPath)){
							continue;
						}

						if (!name.endsWith(".class")){
							continue;
						}

						if (name.contains("$")){
							continue;
						}

						// Strip off .class and convert the slashes back to periods.

						String className = name.substring(0, name.length() - ".class".length()).replace("/", ".");

						list.add(className);
					}			        
				}else{
					InputStream is = null;

					try {
						is = new BufferedInputStream(url.openStream());
					} catch (FileNotFoundException ex) {
						// This can happen for certain application servers (JBoss 4.0.5 for
						// example), that
						// export part of the exploded WAR for deployment, but leave part
						// (WEB-INF/classes)
						// unexploded.
						break;
					}

					Reader reader = new InputStreamReader(is);
					LineNumberReader lineReader = new LineNumberReader(reader);

					try {
						while (true) {
							String line = lineReader.readLine();

							if (line == null)
								break;

							if (line.contains("$"))
								continue;

							if (line.endsWith(".class")) {
								String className     = line.substring(0, line.length() - ".class".length());
								String fullClassName = pkgName + className;
								
								list.add(fullClassName);

								continue;
							}

							// Either a file or a hidden directory (such as .svn)

							if (line.contains(".")){
								continue;
							}else{
								//directory?
								List<String> childs = findAllClassNames(loader, pkgName + line);
								for(String child : childs){
									list.add(child);
								}
							}
						}
						lineReader.close();
						lineReader = null;
					} finally {
						if (lineReader != null) {
							try {
								lineReader.close();
							} catch (IOException ex) {
								// Ignore.
							}
						}
					}					
				}
			}
		}
		paths.clear();
		
		return list;
	}
	
	static boolean isJarURL(URL url){
        String protocol = url.getProtocol();
        return (URL_PROTOCOL_JAR.equals(protocol) ||
                URL_PROTOCOL_ZIP.equals(protocol) ||
                URL_PROTOCOL_WSJAR.equals(protocol));
	}	
	
	/**
	 * copy from apache axis2 kernel source code <p> 
	 * 
	 * <p/>
	 * This is the class file reader for obtaining the parameter names
	 * for declared methods in a class.  The class must have debugging
	 * attributes for us to obtain this information. <p>
	 * 
	 * <p/>
	 * This does not work for inherited methods.  To obtain parameter
	 * names for inherited methods, you must use a paramReader for the
	 * class that originally declared the method. <p>
	 * 
	 * <p/>
	 * don't get tricky, it's the bare minimum.  Instances of this class
	 * are not threadsafe -- don't share them. <p>
	 */
	@SuppressWarnings("unchecked")
	public static class ClassReader extends ByteArrayInputStream {
	    // constants values that appear in java class files,
	    // from jvm spec 2nd ed, section 4.4, pp 103
	    private static final int CONSTANT_Class = 7;
	    private static final int CONSTANT_Fieldref = 9;
	    private static final int CONSTANT_Methodref = 10;
	    private static final int CONSTANT_InterfaceMethodref = 11;
	    private static final int CONSTANT_String = 8;
	    private static final int CONSTANT_Integer = 3;
	    private static final int CONSTANT_Float = 4;
	    private static final int CONSTANT_Long = 5;
	    private static final int CONSTANT_Double = 6;
	    private static final int CONSTANT_NameAndType = 12;
	    private static final int CONSTANT_Utf8 = 1;	
		
	    private static Map attrMethods;

	    static{
	    	attrMethods = findAttributeReaders(ClassReader.class);
	    }
	    
	    /**
	     * the constant pool.  constant pool indices in the class file
	     * directly index into this array.  The value stored in this array
	     * is the position in the class file where that constant begins.
	     */
	    private int[] cpoolIndex;
	    private Object[] cpool;
	    
	    private String methodName;
	    private Map methods = new HashMap();
	    private Class[] paramTypes;
	    
	    protected ClassReader(Class c) throws IOException {
	        this(getBytes(c));
	    }

	    protected ClassReader(byte[] b) throws IOException {
	        super(b);

	        // check the magic number
	        if (readInt() != 0xCAFEBABE) {
	            // not a class file!
	            throw new IOException("Error looking for paramter names in bytecode: input does not appear to be a valid class file");
	        }

	        readShort(); // minor version
	        readShort(); // major version

	        readCpool(); // slurp in the constant pool

	        readShort(); // access flags
	        readShort(); // this class name
	        readShort(); // super class name

	        int count = readShort(); // ifaces count
	        for (int i = 0; i < count; i++) {
	            readShort(); // interface index
	        }

	        count = readShort(); // fields count
	        for (int i = 0; i < count; i++) {
	            readShort(); // access flags
	            readShort(); // name index
	            readShort(); // descriptor index
	            skipAttributes(); // field attributes
	        }

	        count = readShort(); // methods count
	        for (int i = 0; i < count; i++) {
	            readShort(); // access flags
	            int m = readShort(); // name index
	            String name = resolveUtf8(m);
	            int d = readShort(); // descriptor index
	            this.methodName = name + resolveUtf8(d);
	            readAttributes(); // method attributes
	        }
	    }
	    
	    /**
	     * Loads the bytecode for a given class, by using the class's defining
	     * classloader and assuming that for a class named P.C, the bytecodes are
	     * in a resource named /P/C.class.
	     *
	     * @param c the class of interest
	     * @return Returns a byte array containing the bytecode
	     * @throws IOException
	     */
	    protected static byte[] getBytes(Class c) throws IOException {
	    	
	    	String name = c.getName() ;
	    	
	        InputStream fin = c.getResourceAsStream('/' + name.replace('.', '/') + ".class");
	        if (fin == null) {
	        	//在类被增强以后，名称已经发生变化
	        	name = c.getSuperclass().getName() ;
	        	fin = c.getResourceAsStream('/' + name.replace('.', '/') + ".class");
	        	if(fin == null ){
	        		throw new IOException("Unable to load bytecode for class " + name
	                   );
	        	}
	        }
	        try {
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            byte[] buf = new byte[1024];
	            int actual;
	            do {
	                actual = fin.read(buf);
	                if (actual > 0) {
	                    out.write(buf, 0, actual);
	                }
	            } while (actual > 0);
	            return out.toByteArray();
	        } finally {
	            fin.close();
	        }
	    }

	    static String classDescriptorToName(String desc) {
	        return desc.replace('/', '.');
	    }

	    protected static Map findAttributeReaders(Class c) {
	        HashMap map = new HashMap();
	        Method[] methods = c.getMethods();

	        for (int i = 0; i < methods.length; i++) {
	            String name = methods[i].getName();
	            if (name.startsWith("read") && methods[i].getReturnType() == void.class) {
	                map.put(name.substring(4), methods[i]);
	            }
	        }

	        return map;
	    }

	    protected static String getSignature(Member method, Class[] paramTypes) {
	        // compute the method descriptor

	        StringBuffer b = new StringBuffer((method instanceof Method) ? method.getName() : "<init>");
	        b.append('(');

	        for (int i = 0; i < paramTypes.length; i++) {
	            addDescriptor(b, paramTypes[i]);
	        }

	        b.append(')');
	        if (method instanceof Method) {
	            addDescriptor(b, ((Method) method).getReturnType());
	        } else if (method instanceof Constructor) {
	            addDescriptor(b, void.class);
	        }

	        return b.toString();
	    }

	    private static void addDescriptor(StringBuffer b, Class c) {
	        if (c.isPrimitive()) {
	            if (c == void.class)
	                b.append('V');
	            else if (c == int.class)
	                b.append('I');
	            else if (c == boolean.class)
	                b.append('Z');
	            else if (c == byte.class)
	                b.append('B');
	            else if (c == short.class)
	                b.append('S');
	            else if (c == long.class)
	                b.append('J');
	            else if (c == char.class)
	                b.append('C');
	            else if (c == float.class)
	                b.append('F');
	            else if (c == double.class) b.append('D');
	        } else if (c.isArray()) {
	            b.append('[');
	            addDescriptor(b, c.getComponentType());
	        } else {
	            b.append('L').append(c.getName().replace('.', '/')).append(';');
	        }
	    }


	    /**
	     * @return Returns the next unsigned 16 bit value.
	     */
	    protected final int readShort() {
	        return (read() << 8) | read();
	    }

	    /**
	     * @return Returns the next signed 32 bit value.
	     */
	    protected final int readInt() {
	        return (read() << 24) | (read() << 16) | (read() << 8) | read();
	    }

	    /**
	     * Skips n bytes in the input stream.
	     */
	    protected void skipFully(int n) throws IOException {
	        while (n > 0) {
	            int c = (int) skip(n);
	            if (c <= 0)
	                throw new EOFException("Error looking for paramter names in bytecode: unexpected end of file");
	            n -= c;
	        }
	    }

	    protected final Member resolveMethod(int index) throws IOException, ClassNotFoundException, NoSuchMethodException {
	        int oldPos = pos;
	        try {
	            Member m = (Member) cpool[index];
	            if (m == null) {
	                pos = cpoolIndex[index];
	                Class owner = resolveClass(readShort());
	                NameAndType nt = resolveNameAndType(readShort());
	                String signature = nt.name + nt.type;
	                if (nt.name.equals("<init>")) {
	                    Constructor[] ctors = owner.getConstructors();
	                    for (int i = 0; i < ctors.length; i++) {
	                        String sig = getSignature(ctors[i], ctors[i].getParameterTypes());
	                        if (sig.equals(signature)) {
	                            cpool[index] = m = ctors[i];
	                            return m;
	                        }
	                    }
	                } else {
	                    Method[] methods = owner.getDeclaredMethods();
	                    for (int i = 0; i < methods.length; i++) {
	                        String sig = getSignature(methods[i], methods[i].getParameterTypes());
	                        if (sig.equals(signature)) {
	                            cpool[index] = m = methods[i];
	                            return m;
	                        }
	                    }
	                }
	                throw new NoSuchMethodException(signature);
	            }
	            return m;
	        } finally {
	            pos = oldPos;
	        }

	    }

	    protected final Field resolveField(int i) throws IOException, ClassNotFoundException, NoSuchFieldException {
	        int oldPos = pos;
	        try {
	            Field f = (Field) cpool[i];
	            if (f == null) {
	                pos = cpoolIndex[i];
	                Class owner = resolveClass(readShort());
	                NameAndType nt = resolveNameAndType(readShort());
	                cpool[i] = f = owner.getDeclaredField(nt.name);
	            }
	            return f;
	        } finally {
	            pos = oldPos;
	        }
	    }

	    private static class NameAndType {
	        String name;
	        String type;

	        public NameAndType(String name, String type) {
	            this.name = name;
	            this.type = type;
	        }
	    }

	    protected final NameAndType resolveNameAndType(int i) throws IOException {
	        int oldPos = pos;
	        try {
	            NameAndType nt = (NameAndType) cpool[i];
	            if (nt == null) {
	                pos = cpoolIndex[i];
	                String name = resolveUtf8(readShort());
	                String type = resolveUtf8(readShort());
	                cpool[i] = nt = new NameAndType(name, type);
	            }
	            return nt;
	        } finally {
	            pos = oldPos;
	        }
	    }


	    protected final Class resolveClass(int i) throws IOException, ClassNotFoundException {
	        int oldPos = pos;
	        try {
	            Class c = (Class) cpool[i];
	            if (c == null) {
	                pos = cpoolIndex[i];
	                String name = resolveUtf8(readShort());
	                cpool[i] = c = Class.forName(classDescriptorToName(name));
	            }
	            return c;
	        } finally {
	            pos = oldPos;
	        }
	    }

	    protected final String resolveUtf8(int i) throws IOException {
	        int oldPos = pos;
	        try {
	            String s = (String) cpool[i];
	            if (s == null) {
	                pos = cpoolIndex[i];
	                int len = readShort();
	                skipFully(len);
	                cpool[i] = s = new String(buf, pos - len, len, "utf-8");
	            }
	            return s;
	        } finally {
	            pos = oldPos;
	        }
	    }

	    protected final void readCpool() throws IOException {
	        int count = readShort(); // cpool count
	        cpoolIndex = new int[count];
	        cpool = new Object[count];
	        for (int i = 1; i < count; i++) {
	            int c = read();
	            cpoolIndex[i] = super.pos;
	            switch (c) // constant pool tag
	            {
	                case CONSTANT_Fieldref:
	                case CONSTANT_Methodref:
	                case CONSTANT_InterfaceMethodref:
	                case CONSTANT_NameAndType:

	                    readShort(); // class index or (12) name index
	                    // fall through

	                case CONSTANT_Class:
	                case CONSTANT_String:

	                    readShort(); // string index or class index
	                    break;

	                case CONSTANT_Long:
	                case CONSTANT_Double:

	                    readInt(); // hi-value

	                    // see jvm spec section 4.4.5 - double and long cpool
	                    // entries occupy two "slots" in the cpool table.
	                    i++;
	                    // fall through

	                case CONSTANT_Integer:
	                case CONSTANT_Float:

	                    readInt(); // value
	                    break;

	                case CONSTANT_Utf8:

	                    int len = readShort();
	                    skipFully(len);
	                    break;

	                default:
	                    // corrupt class file
	                    throw new IllegalStateException("Error looking for paramter names in bytecode: unexpected bytes in file");
	            }
	        }
	    }

	    protected final void skipAttributes() throws IOException {
	        int count = readShort();
	        for (int i = 0; i < count; i++) {
	            readShort(); // name index
	            skipFully(readInt());
	        }
	    }

	    /**
	     * Reads an attributes array.  The elements of a class file that
	     * can contain attributes are: fields, methods, the class itself,
	     * and some other types of attributes.
	     */
	    protected final void readAttributes() throws IOException {
	        int count = readShort();
	        for (int i = 0; i < count; i++) {
	            int nameIndex = readShort(); // name index
	            int attrLen = readInt();
	            int curPos = pos;

	            String attrName = resolveUtf8(nameIndex);

	            Method m = (Method) attrMethods.get(attrName);

	            if (m != null) {
	                try {
	                    m.invoke(this, new Object[]{});
	                } catch (IllegalAccessException e) {
	                    pos = curPos;
	                    skipFully(attrLen);
	                } catch (InvocationTargetException e) {
	                    try {
	                        throw e.getTargetException();
	                    } catch (Error ex) {
	                        throw ex;
	                    } catch (RuntimeException ex) {
	                        throw ex;
	                    } catch (IOException ex) {
	                        throw ex;
	                    } catch (Throwable ex) {
	                        pos = curPos;
	                        skipFully(attrLen);
	                    }
	                }
	            } else {
	                // don't care what attribute this is
	                skipFully(attrLen);
	            }
	        }
	    }

	    public void readCode() throws IOException {
	        readShort(); // max stack
	        int maxLocals = readShort(); // max locals

	        MethodInfo info = new MethodInfo(maxLocals);
	        if (methods != null && methodName != null) {
	            methods.put(methodName, info);
	        }

	        skipFully(readInt()); // code
	        skipFully(8 * readShort()); // exception table
	        // read the code attributes (recursive).  This is where
	        // we will find the LocalVariableTable attribute.
	        readAttributes();
	    }

	    /**
	     * Returns the names of the declared parameters for the given constructor.
	     * If we cannot determine the names, return null.  The returned array will
	     * have one name per parameter.  The length of the array will be the same
	     * as the length of the Class[] array returned by Constructor.getParameterTypes().
	     *
	     * @param ctor
	     * @return Returns String[] array of names, one per parameter, or null
	     */
	    public String[] getParameterNames(Constructor ctor) {
	        paramTypes = ctor.getParameterTypes();
	        return getParameterNames(ctor, paramTypes);
	    }

	    /**
	     * Returns the names of the declared parameters for the given method.
	     * If we cannot determine the names, return null.  The returned array will
	     * have one name per parameter.  The length of the array will be the same
	     * as the length of the Class[] array returned by Method.getParameterTypes().
	     *
	     * @param method
	     * @return Returns String[] array of names, one per parameter, or null
	     */
	    public String[] getParameterNames(Method method) {
	        paramTypes = method.getParameterTypes();
	        return getParameterNames(method, paramTypes);
	    }

	    protected String[] getParameterNames(Member member, Class [] paramTypes) {
	        // look up the names for this method
	        MethodInfo info = (MethodInfo) methods.get(getSignature(member, paramTypes));

	        // we know all the local variable names, but we only need to return
	        // the names of the parameters.

	        if (info != null) {
	            String[] paramNames = new String[paramTypes.length];
	            int j = Modifier.isStatic(member.getModifiers()) ? 0 : 1;

	            boolean found = false;  // did we find any non-null names
	            for (int i = 0; i < paramNames.length; i++) {
	                if (info.names[j] != null) {
	                    found = true;
	                    paramNames[i] = info.names[j];
	                }
	                j++;
	                if (paramTypes[i] == double.class || paramTypes[i] == long.class) {
	                    // skip a slot for 64bit params
	                    j++;
	                }
	            }

	            if (found) {
	                return paramNames;
	            } else {
	                return null;
	            }
	        } else {
	            return null;
	        }
	    }

	    private static class MethodInfo {
	        String[] names;

	        public MethodInfo(int maxLocals) {
	            names = new String[maxLocals];
	        }
	    }

	    private MethodInfo getMethodInfo() {
	        MethodInfo info = null;
	        if (methods != null && methodName != null) {
	            info = (MethodInfo) methods.get(methodName);
	        }
	        return info;
	    }

	    /**
	     * This is invoked when a LocalVariableTable attribute is encountered.
	     * @throws IOException
	     */
	    public void readLocalVariableTable() throws IOException {
	        int len = readShort(); // table length
	        MethodInfo info = getMethodInfo();
	        for (int j = 0; j < len; j++) {
	            readShort(); // start pc
	            readShort(); // length
	            int nameIndex = readShort(); // name_index
	            readShort(); // descriptor_index
	            int index = readShort(); // local index
	            if (info != null) {
	                info.names[index] = resolveUtf8(nameIndex);
	            }
	        }
	    }
	}	
}
