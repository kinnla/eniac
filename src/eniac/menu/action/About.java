/*
 * Created on 02.05.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;

import eniac.Manager;
import eniac.lang.Dictionary;
import eniac.menu.action.gui.TextPanel;

/**
 * @author zoppke
 */
public class About extends EAction {

    /**
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        // create dialog that displays information
        TextPanel panel = new TextPanel(Dictionary.ABOUT_TEXT);
        panel.init();
        Manager.getInstance().makeDialog(panel, Dictionary.ABOUT_NAME);
        // dialog closed. Nothing to do any more
    }
}