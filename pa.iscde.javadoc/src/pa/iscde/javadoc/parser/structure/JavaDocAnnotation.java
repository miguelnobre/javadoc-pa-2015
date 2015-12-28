package pa.iscde.javadoc.parser.structure;

/**
 * Representa uma Tag e o seu Conteudo
 * @author Miguel
 *
 */
public class JavaDocAnnotation {

    private JavaDocTagI tag;
    private String description;

    public JavaDocAnnotation(JavaDocTagI tag, String description) {
	this.tag = tag;
	this.description = description;
    }

    public JavaDocTagI getTag() {
	return tag;
    }

    public void setTag(JavaDocTagI tag) {
	this.tag = tag;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getTagName() {
	return this.tag.getTagName();
    }
}