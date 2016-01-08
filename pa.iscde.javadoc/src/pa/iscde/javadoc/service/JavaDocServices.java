package pa.iscde.javadoc.service;

import java.io.File;

/**
 * Interfaces that specifies the Services provided by the JavaDoc Plugin. The
 * JavaDoc Plugin provides, as a service, a method that allows other PIDESCO
 * plugins to request on-demand the generation the Javadoc for a specified file
 * or for a specific method or field contained in specified file.
 * 
 */
public interface JavaDocServices {

    /**
     * Types of elements to generate javadoc to:
     * 
     * <li>{@link METHOD}</li>
     * <li>{@link FIELD}</li>
     *
     */
    enum Type {
	/**
	 * Type Method
	 */
	METHOD,
	/**
	 * Type Method
	 */
	FIELD
    };

    /**
     * Returns a {@link String} containing the generated javadoc for the
     * specified file.
     * 
     * @param file
     *            name of the file for which the will be generated.
     * @return the generated javadoc.
     */
    String render(File file);

    /**
     * Returns a {@link String} containing the generated javadoc for the method
     * / field contained in the specified file
     * 
     * @param file
     *            name of the file for which javadoc the will be generated
     * @param type
     *            {@link Type} of the element specified by the field
     *            <code>name</code>
     * @param name
     *            of the field or method for which should the javadoc be
     *            generated
     * @return the generated javadoc
     */
    String render(File file, Type type, String name);

}