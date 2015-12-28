package pa.iscde.javadoc.export.style;

public class ItalicStyle implements AnnotationStyleI {

    private int[] columnsIndex;

    public ItalicStyle(int[] columnsToBeStyled) {
	this.columnsIndex = columnsToBeStyled;
    }

    public String getOpenHTMLTag() {
	return "<i>";
    }

    public String getEndHTMLTag() {
	return "</i>";
    }

    @Override
    public void getStyled(String[] columns) {
	for (int i : columnsIndex) {
	    columns[i] = getOpenHTMLTag() + columns[i] + getEndHTMLTag();
	}
    }
}