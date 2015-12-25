package pa.iscde.javadoc.parser.structure;

public interface JavaDocTagI {

    /**
     * Define o nome do cabeçalho da Tag na View
     * 
     * @return Header Name
     */
    public abstract String getHeaderName();

    /**
     * Define o nome da Tag da anotação
     * 
     * @return Nome da Tag
     */
    public abstract String getTagName();
}