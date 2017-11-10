package greencode.kernel;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

import greencode.util.FileUtils;
import greencode.util.FileUtils.FileRead;

public final class CoreFileJS {
	private final StringBuilder core;
	private final String greencodePath;
	private final Set<URL> mergedFiles = new HashSet<URL>();

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
		if(mergedFiles.contains(file)) {
			return null;
		}
		
		mergedFiles.add(file);
		return core.append(FileUtils.getContentFile(file));
	}

	public StringBuilder append(URL file, String charset) throws IOException {
		if(mergedFiles.contains(file)) {
			return null;
		}
		
		mergedFiles.add(file);
		return core.append(FileUtils.getContentFile(file, charset));
	}
	
	public StringBuilder append(URL file, Charset charset, FileRead read) throws IOException {
		if(mergedFiles.contains(file)) {
			return null;
		}
		
		mergedFiles.add(file);
		return core.append(FileUtils.getContentFile(file, charset, read));
	}

	public void save() throws IOException {
		String content = core.toString();
		if (GreenCodeConfig.Server.View.useMinified) {
			HtmlCompressor html = new HtmlCompressor();
			html.setRemoveIntertagSpaces(true);
			content = html.compress(content);
		}
		FileUtils.createFile(content, greencodePath + "/core.js");
	}
}
