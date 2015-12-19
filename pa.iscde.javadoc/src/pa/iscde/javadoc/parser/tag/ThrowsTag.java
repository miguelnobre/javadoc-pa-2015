package pa.iscde.javadoc.parser.tag;

import pa.iscde.javadoc.parser.export.JavaDocNamedTagI;

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