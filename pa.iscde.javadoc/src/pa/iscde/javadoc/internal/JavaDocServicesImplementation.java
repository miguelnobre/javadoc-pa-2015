package pa.iscde.javadoc.internal;

import java.io.File;

import org.osgi.service.log.LogService;

import pa.iscde.javadoc.generator.StringTemplateVisitor;
import pa.iscde.javadoc.service.JavaDocServices;

public class JavaDocServicesImplementation implements JavaDocServices {

    @SuppressWarnings("unused")
    private final LogService logService;

    public JavaDocServicesImplementation() {
	this.logService = JavaDocServiceLocator.getLogService();
    }

    @Override
    public String render(File openedFile) {
	return render(openedFile, null, null);
    }

    @Override
    public String render(File openedFile, Type type, String name) {
	String generatedText = null;
	if (null != openedFile) {
	    StringBuilder sb = new StringBuilder();
	    StringTemplateVisitor jDoc = new StringTemplateVisitor(sb, type, name);
	    JavaDocServiceLocator.getJavaEditorService().parseFile(openedFile, jDoc);
	    generatedText = sb.toString();
	}
	return generatedText;
    }
}