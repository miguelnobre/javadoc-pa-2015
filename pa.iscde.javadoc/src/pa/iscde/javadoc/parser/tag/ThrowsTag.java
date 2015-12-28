package pa.iscde.javadoc.parser.tag;

import pa.iscde.javadoc.export.parser.JavaDocNamedTagI;

public class ThrowsTag implements JavaDocNamedTagI {

	@Override
	public String getHeaderName() {
		return "Throws";
	}

	@Override
	public String getTagName() {
		return "throws";
	}
}