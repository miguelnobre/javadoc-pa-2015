package pa.iscde.javadoc.export.render;

import org.eclipse.jdt.core.dom.ASTNode;

public interface JavaDocGenericRender {

    boolean render(ASTNode node, StringBuilder sb);

}
