package greencode.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public final class MergedFile extends File {
	private static final long serialVersionUID = 1L;	
	private final File[] files;
	private final Long[] lastModifications;
	
	public MergedFile(File parent, String child, File[] files) throws IOException {
		super(parent, child);
		this.files = files;
		this.lastModifications = new Long[files.length];
		this.createNewFile();
		merge();
	}

	public MergedFile(String parent, String child, File[] files) throws IOException {
		super(parent, child);
		this.files = files;
		this.lastModifications = new Long[files.length];
		this.createNewFile();
		merge();
	}

	public MergedFile(String pathname, File[] files) throws IOException {
		super(pathname);
		this.files = files;
		this.lastModifications = new Long[files.length];
		this.createNewFile();
		merge();
	}

	public MergedFile(URI uri, File[] files) throws IOException {
		super(uri);
		this.files = files;
		this.lastModifications = new Long[files.length];
		this.createNewFile();
		merge();
	}
	
	public boolean verifyChanges() throws IOException {
		for (int i = -1, s = files.length; ++i < s;) {
			if(lastModifications[i] != files[i].lastModified()){
				merge();
				return true;
			}
		}
		
		return false;
	}
	
	public void merge() throws IOException {
		StringBuilder joinContent = new StringBuilder();
		for (int i = -1, s = files.length; ++i < s;) {
			File f = files[i];
			lastModifications[i] = f.lastModified();
			joinContent.append(FileUtils.getContentFile(f.toURI().toURL()));
		}
		
		FileUtils.createFile(joinContent.toString(), this);
	}
}
