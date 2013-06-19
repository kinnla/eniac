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
 * Created on 23.02.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;

import eniac.Manager;
import eniac.io.Proxy;
import eniac.lang.Dictionary;
import eniac.menu.action.gui.OpenSkinPanel;
import eniac.skin.SkinIO;

/**
 * @author zoppke
 */
public class OpenSkin extends EAction implements Runnable {

    public void actionPerformed(ActionEvent evt) {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void run() {

        // announce that we are working
        Manager.getInstance().block();

        // scan for proxies
        Proxy[] proxies = SkinIO.loadProxies();

        // create dialog that user can choose a configDescriptor
        OpenSkinPanel panel = new OpenSkinPanel(proxies);
        panel.init();
        Manager.getInstance().makeDialog(panel, Dictionary.OPEN_SKIN_NAME.getText());

        // if ok, open skin
        Proxy proxy = panel.getSelectedProxy();
        if (proxy != null) {
            SkinIO.loadSkin(proxy);
        }

        // announce that work is done
        Manager.getInstance().unblock();
    }
}
