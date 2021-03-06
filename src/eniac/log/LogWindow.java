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
 * Created on 22.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.log;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import eniac.lang.Dictionary;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StatusListener;
import eniac.util.StringConverter;
import eniac.window.EFrame;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class LogWindow extends JDialog {

	// reference to logpanel
	private LogPanel _logPanel = null;

	// ============================= singleton stuff
	// ============================

	// singleton self reference
	private static LogWindow instance;

	// private constructor
	private LogWindow() {
		super(EFrame.getInstance(), false);
	}

	public static LogWindow getInstance() {
		if (instance == null) {
			instance = new LogWindow();
			instance.init();
		}
		return instance;
	}

	private void init() {

		// layout
		setTitle(Dictionary.LOG_WINDOW_TITLE.getText());
		_logPanel = Log.getInstance().getLogPanel();
		setContentPane(_logPanel);

		// add status Listener for visibility
		Status.SHOW_LOG.addListener(new StatusListener() {

			@Override
			public void statusChanged(Status status, Object newValue) {
				// show_log changed. Show or hide log window
				setVisible((boolean) newValue);
			}
		});

		// add status listener for language
		Status.LANGUAGE.addListener(new StatusListener() {

			@Override
			public void statusChanged(Status status, Object newValue) {
				// language changed. update window title
				setTitle(Dictionary.LOG_WINDOW_TITLE.getText());
			}
		});

		// add windowListener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Status.SHOW_LOG.setValue(false);
			}
		});

		// to the screen
		setBounds(StringConverter.toRectangle(EProperties.getInstance().getProperty("LOG_WINDOW_BOUNDS")));
		setVisible((boolean) Status.SHOW_LOG.getValue());
	}
}
