/*
 * Created on 02.05.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;

import eniac.Manager;
import eniac.lang.Dictionary;
import eniac.menu.action.gui.TextPanel;
import eniac.util.EProperties;
import eniac.util.StringConverter;

/**
 * @author zoppke
 */
public class Faq extends EAction {

    /**
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        // create dialog that displays information
        TextPanel panel = new TextPanel(Dictionary.FAQ_TEXT);
        panel.init();
        panel.setPreferredSize(StringConverter.toDimension(EProperties.getInstance()
                .getProperty("FAQ_SIZE")));
        Manager.getInstance().makeDialog(panel, Dictionary.FAQ_NAME);
        // dialog closed. Nothing to do any more
    }
}