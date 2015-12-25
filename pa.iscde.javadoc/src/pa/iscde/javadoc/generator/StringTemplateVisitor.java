package pa.iscde.javadoc.generator;

import java.io.File;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import pa.iscde.javadoc.parser.JavaDocParser;
import pa.iscde.javadoc.parser.structure.JavaDocBlock;

public class StringTemplateVisitor extends ASTVisitor {

    private static final STGroup group;

    private StringBuilder stringBuilder;
    private JavaDocParser javaDocParser = new JavaDocParser();

    static {
	group = new STGroupFile("/pa/iscde/javadoc/templates/javadoc.stg");
    }

    public StringTemplateVisitor(final StringBuilder stringBuilder) {
	super(true);
	this.stringBuilder = stringBuilder;
    }

    private void renderNode(final ASTNode node, final String operation) {
	final ST template = group.getInstanceOf(operation + "_" + node.getClass().getSimpleName());

	if (null != template) {
	    BodyDeclaration bd = (BodyDeclaration) node;
	    JavaDocBlock javaDoc = new JavaDocBlock();

	    if (bd.getJavadoc() != null) {
		javaDoc = javaDocParser.parseJavaDoc(bd.getJavadoc().toString());
	    }

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