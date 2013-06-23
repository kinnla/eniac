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
 * Created on 20.02.2004
 */
package eniac.log;

import javax.swing.JOptionPane;

/**
 * @author zoppke
 */
public class LogMessage {

	// ================================ fields
	// ==================================

	private String _message;

	private int _type;

	private Object[] _objects;

	private boolean _forUser;

	// ============================= lifecycle
	// ==================================

	public LogMessage(String message, int type, Object[] objects, boolean forUser) {

		_message = message;
		_type = type;
		_objects = objects;
		_forUser = forUser;
	}

	public LogMessage(String message, int type, boolean forUser) {
		this(message, type, null, forUser);
	}

	public LogMessage(String message, int type) {
		this(message, type, null, false);
	}

	public LogMessage(String message) {
		this(message, JOptionPane.PLAIN_MESSAGE, null, false);
	}

	// ============================== methods
	// ===================================

	public String toString() {
		String s = typeToString(_type) + ": " + _message; //$NON-NLS-1$
		if (_objects != null) {
			s += ":"; //$NON-NLS-1$
			for (int i = 0; i < _objects.length; ++i) {
				s = s + " [" + _objects[i] + "]"; //$NON-NLS-1$//$NON-NLS-2$
			}
		}
		return s;
	}

	public static String typeToString(int type) {
		switch (type) {
			case JOptionPane.PLAIN_MESSAGE :
				return ""; //$NON-NLS-1$
			case JOptionPane.ERROR_MESSAGE :
				return "ERROR"; //$NON-NLS-1$
			case JOptionPane.INFORMATION_MESSAGE :
				return "Information"; //$NON-NLS-1$
			case JOptionPane.QUESTION_MESSAGE :
				return "Question"; //$NON-NLS-1$
			case JOptionPane.WARNING_MESSAGE :
				return "Warning"; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

	// ================================ getter
	// ==================================

	public boolean isForUser() {
		return _forUser;
	}

	public String getMessage() {
		return _message;
	}

	public String getTitle() {
		return typeToString(_type);
	}

	public int getType() {
		return _type;
	}
}
