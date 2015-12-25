package pa.iscde.javadoc.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import pa.iscde.javadoc.internal.JavaDocServiceLocator;

public class CacheObjectMapper {

    private static CacheObjectMapper instance = null;
    private Map<String, File> workSpaceFiles = null;

    private CacheObjectMapper() {
	loadCache();
    }

    private void loadCache() {
	this.workSpaceFiles = new HashMap<String, File>();
	String workspaceRoot = JavaDocServiceLocator.getProjectBrowserService().getRootPackage().getFile().getAbsolutePath();
	loadFiles(workspaceRoot);
    }

    private void loadFiles(String directoryPath) {
	File directory = new File(directoryPath);

	File[] fList = directory.listFiles();
	for (File file : fList) {
	    if (file.isFile()) {
		if (file.getName().endsWith(".java")) {
		    this.workSpaceFiles.put(file.getName(), file.getAbsoluteFile());
		}
	    } else if (file.isDirectory()) {
		loadFiles(file.getAbsolutePath());
	    }
	}
    }

    public static CacheObjectMapper getInstance() {
	if (instance == null) {
	    instance = new CacheObjectMapper();
	}

	return instance;
    }

    public void refreshCache() {
	loadCache();
    }

    public String getFilePath(String clazz) {
	return this.workSpaceFiles.get(clazz + ".java") != null ? this.workSpaceFiles.get(clazz + ".java").getAbsolutePath() : null;
    }
}