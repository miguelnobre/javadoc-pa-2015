package pa.iscde.javadoc.export.style;

import java.awt.Color;

public class StyleToolBox {

    private StyleToolBox() throws Exception {
	throw new Exception("Não é possivel instanciar a Classe " + StyleToolBox.class);
    }

    public static BoldStyle BoldStyle(int...columnsToBeStyled) {
	return new BoldStyle(columnsToBeStyled);
    }

    public static ItalicStyle ItalicStyle(int...columnsToBeStyled) {
	return new ItalicStyle(columnsToBeStyled);
    }

    public static LinkStyle LinkStyle(int urlColumn, int aliasColumn) {
	return new LinkStyle(urlColumn, aliasColumn);
    }

    public static LinkStyle LinkStyle(int urlColumn, int aliasColumn, boolean supressUrl) {
	return new LinkStyle(urlColumn, aliasColumn, supressUrl);
    }

    public static ColorStyle ColorStyle(Color color, int...columnsToBeStyled) {
	return new ColorStyle(columnsToBeStyled, color);
    }

    public static ColorStyle ColorRedStyle(int...columnsToBeStyled) {
	return new ColorStyle(columnsToBeStyled, Color.RED);
    }

    public static ColorStyle ColorGreenStyle(int...columnsToBeStyled) {
	return new ColorStyle(columnsToBeStyled, Color.GREEN);
    }

    public static ColorStyle ColorBlueStyle(int...columnsToBeStyled) {
	return new ColorStyle(columnsToBeStyled, Color.BLUE);
    }
}