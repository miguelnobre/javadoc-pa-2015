package pa.iscde.javadoc.export.render;

import org.eclipse.jdt.core.dom.FieldDeclaration;

/**
 * Interface that specified the interface class the must to be implemented while
 * implementing on of the JavaDoc Extensions Points. This extension point allows
 * to customize the output generated by the rendering process for fields.
 * 
 * Inspired in the aspect idiom, it allows to intercept the javadoc generation
 * process of the fields containing in the class, by plugin configuration this
 * interceptor can be configured to run before or after the standard generation
 * process (or before or after other interceptors), the application scope can
 * also be restricted making the interceptor to be applied only to the fields
 * with a specified name.
 * 
 * The return value of the extension method is used to control is the rest of
 * the rendering chain ought to be executed (or the process should be stopped
 * for this element)
 *
 */
public interface JavaDocFieldRender {

    /**
     * Interface for the method that implements an interceptor the javadoc
     * rendering process for Fields. This method is triggered by the parsing
     * process and is invoked when the parser parses fields.
     * 
     * @param field
     *            {@link FieldDeclaration} found by the parser.
     * @param sb
     *            {@link StringBuffer} used to write the output of the current
     *            interceptor.
     * @return true if the rendering process for this stage is completed, false
     *         if the rest of the interceptor chain (which ends with the
     *         standard render process) ought to be executed:
     */
    boolean render(FieldDeclaration field, StringBuilder sb);

}
