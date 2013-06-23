/*******************************************************************************
 * Copyright (c) 2003-2005, 2013 Till Zoppke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Till Zoppke - initial API and implementation
 ******************************************************************************/
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
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DataParsingException extends RuntimeException {

	private static final String[] MESSAGES = {"Unknown tag: ", " missing attribute: "}; //$NON-NLS-1$ //$NON-NLS-2$

	public static final short UNKNOWN_TAG = 0;

	public static final short MISSING_ATTRIBUTE = 1;

	// ============================ constructors
	// ================================

	public DataParsingException(Exception e) {
		super(e.getClass().getName() + ": " + e.getMessage()); //$NON-NLS-1$
	}

	public DataParsingException(String key, Enum<?> tag, Class<?> c) {
		super("Unknown key " //$NON-NLS-1$
				+ key + " for attribute " //$NON-NLS-1$
				+ tag + " in class " //$NON-NLS-1$
				+ c.getName());
	}

	public DataParsingException(int value, Enum<?> tag, Class<?> c) {
		super("Illegal value " //$NON-NLS-1$
				+ Integer.toString(value) + " for attribute " //$NON-NLS-1$
				+ tag + " in class " //$NON-NLS-1$
				+ c.getName());
	}

	public DataParsingException(String attributeName, Class<?> c) {
		super("Unknown attribute " + attributeName + " in class " + c.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public DataParsingException(Enum<?> tag, short messageType) {
		super(MESSAGES[messageType] + tag);
	}

	public DataParsingException(String value, Enum<?> tag) {
		super("Illegal value " + value + " for attribute " + tag); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public DataParsingException(String message) {
		super(message);
	}
}
