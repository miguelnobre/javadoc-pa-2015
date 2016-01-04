package pa.iscde.javadoc.generator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.osgi.service.log.LogService;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import pa.iscde.javadoc.export.render.JavaDocFieldRender;
import pa.iscde.javadoc.export.render.JavaDocGenericRender;
import pa.iscde.javadoc.export.render.JavaDocMethodRender;
import pa.iscde.javadoc.internal.JavaDocServiceLocator;
import pa.iscde.javadoc.parser.JavaDocParser;
import pa.iscde.javadoc.parser.structure.JavaDocBlock;

public class StringTemplateVisitor extends ASTVisitor {

    private static final STGroup group;

    private StringBuilder stringBuilder;
    private boolean showElementsWithoutJavaDoc;
    private JavaDocParser javaDocParser = new JavaDocParser();

    static {
	group = new STGroupFile("/pa/iscde/javadoc/templates/javadoc.stg");
    }

    ////

    private static final String EXTENSION_RENDER_METHOD = "methodRender";
    private static final String EXTENSION_RENDER_FIELD = "fieldRender";
    private static final String EXTENSION_RENDER_GENERIC = "genericRender";

    private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_NAME = "name";

    private static final String SCOPE_ALL = "!!ALL!!";

    private static Multimap<String, JavaDocMethodRender> methodRenderers = ArrayListMultimap.create();
    private static Multimap<String, JavaDocFieldRender> fieldRenderers = ArrayListMultimap.create();
    private static List<JavaDocGenericRender> genericRenderers = new ArrayList<JavaDocGenericRender>();

    private static void addExtensionRenderers() {

	IExtensionRegistry extRegistry = Platform.getExtensionRegistry();
	IExtensionPoint extensionPoint = extRegistry.getExtensionPoint("pa.iscde.javadoc.render");

	if (null != extensionPoint) {

	    IExtension[] extensions = extensionPoint.getExtensions();
	    for (IExtension e : extensions) {
		IConfigurationElement[] confElements = e.getConfigurationElements();
		for (IConfigurationElement c : confElements) {
		    try {
			final String attribute = c.getAttribute(ATTRIBUTE_NAME);
			final JavaDocGenericRender genericRender = (JavaDocGenericRender) c
				.createExecutableExtension(ATTRIBUTE_CLASS);
			final String name = (attribute != null && attribute.trim().length() > 0) ? attribute.trim()
				: SCOPE_ALL;
			switch (c.getName()) {
			case EXTENSION_RENDER_METHOD:
			    methodRenderers.put(name, (JavaDocMethodRender) genericRender);
			    break;
			case EXTENSION_RENDER_FIELD:
			    fieldRenderers.put(name, (JavaDocFieldRender) genericRender);
			    break;
			case EXTENSION_RENDER_GENERIC:
			    genericRenderers.add(genericRender);
			    break;
			default:
			    throw new IllegalArgumentException();
			}
		    } catch (CoreException ex) {
			JavaDocServiceLocator.getLogService().log(LogService.LOG_ERROR, ex.getMessage());
		    }
		}
	    }
	}
    }

    /////
    public StringTemplateVisitor(final StringBuilder stringBuilder) {
	this(stringBuilder, false);
    }

    public StringTemplateVisitor(final StringBuilder stringBuilder, boolean showElementsWithoutJavaDoc) {
	super(true);
	addExtensionRenderers();
	this.stringBuilder = stringBuilder;
	this.showElementsWithoutJavaDoc = showElementsWithoutJavaDoc;
    }

    private void renderNode(final ASTNode node, final String operation) {
	final ST template = group.getInstanceOf(operation + "_" + node.getClass().getSimpleName());

	if (null != template) {
	    BodyDeclaration bd = (BodyDeclaration) node;
	    JavaDocBlock javaDoc = new JavaDocBlock();

	    if (bd.getJavadoc() != null) {
		javaDoc = javaDocParser.parseJavaDoc(bd.getJavadoc().toString());
	    }

	    if (bd.getJavadoc() != null || showElementsWithoutJavaDoc) {
		if (node instanceof MethodDeclaration) {
		    MethodDeclaration method = (MethodDeclaration) node;
		    MethodDeclarationsWrapper methodWrapper = new MethodDeclarationsWrapper(method);

		    template.add("MethodWrapper", methodWrapper);
		} else if (node instanceof FieldDeclaration) {
		    FieldDeclaration field = (FieldDeclaration) node;
		    FieldDeclarationWrapper fieldWrapper = new FieldDeclarationWrapper(field);

		    template.add("FieldDeclarationWrapper", fieldWrapper);
		}

		template.add(node.getClass().getSimpleName(), node);
		template.add("JavaDoc", javaDoc);

		stringBuilder.append(template.render());
	    }
	}
    }

    @Override
    public void preVisit(ASTNode node) {
	renderNode(node, "preVisit");
	super.preVisit(node);
    }

    @Override
    public void postVisit(ASTNode node) {
	renderNode(node, "postVisit");
	super.postVisit(node);
    }
}