package com.jrender.kernel;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.jrender.util.FileUtils;
import com.jrender.util.FileUtils.FileRead;

public final class CoreFileJS {
	private final StringBuilder core;
	private final String jrenderPath;
	private final Set<URL> mergedFiles = new HashSet<URL>();

	CoreFileJS(String jrenderPath) {
		this.core = new StringBuilder();
		this.jrenderPath = jrenderPath;
	}

	CoreFileJS(StringBuilder jsCore, String jrenderPath) {
		this.core = jsCore;
		this.jrenderPath = jrenderPath;
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
		if (JRenderConfig.Server.View.useMinified) {
			HtmlCompressor html = new HtmlCompressor();
			html.setRemoveIntertagSpaces(true);
			content = html.compress(content);
		}
		FileUtils.createFile(content, jrenderPath + "/core.js");
	}
}
