package pa.iscde.javadoc.extensions;

import org.eclipse.swt.graphics.Image;

import extensionpoints.Item;

class DeepSearchOutputItem implements Item {

    private String name;
    private String textPreview;
    private String textHighlighted;

    private Image image;

    private Object specialData;

    public DeepSearchOutputItem(final String name, final String textPreview, final String textHighlighted,
	    final Image image) {
	this(name, textPreview, textHighlighted, image, null);
    }

    public DeepSearchOutputItem(final String name, final String textPreview, final String textHighlighted,
	    final Image image, final Object specialData) {
	this.name = name;
	this.textPreview = textPreview;
	this.textHighlighted = textHighlighted;
	this.image = image;
	this.specialData = specialData;
    }

    @Override
    public void setItem(final String nameItem, final String textPreview, final String textHighlighted) {
	this.name = nameItem;
	this.textPreview = textPreview;
	this.textHighlighted = textHighlighted;
    }

    @Override
    public String getName() {
	return this.name;
    }

    @Override
    public String getPreviewText() {
	return this.textPreview;
    }

    @Override
    public String getHighlightText() {
	return this.textHighlighted;
    }

    @Override
    public void setImg(final Image image) {
	this.image = image;
    }

    @Override
    public Image getImg() {
	return this.image;
    }

    @Override
    public void setSpecialData(final Object specialData) {
	this.specialData = specialData;
    }

    @Override
    public Object getSpecialData() {
	return this.specialData;
    }

}
