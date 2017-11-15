package com.jrender.kernel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.jrender.database.implementation.DatabaseConnectionEvent;
import com.jrender.jscript.dom.Form;
import com.jrender.jscript.dom.Window;
import com.jrender.kernel.implementation.BootActionImplementation;
import com.jrender.kernel.implementation.PluginImplementation;
import com.jrender.util.MergedFile;

class Cache {
	static final File defaultTemplate = null;
	static final Map<String, File> templates = new HashMap<String, File>();
	static final BootActionImplementation bootAction = null;
	static final Class<? super DatabaseConnectionEvent> classDatabaseConnectionEvent = null;
	static final PluginImplementation[] plugins = null;
	static final Map<String, Class<? extends Form>> forms = new HashMap<String, Class<? extends Form>>();
	static final Map<String, Class<? extends Window>> registeredWindows = new HashMap<String, Class<? extends Window>>();
	static final Map<String, MergedFile> mergedFiles = new HashMap<String, MergedFile>();
}
