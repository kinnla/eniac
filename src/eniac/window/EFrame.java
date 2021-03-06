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
 * Created on 19.09.2003
 */
package eniac.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeListener;

import eniac.Manager;
import eniac.data.model.parent.Configuration;
import eniac.data.view.parent.ConfigPanel;
import eniac.lang.Dictionary;
import eniac.menu.MenuHandler;
import eniac.menu.action.EAction;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StatusListener;
import eniac.util.StringConverter;

public class EFrame extends JFrame implements StatusListener {

	/*
	 * ========================== private fields ===============================
	 */

	// scrollPane as south component of the contentPane (North component is
	// the actionBar).
	private JScrollPane _scrollPane;

	// configuration panel for display the current configuration of the eniac.
	// This component is child to the scrollPane.
	// If no current configuration, this is null.
	private ConfigPanel _configPanel = null;

	private MediaTracker _mediaTracker;

	/*
	 * ============================ singleton stuff ===========================
	 */

	private static EFrame instance;

	private EFrame() {

		// set bounds.
		Dimension mySize = StringConverter.toDimension(EProperties.getInstance().getProperty("EFRAME_SIZE"));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mySize.width = Math.min(mySize.width, screenSize.width);
		mySize.height = Math.min(mySize.height, screenSize.height);
		setSize(mySize);
		setLocation((screenSize.width - mySize.width) / 2, (screenSize.height - mySize.height) / 3);

		// create media tracker
		_mediaTracker = new MediaTracker(this);

		// add as singleton to starter
		Status.LIFECYCLE.addListener(this);
	}

	public static EFrame getInstance() {
		if (instance == null) {
			instance = new EFrame();
		}
		return instance;
	}

	/**
	 * Initializes this dvFrame. Listener registration is done, the frame is
	 * layouted and finally brought to the screen.
	 */
	public void toScreen() {

		// set window title
		setTitle(Dictionary.MAIN_FRAME_TITLE.getText());

		// window listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Manager.getInstance().stop();
				Manager.getInstance().destroy();
				dispose();
			}
		});

		// init scrollPane
		_scrollPane = new JScrollPane();
		Color c = StringConverter.toColor(EProperties.getInstance().getProperty("BACKGROUND_COLOR"));
		_scrollPane.getViewport().setBackground(c);

		// add components
		// create and init menu handler
		Hashtable<String, EAction> actionDefaults = new Hashtable<>();
		MenuHandler handler = new MenuHandler(actionDefaults);
		handler.init();

		setJMenuBar(handler.getMenuBar());
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(handler.getToolBar(), BorderLayout.NORTH);
		getContentPane().add(_scrollPane, BorderLayout.CENTER);

		// update configurationPanel
		// TODO: find new way of initializing configuration
		updateConfigPanel();

		// add this as propertyChangeListener
		Status.CONFIGURATION.addListener(this);
		Status.SKIN.addListener(this);
		Status.LANGUAGE.addListener(this);

		// init LogWindow
		// LogWindow.getInstance();
		setVisible(true);
	}

	/*
	 * =============================== methods =================================
	 */

	// update config panel. This method is called, when the current
	// configuration changed.
	private void updateConfigPanel() {

		// dispose the old panel if any
		if (_configPanel != null) {
			_scrollPane.setViewportView(null);
			_configPanel.dispose();
		}

		// get current configuration and the viewDimension
		Configuration config = (Configuration) Status.CONFIGURATION.getValue();

		// determine which configPanel to set
		if (config == null) {

			// configuration is null. Set null.
			_configPanel = null;
		}
		else {
			// create new configurationPanel
			_configPanel = (ConfigPanel) config.makePanel();
			// add configPanel to scrollPane and init
			_configPanel.init();
			_scrollPane.setViewportView(_configPanel);
		}

		// tell OVWindow to adjust its panel to the new configPanel
		OVWindow.getInstance().configPanelChanged();
	}

	public int showFileChooser(JFileChooser chooser, String approveButtonText) {
		return chooser.showDialog(this, approveButtonText);
	}

	public ConfigPanel getConfigPanel() {
		return _configPanel;
	}

	/*
	 * ===================== PropertyChangeListener methods ==================
	 */

	/**
	 * This method is called when a status property is changed
	 * 
	 * @param evt
	 *            The propertyEvent indicating that the architecture changed
	 */
	public void statusChanged(Status status, Object newValue) {

		switch (status) {

			case CONFIGURATION :
				// configuration changed. Update the configPanel.
				updateConfigPanel();
				break;
			case SKIN :
				// skin changed. repaint
				repaint();
				break;

			case LANGUAGE :
				// language changed. repaint and adjust title.
				repaint();

				// set default locale to JComponent.
				// So optionPane buttons get the right language.
				JComponent.setDefaultLocale(new Locale((String) newValue));
				setTitle(Dictionary.MAIN_FRAME_TITLE.getText());
				break;

			case LIFECYCLE :
				if (newValue == Manager.LifeCycle.STOPPED) {
					setVisible(false);
				}
				else if (newValue == Manager.LifeCycle.DESTROYED) {
					dispose();
					instance = null;
				}
				break;

			default :
				break;
		}
	}

	/*
	 * ============================ changelistener ===========================
	 */

	public void addChangeListener(ChangeListener listener) {
		_scrollPane.getViewport().addChangeListener(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		_scrollPane.getViewport().removeChangeListener(listener);
	}

	/**
	 * @param path
	 * @return
	 */
	public Image getResourceAsImage(String name) {

		// get url. If path cannot be resolved, return null.
		URL url = Manager.class.getClassLoader().getResource(name);
		if (url == null) {
			return null;
		}

		Image img = Toolkit.getDefaultToolkit().createImage(url);
		_mediaTracker.addImage(img, 1);
		try {
			_mediaTracker.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return img;
	}

}
