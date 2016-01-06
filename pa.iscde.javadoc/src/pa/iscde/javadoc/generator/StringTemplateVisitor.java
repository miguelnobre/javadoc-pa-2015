package pa.iscde.javadoc.generator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.osgi.service.log.LogService;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.google.common.collect.LinkedHashMultimap;

import pa.iscde.javadoc.export.render.JavaDocFieldRender;
import pa.iscde.javadoc.export.render.JavaDocGenericRender;
import pa.iscde.javadoc.export.render.JavaDocMethodRender;
import pa.iscde.javadoc.internal.JavaDocServiceLocator;
import pa.iscde.javadoc.parser.JavaDocParser;
import pa.iscde.javadoc.parser.structure.JavaDocBlock;
import pa.iscde.javadoc.service.JavaDocServices;
import pa.iscde.javadoc.service.JavaDocServices.Type;

public class StringTemplateVisitor extends ASTVisitor {

    private static final STGroup group;

    private StringBuilder stringBuilder;

    private Type type;
    private String name;
    private boolean showElementsWithoutJavaDoc;

    private JavaDocParser javaDocParser = new JavaDocParser();

    static {
	group = new STGroupFile("/pa/iscde/javadoc/templates/javadoc.stg");
    }

    private static final String EXTENSION_POINT_ID = "pa.iscde.javadoc.renderers";

    private static final String EXTENSION_RENDER_METHOD = "methodRender";
    private static final String EXTENSION_RENDER_FIELD = "fieldRender";
    private static final String EXTENSION_RENDER_GENERIC = "genericRender";

    private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_RENDER_TYPE = "renderType";
    private static final String ATTRIBUTE_RENDER_TYPE_PREVISIT = "preVisit";
    private static final String ATTRIBUTE_RENDER_TYPE_POSTVISIT = "postVisit";

    private static final String SCOPE_ALL = "!!ALL!!";

    // Type -> (pre|pos) -> (name | ALL) -> renderClass;
    private static Map<Class<? extends ASTNode>, Map<String, LinkedHashMultimap<String, Object>>> renderers = new HashMap<Class<? extends ASTNode>, Map<String, LinkedHashMultimap<String, Object>>>();

    static {
	Map<String, LinkedHashMultimap<String, Object>> m;

	m = new HashMap<String, LinkedHashMultimap<String, Object>>();
	m.put(ATTRIBUTE_RENDER_TYPE_PREVISIT, LinkedHashMultimap.<String, Object> create());
	m.put(ATTRIBUTE_RENDER_TYPE_POSTVISIT, LinkedHashMultimap.<String, Object> create());
	renderers.put(ASTNode.class, m);

	m = new HashMap<String, LinkedHashMultimap<String, Object>>();
	m.put(ATTRIBUTE_RENDER_TYPE_PREVISIT, LinkedHashMultimap.<String, Object> create());
	m.put(ATTRIBUTE_RENDER_TYPE_POSTVISIT, LinkedHashMultimap.<String, Object> create());
	renderers.put(MethodDeclaration.class, m);

	m = new HashMap<String, LinkedHashMultimap<String, Object>>();
	m.put(ATTRIBUTE_RENDER_TYPE_PREVISIT, LinkedHashMultimap.<String, Object> create());
	m.put(ATTRIBUTE_RENDER_TYPE_POSTVISIT, LinkedHashMultimap.<String, Object> create());
	renderers.put(FieldDeclaration.class, m);
    }

    private static void addExtensionRenderers() {
	IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
	if (null != extensionPoint) {
	    IExtension[] extensions = extensionPoint.getExtensions();
	    for (IExtension e : extensions) {
		IConfigurationElement[] confElements = e.getConfigurationElements();
		for (IConfigurationElement c : confElements) {
		    try {
			@SuppressWarnings("unused")
			final String name = c.getAttribute(ATTRIBUTE_NAME);
			final String renderType = c.getAttribute(ATTRIBUTE_RENDER_TYPE);
			final String renderName = c.getChildren()[0].getName();
			final Object render = c.getChildren()[0].createExecutableExtension(ATTRIBUTE_CLASS);
			final String renderAttribute = c.getChildren()[0].getAttribute(ATTRIBUTE_NAME);
			final String renderScope = (renderAttribute != null && renderAttribute.trim().length() > 0)
				? renderAttribute.trim() : SCOPE_ALL;
			final Class<? extends ASTNode> clazz;
			switch (renderName) {
			case EXTENSION_RENDER_METHOD:
			    clazz = MethodDeclaration.class;
			    break;
			case EXTENSION_RENDER_FIELD:
			    clazz = FieldDeclaration.class;
			    break;
			case EXTENSION_RENDER_GENERIC:
			    clazz = ASTNode.class;
			    break;
			default:
			    throw new IllegalArgumentException();
			}
			renderers.get(clazz).get(renderType).put(renderScope, render);
		    } catch (CoreException ex) {
			JavaDocServiceLocator.getLogService().log(LogService.LOG_ERROR, ex.getMessage());
		    }
		}
	    }
	}
    }

