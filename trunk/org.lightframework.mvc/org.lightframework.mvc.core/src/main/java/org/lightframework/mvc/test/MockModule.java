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
package org.lightframework.mvc.test;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.lightframework.mvc.Module;
import org.lightframework.mvc.Plugin;

/**
 * mock object of {@link Module} for testing.
 * 
 * @author fenghm (live.fenghm@gmail.com)
 *
 * @since 1.0.0
 */
public class MockModule extends Module {
	
	protected boolean      findWebRoot   = true; 
	protected String       webRootDir    = null;
	protected List<String> addClasses    = new ArrayList<String>();
	protected List<String> removeClasses = new ArrayList<String>();
	protected List<String> webResources  = new ArrayList<String>();
	protected ClassLoader  classLoader   = null;

	public void setPackagee(String packagee){
		this.packagee = packagee;
	}

	public void addClassName(String name){
		addClasses.add(name);
		removeClasses.remove(name);
	}
	
	public void removeClassName(String name){
		removeClasses.add(name);
		addClasses.remove(name);
	}
	
	public String getWebRootDir() {
		if(null == webRootDir && findWebRoot){
			webRootDir = findWebRootDir();
		}
    	return webRootDir;
    }

	public void setWebRootDir(String webRootDir) {
    	this.webRootDir = webRootDir;
    }
	
	public void setFindWebRoot(boolean isFindWebRoot){
		this.findWebRoot = isFindWebRoot;
	}
	
	public void addWebResource(String resource){
		webResources.add(resource);
	}
	
	public void removeWebResource(String resource){
		webResources.remove(resource);
	}
	
	public String getViewResourcePath(String view){
		return getViewPath() + view;
	}
	
	public void addViewResource(String view){
		addWebResource(getViewResourcePath(view));
	}
	
	public void removeViewResource(String view){
		removeWebResource(getViewResourcePath(view));
	}	
	
	public void clearWebResources(){
		webResources.clear();
	}
	
	public void setClassLoader(ClassLoader classLoader){
		this.classLoader = classLoader;
	}
	
	public void addPlugin(Plugin plugin){
		plugins.add(plugin);
	}
	
	@Override
    public ClassLoader getClassLoader() {
		if(null != classLoader){
			return classLoader;
		}
	    return super.getClassLoader();
    }

	@Override
    protected Set<String> getModuleClassNames() {
	    Set<String> classes = super.getModuleClassNames();
	    classes.addAll(addClasses);
	    classes.removeAll(removeClasses);
	    return classes;
    }

	@Override
    protected Collection<String> findWebResources(String path) {
		String webRootPath = getWebRootDir();
		if(null == webRootPath){
			return webResources;
		}
		
		String resourcesPath = webRootPath + path;
		File resourcesFolder = new File(resourcesPath);
		List<String> resources = new ArrayList<String>();
		
		if(resourcesFolder.exists()){
			File[] files = resourcesFolder.listFiles();
			for(int i=0;i<files.length;i++){
				File file = files[i];
				if(!file.isDirectory()){
					resources.add(path + (path.endsWith("/") ? "" : "/") + file.getName());
				}
			}
		}
		
		resources.addAll(webResources);

		return resources;
    }
	
	protected String findWebRootDir(){
		String current = System.getProperty("user.dir");
		
		//search folder has a 'WEB-INF' sub-folder in current directory
		File root = new File(current);
		
		return findWebRootDir(root);
	}
	
	protected String findWebRootDir(File root){
		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() && !pathname.getName().startsWith(".");
			}
		};
		
		File[] childs = root.listFiles(filter);
		for(File child : childs){
			if(child.getName().equals("WEB-INF")){
				return root.getAbsolutePath();
			}else{
				String webDir = findWebRootDir(child);
				if(null != webDir){
					return webDir;
				}
			}
		}		
		return null;
	}
}
