package greencode.util;

import greencode.kernel.Console;
import greencode.kernel.LogMessage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class PackageUtils {
	
	private PackageUtils(){}
	
	/* Based LINK: http://snippets.dzone.com/posts/show/4831  */
	public static File getPackageFolder(String packageName) throws IOException
	{			
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();	        
        Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));
        	        
        if(resources.hasMoreElements())
        	return new File(resources.nextElement().getFile());
        
       return null;
	}
	
	public static List<File> getFilesByPackgeName(String packageName) throws IOException
	{
		return getFilesByPackgeName(packageName, null);
	}
	
	private static List<File> getFilesByPackgeName(String packageName, File _file) throws IOException
	{
		List<File> list = new ArrayList<File>();
		
		File packageFile;
		if(_file != null)
			packageFile = _file;
		else
			packageFile = getPackageFolder(packageName);
		
		if(packageFile == null)
			return list;
		
		File[] files = packageFile.listFiles();
		
		for (File file : files) {
			if(file.isDirectory())
			{
				if(!file.getName().contains("."))
				{
					list.addAll(getFilesByPackgeName(packageName+'.'+file.getName(), packageFile));
				}
			}else
			{
				list.add(file);
			}
		}
		
		return list;
	}
	
	public static List<Class<?>> getClasses(String packageName) throws IOException
	{
		return getClasses(packageName, null, false);
	}
	
	public static List<Class<?>> getClasses(String packageName, boolean searchInside) throws IOException
	{
		return getClasses(packageName, null, searchInside);
	}
	
	private static List<Class<?>> getClasses(String packageName, File _file, boolean searchInside) throws IOException
	{
		List<Class<?>> list = new ArrayList<Class<?>>();
		
		File packageFile;
		if(_file != null)
			packageFile = _file;
		else
			packageFile = getPackageFolder(packageName);
		
		if(packageFile == null)
			return list;
		
		File[] files = packageFile.listFiles();
		
		for (File file : files) {
			String fileName = file.getName();
			if(file.isDirectory())
			{
				if(searchInside && !fileName.contains("."))
				{
					String _packageName;
					if(packageName.equals("/"))
					{
						_packageName = fileName;
					}else
					{
						_packageName = packageName+'.'+fileName;
					}
					
					list.addAll(getClasses(_packageName, null, searchInside));
				}
			}else
			{
				if(fileName.endsWith(".class") && !fileName.contains("$"))
				{
					try {
						list.add(Class.forName(packageName+'.'+fileName.substring(0, fileName.length()-6)));
					} catch (ClassNotFoundException e) {
						Console.warning(LogMessage.getMessage("green-0023", fileName));
					}
				}
			}
		}
		
		return list;
	}
}