    public StringTemplateVisitor(final StringBuilder stringBuilder) {
	this(stringBuilder, true);
    }

    public StringTemplateVisitor(final StringBuilder stringBuilder, final JavaDocServices.Type type,
	    final String name) {
	this(stringBuilder, true);
	this.type = type;
	this.name = name;
    }

    public StringTemplateVisitor(final StringBuilder stringBuilder, boolean showElementsWithoutJavaDoc) {
	super(true);
	addExtensionRenderers();
	this.stringBuilder = stringBuilder;
	this.showElementsWithoutJavaDoc = showElementsWithoutJavaDoc;
    }

    private void renderNode(final ASTNode node, final String operation) {

	final ST template = group.getInstanceOf(operation + "_" + node.getClass().getSimpleName());

	if (type != null && name != null) {
	    if (type == Type.METHOD && node instanceof MethodDeclaration) {
		MethodDeclaration mDeclaration = (MethodDeclaration) node;
		if (!mDeclaration.getName().getFullyQualifiedName().equals(name)) {
		    return;
		}
	    } else if (type == Type.FIELD && node instanceof FieldDeclaration) {
		FieldDeclaration fDeclaration = (FieldDeclaration) node;
		if (!((VariableDeclarationFragment) fDeclaration.fragments().get(0)).getName().getFullyQualifiedName()
			.equals(name)) {
		    return;
		}
	    } else {
		return;
	    }
	}

	if (processExtensions(node, operation, stringBuilder)) {
	    return;
	}

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

    private boolean processMethodExtension(JavaDocMethodRender jRender, MethodDeclaration mDeclaration,
	    StringBuilder sb) {
	final StringBuilder sbExtension = new StringBuilder();
	final boolean completed = jRender.render(mDeclaration, sbExtension);
	if (sbExtension.length() > 0) {
	    sb.append(sbExtension);
	}
	return completed;
    }

    private boolean processFieldExtension(JavaDocFieldRender jRender, FieldDeclaration fDeclaration, StringBuilder sb) {
	final StringBuilder sbExtension = new StringBuilder();
	final boolean completed = jRender.render(fDeclaration, sbExtension);
	if (sbExtension.length() > 0) {
	    sb.append(sbExtension);
	}
	return completed;
    }

    private boolean processExtensions(final ASTNode node, final String operation, final StringBuilder sb) {
	Map<String, LinkedHashMultimap<String, Object>> m = renderers.get(node.getClass());
	if (null != m) {
	    LinkedHashMultimap<String, Object> r = m.get(operation);
	    if (node instanceof MethodDeclaration) {	
		MethodDeclaration mDeclaration = (MethodDeclaration) node;
		for (Object o : r.get(mDeclaration.getName().getFullyQualifiedName())) {
		    if (processMethodExtension((JavaDocMethodRender) o, mDeclaration, sb)) {
			return true;
		    }
		}
		for (Object o : r.get(SCOPE_ALL)) {
		    if (processMethodExtension((JavaDocMethodRender) o, mDeclaration, sb)) {
			return true;
		    }
		}
		
	    } else if (node instanceof FieldDeclaration) {		
		FieldDeclaration fDeclaration = (FieldDeclaration) node;
		for (Object o : r.get(((VariableDeclarationFragment) fDeclaration.fragments().get(0)).getName()
			.getFullyQualifiedName())) {
		    if (processFieldExtension((JavaDocFieldRender) o, fDeclaration, sb)) {
			return true;
		    }
		}
		for (Object o : r.get(SCOPE_ALL)) {
		    if (processFieldExtension((JavaDocFieldRender) o, fDeclaration, sb)) {
			return true;
		    }
		}		
	    } else {		
		for (Object o : r.get(SCOPE_ALL)) {
		    JavaDocGenericRender jRender = (JavaDocGenericRender) o;
		    final StringBuilder sbExtension = new StringBuilder();
		    final boolean completed = jRender.render(node, sbExtension);
		    if (sbExtension.length() > 0) {
			sb.append(sbExtension);
		    }
		    if (completed) {
			return true;
		    }
		}
	    }
	}
	return false;
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