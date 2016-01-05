package pa.iscde.javadoc.export.render;

import org.eclipse.jdt.core.dom.MethodDeclaration;

public interface JavaDocMethodRender {

    boolean render(MethodDeclaration field, StringBuilder sb);

}
