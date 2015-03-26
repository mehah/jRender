package greencode.kernel;

import greencode.util.FileUtils;
import greencode.util.FileUtils.FileRead;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public final class CoreFileJS {
	private final StringBuilder core;
	private final String greencodePath;
	
	CoreFileJS(String greencodePath) {
		this.core = new StringBuilder();
		this.greencodePath = greencodePath;
	}
	
	CoreFileJS(StringBuilder jsCore, String greencodePath) {
		this.core = jsCore;
		this.greencodePath = greencodePath;
	}
	
	public StringBuilder append(String content) {
		return core.append(content);
	}
	
	public StringBuilder append(URL file) throws IOException {
		return core.append(FileUtils.getContentFile(file));
	}
	
	public StringBuilder append(URL file, Charset charset, FileRead read) throws IOException {
		return core.append(FileUtils.getContentFile(file, charset, read));
	}
	
	public void save() throws IOException {
		FileUtils.createFile(core.toString(), greencodePath+"/core.js");
	}
}
