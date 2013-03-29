/*
 * Created on 23.02.2004
 */
package eniac.menu.action.gui;

import java.awt.LayoutManager;
import java.awt.Window;

import javax.swing.JPanel;

/**
 * @author zoppke
 */
public abstract class DialogPanel extends JPanel {

    protected Window _window = null;

    public DialogPanel(LayoutManager lm) {
        super(lm);
    }

    public abstract void performCancelAction();

    public void setWindow(Window window) {
        _window = window;
    }

}