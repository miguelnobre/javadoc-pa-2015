package pa.iscde.javadoc.extensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.swt.graphics.Image;

import extensionpoints.Item;
import extensionpoints.OutputPreview;
import pa.iscde.javadoc.internal.JavaDocServiceLocator;
import pa.iscde.javadoc.internal.JavaDocView;

public class DeepSearchOutputPreview implements OutputPreview {

    private static final String JAVADOC_IMAGE_PATH = "/images/javadoc.gif";

    private static final String JAVADOC_TITLE = "Javadoc";

    private static final int NLINES_BEFORE = 2;
    private static final int NLINES_AFTER = 2;

    private final Image JAVADOC_IMAGE = JavaDocServiceLocator.getImage(JAVADOC_IMAGE_PATH);

    private final Item ROOT_NODE = new DeepSearchOutputItem(JAVADOC_TITLE, "", "", JAVADOC_IMAGE);

    private ArrayList<Item> foundItems = new ArrayList<>();

    @Override
    public void search(String textSearch, String textSearchInCombo, String specificTextSearchInCombo,
	    String textSearchForCombo, ArrayList<String> buttonsSelectedSearchForCombo) {

	foundItems.clear();
	if (null != JavaDocView.getInstance()) {
	    final String htmlText = JavaDocView.getInstance().getLastGeneratedText();
	    String[] htmlLines = htmlText.split("\n|<br>|</br>|<p>|</p>");
	    for (int line = 0; line < htmlLines.length; line++) {
		if (htmlLines[line].contains(textSearch)) {
		    final String htmlString = formatPreviewText(htmlLines, line, NLINES_BEFORE, NLINES_AFTER);
		    DeepSearchOutputItem item = new DeepSearchOutputItem(JAVADOC_TITLE, htmlString, textSearch.toLowerCase(),
			    JAVADOC_IMAGE, Integer.valueOf(line));
		    foundItems.add(item);
		}
	    }
	}
    }

    private String formatPreviewText(final String[] htmlLines, final int line, final int before, final int after) {
	StringBuilder sb = new StringBuilder();
	for (int i = line - before; i < line + after + 1; i++) {
	    if (i > 0 && i < htmlLines.length) {
		final String trimmed = htmlLines[i].replaceAll("<[^>]*>", "").trim();
		if (trimmed.length() > 0) {
		    sb.append(trimmed).append('\n');
		}
	    }
	}
	return sb.toString();
    }

    @Override
    public Collection<Item> getParents() {
	if (null != JavaDocView.getInstance()) {
	    return Arrays.asList(ROOT_NODE);
	} else {
	    return Collections.emptyList();
	}
    }

    @Override
    public Collection<Item> getChildren(String parent) {
	if (JAVADOC_TITLE.equals(parent)) {
	    return foundItems;
	} else {
	    return Collections.emptyList();
	}
    }

    @Override
    public void doubleClick(Item item) {

    }

}
