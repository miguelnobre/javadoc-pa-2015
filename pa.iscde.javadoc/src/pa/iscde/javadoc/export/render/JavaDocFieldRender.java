package pa.iscde.javadoc.export.render;

import org.eclipse.jdt.core.dom.FieldDeclaration;

public interface JavaDocFieldRender {

    boolean render(FieldDeclaration field, StringBuilder sb);

}
