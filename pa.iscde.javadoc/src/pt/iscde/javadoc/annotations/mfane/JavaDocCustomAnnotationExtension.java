package pt.iscde.javadoc.annotations.mfane;

import pa.iscde.javadoc.export.parser.JavaDocCustomTagI;

/**
 * Implementação da Extensão para definir uma nova Custom Tag
 * 
 * @author Miguel
 *
 */
public interface JavaDocCustomAnnotationExtension {

    public JavaDocCustomTagI getJavaDocCustomTag();
}