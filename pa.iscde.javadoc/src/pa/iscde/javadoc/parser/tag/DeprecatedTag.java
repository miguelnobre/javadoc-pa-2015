package pa.iscde.javadoc.parser.tag;

import pa.iscde.javadoc.export.parser.JavaDocUnnamedTagI;

public class DeprecatedTag implements JavaDocUnnamedTagI {

	@Override
	public String getHeaderName() {
		return "Deprecated";
	}

	@Override
	public String getTagName() {
		return "deprecated";
	}
}