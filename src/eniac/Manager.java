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
package eniac;

import java.applet.Applet;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import eniac.data.io.ConfigIO;
import eniac.data.model.parent.Configuration;
import eniac.data.type.TypeHandler;
import eniac.io.Progressor;
import eniac.lang.DictionaryIO;
import eniac.log.LogWindow;
import eniac.menu.action.gui.DialogPanel;
import eniac.skin.SkinIO;
import eniac.util.Status;
import eniac.window.EFrame;

/**
 * The main class of this project and the one extending the JApplet. So on
 * start-up the (empty) constructor is called by the browser, and then the
 * methods init() and start(). If you start the program as an application, the
 * main(String[]) method does this instead. <br>
 * <br>
 * The applet's property of participating in the java.awt.component-hierarchy is
 * neglected. The applet doesn't display any content itself, but it opens an
 * {@link eniac.window.EFrame} for this purpose. So the applet's size should be
 * like 1x1 or 0x0 in order to hide its defaulty greyness. <br>
 * <br>
 * This class principally implements the singleton pattern (just the constructor
 * is public because as an applet it has to), There is a couple of other
 * singleton classes in this project. These classes implement the
 * {@link eniac.LifecycleListener} interface. Their instances can be added as
 * listeners to the Main-instance in order to be informed about the applet's
 * lifecycle state. <br>
 * <br>
 * The applet's life cycles through 7 runlevels.
 * 
 * @author zoppke
 */
public class Manager {

	/*
	 * ========================= applet lifecycle states =======================
	 */

	public enum LifeCycle {

		/**
		 * Default lifecycle state on startup
		 */
		DEFAULT,

		/**
		 * Livecycle state indicating a successful initialization
		 */
		INITIALIZED,

		/**
		 * Lifecycle state indicating that the application is running and the
		 * gui expects input.
		 */
		RUNNING,

		/**
		 * Lifecycle state indicating that the application is running but the
		 * gui is blocked.
		 */
		BLOCKED,

		/**
		 * Lifecycle state indicating that the application is stopped.
		 */
		STOPPED,

		/**
		 * Lifecycle state indicating that the application is destroyed.
		 */
		DESTROYED,
	}

	/*
	 * =============================== fields ==================================
	 */

	// flag indicating, whether we have privileged local file access
	private boolean _ioAccess;

	// reference to the applet. Stays null, if started as application.
	private Applet _applet = null;

	/*
	 * ========================== singleton stuff ==============================
	 */

	private Manager() {
		// empty constructor
	}

	// singleton self reference
	private static Manager instance = null;

	public static Manager getInstance() {
		if (instance == null) {
			instance = new Manager();
		}
		return instance;
	}

	/*
	 * =================== lifecycle state transitions =========================
	 */

	/**
	 * creates and initializes all instances
	 */
	public void init() {
		Status.initValues();
		DictionaryIO.loadDefaultLanguage();
		SkinIO.loadDefaultSkin();
		Status.LIFECYCLE.setValue(LifeCycle.INITIALIZED);
	}

	public void start() {

		// loading the configuration may open an input dialog for the user.
		// as the java-plugin may expect this method to return quickly,
		// we start a new thread.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				// load types
				TypeHandler.loadTypes();

				// load default configuration
				ConfigIO.loadDefaultConfiguration();

				// check, if we haven't been interrupted
				if (Status.LIFECYCLE.getValue() == LifeCycle.INITIALIZED) {

					// open eframe and adjust runlevel
					EFrame.getInstance().toScreen();
					LogWindow.getInstance();
					Status.LIFECYCLE.setValue(LifeCycle.RUNNING);
				}
			}
		});
	}

	public void stop() {

		// TODO: check, if we need to block first

		// dispose configuration
		Configuration config = (Configuration) Status.CONFIGURATION.getValue();
		if (config != null) {
			config.dispose();
		}
		Status.CONFIGURATION.setValue(null);

		// announce that applet is shutting down
		Status.LIFECYCLE.setValue(LifeCycle.STOPPED);
	}

	public void destroy() {

		// check that we haven't been destroyed before
		if (Status.LIFECYCLE.getValue() != LifeCycle.STOPPED) {
			return;
		}

		// run finalization.
		// Though it probably has no effect, the purpose provides good fortune.
		System.runFinalization();

		// announce that applet is destroyed
		Status.LIFECYCLE.setValue(LifeCycle.DESTROYED);
	}

	/*
	 * ============================== methods ==================================
	 */

	public void setApplet(Applet applet) {
		_applet = applet;
	}

	public void makeDialog(DialogPanel content, String title) {
		// create dialog. Add listener and set content pane
		final JDialog dialog = new JDialog(Progressor.getInstance(), title, true);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				((DialogPanel) dialog.getContentPane()).performCancelAction();
			}
		});
		dialog.setContentPane(content);

		// bring it to the screen.
		dialog.pack();
		dialog.setLocationRelativeTo(Progressor.getInstance());
		content.setWindow(dialog);
		dialog.setVisible(true);
	}

	/*
	 * public boolean isPrivileged() { // if we are started as an application,
	 * we have all privileges if (!isApplet()) { return true; } // check, if we
	 * already tried to set our SecurityManager if (_privileged != null) {
	 * return _privileged.booleanValue(); } // This is an applet. Ask for
	 * priviliges. try { // anonymous security manager granting any permission
	 * new SecurityManager() { public void checkPermission(Permission
	 * permission) { // grant any permission }
	 * 
	 * public void checkPermission(Permission permission, Object obj) { // grant
	 * any permission } }; // user allowed the signed applet. Set flag.
	 * _privileged = new Boolean(true); return true; } catch
	 * (AccessControlException ace) { // User didn't allow the signed applet. //
	 * Reset flag and display message. // ace.printStackTrace();
	 * Log.log(LogWords.NO_PRIVILEGES_GRANTED, JOptionPane.INFORMATION_MESSAGE,
	 * ace.getMessage(), true); _privileged = new Boolean(false); return false;
	 * } }
	 */
	// indicates whether this program is started as applet or as application.
	// private boolean isApplet() {
	// return getAppletContext() == null;
	// }
	public void block() {
		if (Status.LIFECYCLE.getValue() == LifeCycle.RUNNING) {
			Status.LIFECYCLE.setValue(LifeCycle.BLOCKED);
		}
	}

	public void unblock() {
		if (Status.LIFECYCLE.getValue() == LifeCycle.BLOCKED) {
			Status.LIFECYCLE.setValue(LifeCycle.RUNNING);
		}
	}

	// ============================ io methods //===============================

	/**
	 * Opens an <code>inputStream</code> to the specified resource.
	 * 
	 * @param name
	 *            a <code>string</code> specifying the resource to load
	 * @return an <code>inputStream</code> if the resource could be found, <br>
	 *         or <code>null</code> otherwise
	 */
	public InputStream getResourceAsStream(String name) {
		return Manager.class.getClassLoader().getResourceAsStream(name);
	}

	public void setIOAccess(boolean ioAccess) {
		_ioAccess = ioAccess;
	}

	public boolean hasIOAccess() {
		return _ioAccess;
	}
}
