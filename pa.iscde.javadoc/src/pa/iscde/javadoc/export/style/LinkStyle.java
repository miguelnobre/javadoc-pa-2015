package pa.iscde.javadoc.export.style;

public class LinkStyle implements AnnotationStyleI {

    private int urlColumn;
    private int aliasColumn;
    private boolean supressUrl;

    public LinkStyle(int urlColumn, int aliasColumn) {
	this(urlColumn, aliasColumn, true);
    }

    public LinkStyle(int urlColumn, int aliasColumn, boolean supressUrl) {
	this.urlColumn = urlColumn;
	this.aliasColumn = aliasColumn;
	this.supressUrl = supressUrl;
    }

    public String getOpenHTMLTag() {
	return "<a href='?'>";
    }

    public String getEndHTMLTag() {
	return "</a>";
    }

    @Override
    public void getStyled(String[] columns) {
	String url = columns[this.urlColumn];
	String alias = columns[this.aliasColumn];

	String link = getOpenHTMLTag() + alias + getEndHTMLTag();
	link = link.replace("?", url);
	columns[this.aliasColumn] = link;

	if (supressUrl) {
	    columns[this.urlColumn] = null;
	}
    }
}