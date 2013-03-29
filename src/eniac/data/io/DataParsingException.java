/*
 * Created on 12.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.io;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DataParsingException extends RuntimeException {

    private static final String[] MESSAGES = {
            "Unknown tag: ", "missing attribute: " }; //$NON-NLS-1$ //$NON-NLS-2$

    public static final short UNKNOWN_TAG = 0;

    public static final short MISSING_ATTRIBUTE = 1;

    ////////////////////////////// constructors
    // ////////////////////////////////

    public DataParsingException(Exception e) {
        super(e.getClass().getName() + ": " + e.getMessage()); //$NON-NLS-1$
    }

    public DataParsingException(String key, String attributeName, Class c) {
        super("Unknown key " //$NON-NLS-1$
                + key + " for attribute " //$NON-NLS-1$
                + attributeName + " in class " //$NON-NLS-1$
                + c.getName());
    }

    public DataParsingException(int value, String attributeName, Class c) {
        super("Illegal value " //$NON-NLS-1$
                + Integer.toString(value) + " for attribute " //$NON-NLS-1$
                + attributeName + " in class " //$NON-NLS-1$
                + c.getName());
    }

    public DataParsingException(String attributeName, Class c) {
        super("Unknown attribute " + attributeName + " in class " + c.getName()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public DataParsingException(String xml, short messageType) {
        super(MESSAGES[messageType] + xml);
    }

    public DataParsingException(String value, String attributeName) {
        super("Illegal value " + value + " for attribute " + attributeName); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public DataParsingException(String message) {
        super(message);
    }
}