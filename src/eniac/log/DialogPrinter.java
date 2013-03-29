/*
 * Created on 22.02.2004
 */
package eniac.log;

import javax.swing.JOptionPane;

import eniac.io.Progressor;

/**
 * @author zoppke
 */
public class DialogPrinter implements LogListener {

    /**
     * @param message
     * @see eniac.log.LogListener#incomingMessage(eniac.log.LogMessage)
     */
    public void incomingMessage(LogMessage message) {
        if (message.isForUser()) {

            // show dialog
            JOptionPane.showMessageDialog(Progressor.getInstance(), message
                    .getMessage(), message.getTitle(), message.getType());
        }
    }

    /**
     * 
     * @see eniac.log.LogListener#cleared()
     */
    public void cleared() {
        //nop
    }
}