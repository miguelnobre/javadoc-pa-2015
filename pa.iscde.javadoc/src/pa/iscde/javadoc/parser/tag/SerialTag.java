package pa.iscde.javadoc.parser.tag;

import pa.iscde.javadoc.export.parser.JavaDocUnnamedTagI;

public class SerialTag implements JavaDocUnnamedTagI {

	@Override
	public String getHeaderName() {
		return "Serial";
	}

	@Override
	public String getTagName() {
		return "serial";
	}
}