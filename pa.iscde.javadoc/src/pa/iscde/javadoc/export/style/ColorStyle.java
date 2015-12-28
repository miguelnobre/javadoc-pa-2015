package pa.iscde.javadoc.export.style;

import java.awt.Color;

public class ColorStyle implements AnnotationStyleI {

    private int[] columns;
    private Color color;

    public ColorStyle(int[] columns) {
	this.columns = columns;
	this.color = Color.RED;
    }

    public ColorStyle(int[] columns, Color color) {
	this.columns = columns;
	this.color = color;
    }

    public String getInjectHTML() {
	return " style='color: #?;'";
    }

    public String getOpenHTMLTag() {
	return "<span" + getInjectHTML() + ">";
    }

    public String getEndHTMLTag() {
	return "</span>";
    }

    @Override
    public void getStyled(String[] columns) {
	for (int i : this.columns) {
	    String red = formatString(Integer.toHexString(this.color.getRed()), 2);
	    String blue = formatString(Integer.toHexString(this.color.getBlue()), 2);
	    String green = formatString(Integer.toHexString(this.color.getGreen()), 2);
	    String colorHtml = red + green + blue;

	    String html = null;
	    if (columns[i] != null) {
		// Se já existir um elemento HTML, inserimos o estilo
		if (columns[i].contains(">")) {
		    html = columns[i].replaceFirst(">", getInjectHTML() + ">");
		} // Se não existir elemento HTML, inserimos um <span> com o estilo
		else {
		    html = getOpenHTMLTag() + columns[i] + getEndHTMLTag();
		}
		columns[i] = html.replace("?", colorHtml);
	    }
	}
    }

    /**
     * Método que acrescenta 0's à esquerda, até atingir o compimento de string que se deseja,
     * 
     * @param text
     * @param radix
     * @return
     */
    private String formatString(String text, int radix) {
	StringBuilder sb = new StringBuilder();

	if (text.length() == radix) {
	    return text;
	}

	for (int i = 0; i < radix - text.length(); i++) {
	    sb.append("0");
	}
	return sb.append(text).toString();
    }
}
