package pa.iscde.javadoc.export.parser;

import pa.iscde.javadoc.export.style.AnnotationStyleI;
import pa.iscde.javadoc.parser.structure.JavaDocTagI;

/**
 * JavaDoc Tag Customizavel
 * @author Miguel
 *
 */
public interface JavaDocCustomTag extends JavaDocTagI {

    /**
     * Permite definir quantas colunas a tag vai suportar
     */
    public int nColumns();
    
    /**
     * Permite definir qual o separador, que separa o conteudo entre as colunas
     */
    public String getColumnSeparator();
    
    /**
     * Permite definir Estilos a determinadas colunas
     * O indice das colunas tem que estar entre 0 e nColumns() - 1
     * Os estilos estao disponiveis pela classe StyleToolBox
     * Ex: StyleToolBox.BoldStyle(new int[] { 0, 2 }) - Indica que o conteudo da coluna 0 e 2 vai ser apresentado a Bold. 
     */
    public AnnotationStyleI[] getAnnotationStyle();
}