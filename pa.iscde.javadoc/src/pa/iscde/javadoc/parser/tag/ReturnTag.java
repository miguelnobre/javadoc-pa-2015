package pa.iscde.javadoc.parser.tag;

import pa.iscde.javadoc.export.parser.JavaDocUnnamedTagI;

public class ReturnTag implements JavaDocUnnamedTagI {

	@Override
	public String getTagName() {
		return "return";
	}

	@Override
	public String getHeaderName() {
		return "Returns";
	}
}