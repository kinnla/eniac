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
package eniac.window;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import eniac.data.view.parent.ConfigPanel;
import eniac.lang.Dictionary;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StatusListener;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OVWindow extends JDialog {

	private OVPanel _ovPanel = null;

	// =========================== singleton stuff
	// ==============================

	private static OVWindow instance;

	private OVWindow() {
		super(EFrame.getInstance(), Dictionary.OVERVIEW_WINDOW_TITLE.getText(), false);
	}

	public static OVWindow getInstance() {
		if (instance == null) {
			instance = new OVWindow();
			instance.init();
		}
		return instance;
	}

	/**
	 * initializes this overviewWindow.
	 */
	private void init() {

		// init configPanel first
		configPanelChanged();

		// register as propertyChangeListener
		Status.SHOW_OVERVIEW.addListener(new StatusListener() {

			@Override
			public void statusChanged(Status status, Object newValue) {
				// show overview toggeled
				setVisible((Boolean) newValue);
			}
		});

		Status.LANGUAGE.addListener(new StatusListener() {

			@Override
			public void statusChanged(Status status, Object newValue) {
				// language changed. update window title
				setTitle(Dictionary.OVERVIEW_WINDOW_TITLE.getText());
			}
		});

		// add WindowListener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Status.SHOW_OVERVIEW.setValue(false);
			}
		});

		// to the screen
		pack();
		setLocation(StringConverter.toPoint(EProperties.getInstance().getProperty("OVERVIEW_WINDOW_LOCATION")));
		setVisible((boolean) Status.SHOW_OVERVIEW.getValue());
	}

	public void dispose() {
		super.dispose();
		instance = null;
	}

	// =============================== methods
	// ==================================

	/**
	 * initializes an overviewPane according to the actual view-dimension and
	 * the actual configurationPanel
	 */
	public void configPanelChanged() {

		// dispose old panel, if an overviewPanel
		if (_ovPanel != null) {
			_ovPanel.dispose();
		}

		// determine, which overviewPanel to set
		ConfigPanel configPanel = EFrame.getInstance().getConfigPanel();
		if (configPanel != null) {
			_ovPanel = new OVPanel();
			_ovPanel.init();
			setContentPane(_ovPanel);
		}
		// layout
		pack();
	}

	public OVPanel getOVPanel() {
		return _ovPanel;
	}
}
