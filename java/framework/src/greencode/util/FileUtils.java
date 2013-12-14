package greencode.util;

import greencode.kernel.Console;
import greencode.kernel.GreenContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;

public final class FileUtils {
	private static String pathWebContent;
	
	private FileUtils() {}
	
	public final static String getExtension(String filePath)
	{
		return filePath.substring(filePath.lastIndexOf(".")+1);
	}
	
	public final static StringBuilder getContentFile(URL file) throws IOException
	{
		InputStream fstream = file.openStream();
		
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
		String strLine;
		StringBuilder str = new StringBuilder();
		
		while ((strLine = br.readLine()) != null)
			str.append(str.length() == 0 ? "" : "\n").append(strLine);
		
		fstream.close();
		in.close();
		br.close();
		
		return str;
	}
	
	public final static void copyTo(URL file, String folder) throws IOException
	{
		StringBuilder str = getContentFile(file);
		
		Writer output = null;
		try {
			File newFile = new File(folder);
			if(!newFile.exists())
				newFile.createNewFile();
			
			output = new BufferedWriter(new FileWriter(newFile));
		} catch (IOException e1) {
			Console.error(e1);
		}
				
		output.write(str.toString());
		output.close();
	}
	
	public final static void createFile(String content, File file) throws IOException
	{
		createFile(content, file.getAbsolutePath());
	}
	
	public final static void createFile(String content, String folder) throws IOException
	{
		Writer output = null;
		try {
			File newFile = new File(folder);
			if(!newFile.exists())
				newFile.createNewFile();
			
			output = new BufferedWriter(new FileWriter(newFile));
			output.write(content);
		} catch (IOException e1) {
			Console.error(e1);
		} finally {
			if(output != null)
				output.close();
		}
	}
	
	public static File getFileInWebContent(String end)
	{
		if(pathWebContent == null)
			pathWebContent = GreenContext.class.getClassLoader().getResource("").getPath()+"../../";
			
		return new File(pathWebContent+end);
	}
}