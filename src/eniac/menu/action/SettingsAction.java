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
 * Created on 02.05.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Vector;

import eniac.Manager;
import eniac.lang.Dictionary;
import eniac.menu.action.gui.SettingsPanel;
import eniac.util.EProperties;

/**
 * @author zoppke
 */
public class SettingsAction extends EAction {

    /**
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        // create dialog that displays information
        SettingsPanel panel = new SettingsPanel();
        panel.init();
        Manager.getInstance().makeDialog(panel, Dictionary.SETTINGS_NAME);

        // dialog closed.
        // If ok pressed, save changes
        if (panel.isOkPressed()) {
            Iterator iter = panel.getDataVector().iterator();
            while (iter.hasNext()) {
                Vector row = (Vector) iter.next();
                String key = (String) row.get(0);
                String value = (String) row.get(1);
                EProperties.getInstance().setProperty(key, value);
            }
        }
    }
}
