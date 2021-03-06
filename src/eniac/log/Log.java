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
 * Created on 10.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.log;

import java.util.LinkedList;
import java.util.List;

import eniac.Manager;
import eniac.util.Status;
import eniac.util.StatusListener;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Log implements StatusListener {

	// containing all text that was passed to this logger
	private StringBuilder _stringBuilder = new StringBuilder();

	// vector containing all registered logListeners
	private List<LogListener> _logListeners = new LinkedList<>();

	// reference to LogPanel
	private LogPanel _logPanel = null;

	// ========================== singleton stuff
	// ===============================

	// self reference
	private static Log instance = null;

	private Log() {
		// empty
	}

	public static synchronized Log getInstance() {
		if (instance == null) {
			instance = new Log();
			instance.init();
		}
		return instance;
	}

	private void init() {
		Status.LIFECYCLE.addListener(this);
		addLogListener(new ConsolePrinter());
		addLogListener(new DialogPrinter());
	}

	// ============================ methods
	// =====================================

	/**
	 * @return
	 */
	public String getText() {
		return _stringBuilder.toString();
	}

	/**
	 * adds a line to this stringbuffer
	 * 
	 * @param line
	 *            A string to be added to this logger
	 */
	private void addMessage(LogMessage message) {
		_stringBuilder.append(message);
		_stringBuilder.append('\n');

		// inform listeners
		for (LogListener listener : _logListeners) {
			listener.incomingMessage(message);
		}
	}

	/**
	 * empties the Logger.
	 */
	public void clear() {
		_stringBuilder = new StringBuilder();

		// inform listeners
		for (LogListener listener : _logListeners) {
			listener.cleared();
		}
	}

	/**
	 * Registers a logListener at this logger
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public void addLogListener(LogListener listener) {
		_logListeners.add(listener);
	}

	/**
	 * Removes a logListener from this logger
	 * 
	 * @param listener
	 *            The listener to be removed
	 */
	public void removeLogListener(LogListener listener) {
		_logListeners.remove(listener);
	}

	/**
	 * @return
	 */
	public LogPanel getLogPanel() {
		if (_logPanel == null) {
			_logPanel = new LogPanel();
			_logPanel.init();
			addLogListener(_logPanel);
		}
		return _logPanel;
	}

	// ======================== convenience methods
	// =============================

	public static void log(String message) {
		getInstance().addMessage(new LogMessage(message));
	}

	public static void log(String message, int type) {
		getInstance().addMessage(new LogMessage(message, type));
	}

	public static void log(String message, int type, Object[] objects) {
		getInstance().addMessage(new LogMessage(message, type, objects, false));
	}

	public static void log(String message, int type, Object object) {
		getInstance().addMessage(new LogMessage(message, type, new Object[]{object}, false));
	}

	public static void log(String message, int type, Object object, boolean forUser) {

		getInstance().addMessage(new LogMessage(message, type, new Object[]{object}, forUser));
	}

	public static void log(String message, int type, boolean forUser) {
		getInstance().addMessage(new LogMessage(message, type, forUser));
	}

	@Override
	public void statusChanged(Status status, Object newValue) {
		if (newValue == Manager.LifeCycle.DESTROYED) {
			instance = null;
		}
	}
}
