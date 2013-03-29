/*
 * Created on 11.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.log;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class LogPanel extends JScrollPane implements LogListener {

    private JTextArea _jtextArea = new JTextArea();

    public LogPanel() {
        // empty
    }

    public void init() {
        setViewportView(_jtextArea);
        _jtextArea.setEditable(false);
        _jtextArea.setText(Log.getInstance().getText());
    }

    /**
     * This method is called, when a line has been added to the logger
     * 
     * @param line
     *            The line that was added
     */
    public void incomingMessage(LogMessage message) {
        _jtextArea.append(message.toString());
        _jtextArea.append("\n"); //$NON-NLS-1$
    }

    /**
     * This method is called, when the logger is cleared.
     *  
     */
    public void cleared() {
        _jtextArea.setText(""); //$NON-NLS-1$
    }

}