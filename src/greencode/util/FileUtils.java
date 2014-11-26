package greencode.util;

import greencode.kernel.GreenContext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;

import javax.servlet.http.Part;

public final class FileUtils {
	private static final String pathWebContent = GreenContext.class.getClassLoader().getResource("").getPath()+"../../";
	
	private FileUtils() {}
	
	public final static String getExtension(String filePath) { return filePath.substring(filePath.lastIndexOf(".")+1); }
	
	public final static String getContentFile(URL file) throws IOException {
		InputStream fstream = null;		
		DataInputStream in = null;
		BufferedReader br = null;		
		try {
			fstream = file.openStream();			
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
						
			String strLine;
			StringBuilder str = new StringBuilder();
			while ((strLine = br.readLine()) != null)
				str.append(str.length() == 0 ? "" : "\n").append(strLine);
			
			return str.toString();
		} finally {
			if(fstream != null) fstream.close();
			if(in != null) in.close();
			if(br != null) br.close();
		}
	}
	
	public final static String getContentFile(URL file, Charset charset, FileRead read) throws IOException {
		InputStream fstream = null;		
		DataInputStream in = null;
		BufferedReader br = null;		
		try {
			fstream = file.openStream();			
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in, charset));
						
			String strLine;
			StringBuilder str = new StringBuilder();
			while ((strLine = br.readLine()) != null && (strLine = read.reading(strLine)) != null)
				str.append(strLine);
			
			return str.toString();
		} finally {
			if(fstream != null) fstream.close();
			if(in != null) in.close();
			if(br != null) br.close();
		}
	}
	
	public final static void copyTo(URL file, String folder) throws IOException {
		createFile(getContentFile(file), folder);
	}
	
	public final static void copyTo(Part part, String folder) throws IOException {
		OutputStream out = null;
		InputStream filecontent = null;
		try {
			out = new FileOutputStream(new File(folder));
			filecontent = part.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];
			while ((read = filecontent.read(bytes)) != -1)
				out.write(bytes, 0, read);
		} finally {
			if (out != null) out.close();
			if (filecontent != null) filecontent.close();
		}
	}
	
	public final static File createFile(String content, File file) throws IOException {
		Writer output = null;
		try {
			if(!file.exists())
				file.createNewFile();
			
			output = new BufferedWriter(new FileWriter(file));
			output.write(content);
			
			return file;
		} finally {
			if(output != null) output.close();
		}
	}
	
	public final static File createFile(String content, String folder) throws IOException {
		return createFile(content, new File(folder));
	}
	
	public static File getFileInWebContent(String end) {
		return new File(pathWebContent+end);
	}
	
	public interface FileRead {
		public String reading(String line);
	}
}