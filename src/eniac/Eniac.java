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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessControlException;
import java.security.Permission;

import javax.swing.JOptionPane;

import eniac.log.Log;
import eniac.log.LogWords;
import eniac.util.EProperties;

public class Eniac extends Applet {

	public Eniac() {
		// constructor to be called by the browser
	}

	public void init() {

		// recurse on properties
		EProperties pts = EProperties.getInstance();
		for (String key : pts.stringPropertyNames()) {

			// try to find a parameter with the property's key
			String value = getParameter(key);
			if (value != null) {

				// set property
				pts.setProperty(key, value);
			}
		}

		// check, if we are started as signed applet
		if (pts.getProperty("RUN_TYPE").equals("APPLET_SIGNED")) {

			// signed applet.
			// ask user to grant io-permission by accepting our certificate.
			try {
				// anonymous security manager granting any permission
				new SecurityManager() {
					public void checkPermission(Permission permission) {
						// grant any permission
					}

					public void checkPermission(Permission permission, Object obj) {
						// grant any permission
					}
				};
				// user accepted our certificate. set io access flag.
				Manager.getInstance().setIOAccess(true);

			} catch (AccessControlException ace) {
				// user didn't accept our certificate.
				// display message and reset io access flag.
				Log.log(LogWords.NO_PRIVILEGES_GRANTED, JOptionPane.INFORMATION_MESSAGE, ace.getMessage(), true);
				Manager.getInstance().setIOAccess(false);
			}
		}
		else if (pts.getProperty("RUN_TYPE").equals("APPLET_UNSIGNED")) {

			// unsigned applet. reset io access flag
			Manager.getInstance().setIOAccess(false);
			Manager.getInstance().setApplet(this);
		}
		else {
			System.out.println("unknown RUN_TYPE");
		}

		// init manager
		Manager.getInstance().init();
	}

	public void start() {
		Manager.getInstance().start();
	}

	public void stop() {
		Manager.getInstance().stop();
	}

	public void destroy() {
		Manager.getInstance().destroy();
	}

	public InputStream openStream(String file) {
		System.out.println("Eniac.openStream()");

		// check the run type
		if (EProperties.getInstance().getProperty("RUN_TYPE").equals("APPLICATION")) {

			// we are running as application. So open a file input stream.
			try {
				return new FileInputStream(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {

			// we are running as applet. so open URL input stream
			try {
				// Return an urlstream
				URL url = new URL(getCodeBase(), file);
				return url.openStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// file not found, return null
		return null;
	}

	/**
	 * main method to start the simulation as application.
	 * 
	 * @param args
	 *            an array of property=value pairs that are passed to the
	 *            application. Such a property in the properties file can be
	 *            overwritten, e.g. "SHOW_OVERVIEW=false"
	 */
	public static void main(String[] args) {

		// init properties
		EProperties properties = EProperties.getInstance();

		// recurse on list of arguments
		for (int i = 0; i < args.length; ++i) {

			// parse key and value
			String[] keyVal = args[i].split("=");

			// if argument is not known as property name, announce to the user
			if (!properties.containsKey(keyVal[0])) {
				System.out.println("Warning: Property \"" + keyVal[0] + "\" is unknown. Mistyping?");
			}

			// set property
			properties.setProperty(keyVal[0], keyVal[1]);
		}

		// create manager, set IO access, init and start.
		Manager.getInstance().setIOAccess(true);
		Manager.getInstance().init();
		Manager.getInstance().start();
	}
}
