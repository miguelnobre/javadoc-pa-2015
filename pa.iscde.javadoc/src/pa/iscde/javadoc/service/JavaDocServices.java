package pa.iscde.javadoc.service;

import java.io.File;

public interface JavaDocServices {
    
    enum Type {
	METHOD,
	FIELD
    };
    
    String render(File file);
    
    String render(File file, Type type, String name);

}