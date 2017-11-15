package com.jrender.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.jrender.kernel.Console;

public final class PackageUtils {
	
	private PackageUtils(){}
	
	/* Based LINK: http://snippets.dzone.com/posts/show/4831  */
	public static File getPackageFolder(String packageName) throws IOException {		
		return getPackageFolder(packageName, Thread.currentThread().getContextClassLoader());
	}
	
	private static File getPackageFolder(String packageName, ClassLoader classLoader) throws IOException {		
		Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));
					
		if(resources.hasMoreElements())
			return new File(resources.nextElement().getFile());
		
		return null;
	}
	
	public static List<File> getFilesByPackgeName(String packageName) throws IOException
	{ return getFilesByPackgeName(packageName, null, Thread.currentThread().getContextClassLoader(), new ArrayList<File>()); }
	
	private static List<File> getFilesByPackgeName(String packageName, File _file, ClassLoader classLoader, List<File> list) throws IOException {		
		_file = _file != null ? _file : getPackageFolder(packageName, classLoader);
		
		if(_file != null) {
			File[] files = _file.listFiles();
			
			for (File file : files) {
				if(file.isDirectory()) {
					if(!file.getName().contains("."))
						getFilesByPackgeName(packageName+'.'+file.getName(), _file, classLoader, list);
				}else
					list.add(file);
			}
		}
		
		return list;
	}
	
	public static List<Class<?>> getClasses(String packageName) throws IOException { return getClasses(packageName, false); }
	
	public static List<Class<?>> getClasses(String packageName, boolean searchInside) throws IOException
	{ return getClasses(packageName, searchInside, null, Thread.currentThread().getContextClassLoader(), new ArrayList<Class<?>>()); }
	
	private static List<Class<?>> getClasses(String packageName, boolean searchInside, File _file, ClassLoader classLoader, List<Class<?>> list) throws IOException {
		if((_file = _file != null ? _file : getPackageFolder(packageName, classLoader)) != null) {
			File[] files = _file.listFiles();
			
			for (File file : files) {
				final String fileName = file.getName();
				if(file.isDirectory())
				{
					if(searchInside && !fileName.contains("."))
						getClasses(packageName.equals("/") ? fileName : packageName+'.'+fileName, searchInside, null, classLoader, list);
				}else
				{
					if(fileName.endsWith(".class") && !fileName.contains("$"))
					{
						try {
							list.add(Class.forName(packageName+'.'+fileName.substring(0, fileName.length()-6)));
						} catch (ClassNotFoundException e) {
							Console.warning(LogMessage.getMessage("0023", fileName));
						}
					}
				}
			}
		}
		
		return list;
	}
}
