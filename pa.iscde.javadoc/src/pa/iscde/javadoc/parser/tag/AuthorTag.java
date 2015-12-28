package pa.iscde.javadoc.parser.tag;

import pa.iscde.javadoc.export.parser.JavaDocUnnamedTagI;

public class AuthorTag implements JavaDocUnnamedTagI {

	@Override
	public String getHeaderName() {
		return "Author";
	}

	@Override
	public String getTagName() {
		return "author";
	}
}