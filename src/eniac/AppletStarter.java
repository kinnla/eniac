package eniac;

import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessControlException;
import java.security.Permission;
import java.util.Enumeration;

import javax.swing.JOptionPane;

import eniac.log.Log;
import eniac.log.LogWords;
import eniac.util.EProperties;

public class AppletStarter extends Applet implements ResourceProvider {

	public AppletStarter() {
		// constructor to be called by the browser
	}
	
	public void init() {
		// check, whether a second instance still exists.
		// if (instance != null) {
		// System.out.println("another instance!");
		// //instance.stop();
		// //instance.destroy();
		// // JOptionPane.showMessageDialog(this, "Another session of the
		// ENIAC\n" //$NON-NLS-1$
		// // +"Simulation is still alive.\n" //$NON-NLS-1$
		// // +"You cannot run two sessions.\n" //$NON-NLS-1$
		// // +"Please close all windows and restart."); //$NON-NLS-1$
		// // Progressor.getInstance().setVisible(false);
		// // EFrame.getInstance().dispose();
		// }

		// register resource provider
		Manager.getInstance().setResourceProvider(this);

		// check, whether we should overwrite the properties file name
//		String propertiesFile = getParameter("PROPERTIES_FILE");
//		if (propertiesFile != null) {
//			EProperties.fileName = propertiesFile;
//		}
		
		// recurse on properties
		EProperties properties = EProperties.getInstance();
		Enumeration en = properties.propertyNames();
		while (en.hasMoreElements()) {

			// try to find a parameter with the property's key
			String key = (String) en.nextElement();
			String value = getParameter(key);
			if (value != null) {

				// set property
				properties.setProperty(key, value);
			}
		}

		// check, if we are started as signed applet
		if (properties.getProperty("RUN_TYPE").equals("APPLET_SIGNED")) {

			// signed applet.
			// ask user to grant io-permission by accepting our certificate.
			try {
				// anonymous security manager granting any permission
				new SecurityManager() {
					public void checkPermission(Permission permission) {
						// grant any permission
					}

					public void checkPermission(Permission permission,
							Object obj) {
						// grant any permission
					}
				};
				// user accepted our certificate. set io access flag.
				Manager.getInstance().setIOAccess(true);

			} catch (AccessControlException ace) {
				// user didn't accept our certificate.
				// display message and reset io access flag.
				Log
						.log(LogWords.NO_PRIVILEGES_GRANTED,
								JOptionPane.INFORMATION_MESSAGE, ace
										.getMessage(), true);
				Manager.getInstance().setIOAccess(false);
			}
		} else {

			// unsigned applet. reset io access flag
			Manager.getInstance().setIOAccess(false);
			Manager.getInstance().setResourceProvider(this);
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
		System.out.println("AppletStarter.openStream()");
		try {
			// Return an urlstream 
			URL url = new URL(getCodeBase(), file);
			return url.openStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// file not found, return null
		return null;
	}
}
