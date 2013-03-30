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
 * Created on 22.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.log;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;

import eniac.LifecycleListener;
import eniac.Manager;
import eniac.lang.Dictionary;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StringConverter;
import eniac.window.EFrame;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class LogWindow extends JDialog implements PropertyChangeListener,
        LifecycleListener {

    // reference to logpanel
    private LogPanel _logPanel = null;

    // ///////////////////////////// singleton stuff
    // ////////////////////////////

    // singleton self reference
    private static LogWindow instance;

    // private constructor
    private LogWindow() {
        super(EFrame.getInstance(), false);
    }

    public static LogWindow getInstance() {
        if (instance == null) {
            instance = new LogWindow();
            instance.init();
        }
        return instance;
    }

    private void init() {

        // add as singleton to starter
        Manager.getInstance().addMainListener(this);

        // layout
        setTitle(Dictionary.LOG_WINDOW_TITLE);
        _logPanel = Log.getInstance().getLogPanel();
        setContentPane(_logPanel);

        // add propertyChangeListener
        Status.getInstance().addListener(this);

        // add windowListener
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Status.set("show_log", false);
            }
        });

        // to the screen
        setBounds(StringConverter.toRectangle(EProperties.getInstance()
                .getProperty("LOG_WINDOW_BOUNDS")));
        setVisible((Boolean)Status.get("show_log"));
    }

    // /////////////////////////// listener stuff
    // ///////////////////////////////

    public void propertyChange(PropertyChangeEvent evt) {

        if (evt.getPropertyName().equals("show_log")) {
            // show_log changed. Show or hide log window
            setVisible((Boolean)Status.get("show_log"));

        } else if (evt.getPropertyName().equals("language")) {
            // language changed. update window title
            setTitle(Dictionary.LOG_WINDOW_TITLE);
        }
    }

    /**
     * @param oldVal
     * @param newVal
     * @see eniac.LifecycleListener#mainChanged(short, short)
     */
    public void runLevelChanged(short oldVal, short newVal) {
        if (newVal == Manager.STATE_DESTROYED) {
            instance = null;
        }
    }
}
