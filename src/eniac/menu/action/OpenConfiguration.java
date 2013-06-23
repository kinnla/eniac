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
 * Created on 10.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;
import java.util.List;

import eniac.Manager;
import eniac.data.io.ConfigIO;
import eniac.io.Proxy;
import eniac.lang.Dictionary;
import eniac.menu.action.gui.OpenConfigurationPanel;
import eniac.util.Status;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OpenConfiguration extends EAction implements Runnable {

	public void actionPerformed(ActionEvent evt) {
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {

		// announce that we are busy now
		Manager.getInstance().block();

		// scan for proxies
		List<Proxy> proxies = ConfigIO.loadProxies();

		// create dialog that user can choose a configDescriptor
		OpenConfigurationPanel panel = new OpenConfigurationPanel(proxies);
		panel.init();
		// Main.getInstance().setOpenConfigurationPanel(panel);
		Manager.getInstance().makeDialog(panel, Dictionary.OPEN_CONFIGURATION_NAME.getText());

		// if we are already stopping, we don't need to load a configuration.
		if (Manager.getInstance() == null || Status.LIFECYCLE.getValue() == Manager.LifeCycle.STATE_STOPPED
				|| Status.LIFECYCLE.getValue() == Manager.LifeCycle.STATE_DESTROYED) {
			return;
		}

		// switch on input result
		switch (panel.getConfiguraionType()) {

		// load basic configuration
			case OpenConfigurationPanel.BASIC :
				Proxy proxy = panel.getProxy();
				ConfigIO.loadConfiguration(proxy);
				break;

			// load local configuration
			case OpenConfigurationPanel.LOCAL :
				String path = panel.getCanonicalPath();
				ConfigIO.loadConfiguration(path);
				break;

			// don't load any configuration
			case OpenConfigurationPanel.NO_CONFIGURATION :
				break;
		}

		// announce that job is done.
		Manager.getInstance().unblock();
	}
}
