package pa.iscde.javadoc.export.style;

public class BoldStyle implements AnnotationStyleI {

    private int[] columnsIndex;

    public BoldStyle(int[] columnsToBeStyled) {
	this.columnsIndex = columnsToBeStyled;
    }

    public String getOpenHTMLTag() {
	return "<b>";
    }

    public String getEndHTMLTag() {
	return "</b>";
    }

    public int[] getColumnsIndex() {
	return columnsIndex;
    }

    public void setColumnsIndex(int[] columnsIndex) {
	this.columnsIndex = columnsIndex;
    }

    @Override
    public void getStyled(String[] columns) {
	for (int i : columnsIndex) {
	    columns[i] = getOpenHTMLTag() + columns[i] + getEndHTMLTag();
	}
    }
}