package pa.iscde.javadoc.generator;

public class ObjectMap {

    private String type;
    private String name;
    private String file;

    public ObjectMap(String type, String name, String file) {
	this.type = type;
	this.name = name;
	this.file = file;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getFile() {
	return file;
    }

    public void setFile(String file) {
	this.file = file;
    }

    public String getType() {
	return this.type;
    }

    public void setType(String type) {
	this.type = type;
    }
}