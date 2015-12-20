package pa.iscde.javadoc.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import pa.iscde.javadoc.internal.JavaDocServiceLocator;
import pa.iscde.javadoc.parser.JavaDocParser;
import pa.iscde.javadoc.parser.structure.JavaDocBlock;

public class StringTemplateVisitor extends ASTVisitor {

	private static final STGroup group;

	private StringBuilder stringBuilder;
	private JavaDocParser javaDocParser = new JavaDocParser();
	private static Map<String, File> workSpaceFiles = new HashMap<String, File>();

	static {
		group = new STGroupFile("/pa/iscde/javadoc/templates/javadoc.stg");
	}

	public StringTemplateVisitor(final StringBuilder stringBuilder) {
		super(true);
		this.stringBuilder = stringBuilder;
	}

	@Override
	public boolean visit(Javadoc node) {
		return super.visit(node);
	}

	private void renderNode(final ASTNode node, final String operation) {
		final ST template = group.getInstanceOf(operation + "_" + node.getClass().getSimpleName());
		if (null != template) {
			BodyDeclaration bd = (BodyDeclaration) node;
			JavaDocBlock javaDoc = new JavaDocBlock();

			if (bd.getJavadoc() != null) {
				javaDoc = javaDocParser.parseJavaDoc(bd.getJavadoc().toString());
				
				listf(JavaDocServiceLocator.getProjectBrowserService().getRootPackage().getFile().getAbsolutePath(), workSpaceFiles);
				
				
				if (node instanceof MethodDeclaration) {
					MethodDeclaration method = (MethodDeclaration) node;
					MethodDeclarationsWrapper methodWrapper = new MethodDeclarationsWrapper(method, workSpaceFiles);
					
					// System.out.println(methodWrapper.getSignature());
					template.add("MethodWrapper", methodWrapper);
				}

				template.add(node.getClass().getSimpleName(), node);
				template.add("JavaDoc", javaDoc);

				stringBuilder.append(template.render());
			}
		}

	}

	public Map<String, File> listf(String directoryName, Map<String, File> workSpaceFiles) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				if (file.getName().endsWith(".java")) {
//					System.out.println(file.getName());
					workSpaceFiles.put(file.getName(), file.getAbsoluteFile());
				}
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), workSpaceFiles);
			}
		}
		
		return workSpaceFiles;
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
