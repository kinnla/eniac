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
 * Created on 11.11.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import eniac.Manager;
import eniac.data.io.ConfigIO;
import eniac.data.model.parent.Configuration;
import eniac.io.IOUtil;
import eniac.io.Proxy;
import eniac.lang.Dictionary;
import eniac.log.Log;
import eniac.log.LogWords;
import eniac.menu.action.gui.SaveConfigurationPanel;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StatusMap;
import eniac.window.EFrame;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class SaveConfiguration extends EAction implements
        PropertyChangeListener, Runnable {

	public void actionPerformed(ActionEvent e) {
        // check for privileges
        if (Manager.getInstance().hasIOAccess()) {
            Thread t = new Thread(this);
            t.start();
            // We have privileges to write file to users file system
        } else {
            // we have no privileges to write.
            // display message to tell user
            Log.log(LogWords.CANNOT_SAVE_FILE, JOptionPane.INFORMATION_MESSAGE,
                    true);
        }
    }

    // starts a wizard with dialogs for enter details of configuration
    // and a filechooser for configuration target.
    public void run() {

        Manager.getInstance().block();

        // create dialog that user can enter name and description
        SaveConfigurationPanel panel = new SaveConfigurationPanel();
        panel.init();
        Manager.getInstance().makeDialog(panel,
                Dictionary.SAVE_CONFIGURATION_TITLE.getText());

        // if canceled, do nothing.
        if (!panel.isNextPressed()) {
            Manager.getInstance().unblock();
            return;
        }

        // otherwise create proxy
        Proxy proxy = new Proxy();
        proxy.put(Proxy.Tag.NAME, panel.getName());
        proxy.put(Proxy.Tag.DESCRIPTION, panel.getDescription());

        // create filechooser
        JFileChooser chooser = new JFileChooser();

        // get default filename without index
        String s = chooser.getFileSystemView().getDefaultDirectory()
                .getAbsolutePath();
        s += File.separator;
        s += EProperties.getInstance().getProperty("DEFAULT_FILE_WITHOUT_NUMBER");

        // determine lowest index that the file doesn't exist
        File file;
        int i = -1;
        do {
            file = new File(IOUtil.addIndex(s, ++i));
        } while (file.exists());

        // create dialog that user can choose path to save
        chooser.setFileFilter(IOUtil.getFileFilter());
        chooser.setSelectedFile(file);
        int returnVal = EFrame.getInstance().showFileChooser(chooser,
                Dictionary.WRITE.getText());

        // check if user wants to write
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            // try to write to the file specified by filechooser
            try {
                Configuration config = (Configuration) StatusMap
                        .get(Status.CONFIGURATION);
                ConfigIO.write(chooser.getSelectedFile(), config, proxy);
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
        Manager.getInstance().unblock();
    }

    // ============================== enabling
    // ==================================

    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("configuration")) {
            setEnabled(e.getNewValue() != null);
        } else {
            super.propertyChange(e);
        }
    }
}
