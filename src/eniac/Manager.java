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
package eniac;

import java.applet.Applet;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import eniac.data.io.ConfigIO;
import eniac.data.model.parent.Configuration;
import eniac.data.type.TypeHandler;
import eniac.io.Progressor;
import eniac.lang.DictionaryIO;
import eniac.log.LogWindow;
import eniac.menu.action.gui.DialogPanel;
import eniac.skin.SkinIO;
import eniac.util.Status;
import eniac.util.StatusMap;
import eniac.window.EFrame;

/**
 * The main class of this project and the one extending the JApplet. So on
 * start-up the (empty) constructor is called by the browser, and then the
 * methods init() and start(). If you start the program as an application, the
 * main(String[]) method does this instead. <br>
 * <br>
 * The applet's property of participating in the java.awt.component-hierarchy is
 * neglected. The applet doesn't display any content itself, but it opens an
 * {@link eniac.window.EFrame} for this purpose. So the applet's size should be
 * like 1x1 or 0x0 in order to hide its defaulty greyness. <br>
 * <br>
 * This class principally implements the singleton pattern (just the constructor
 * is public because as an applet it has to), There is a couple of other
 * singleton classes in this project. These classes implement the
 * {@link eniac.LifecycleListener} interface. Their instances can be added as
 * listeners to the Main-instance in order to be informed about the applet's
 * lifecycle state. <br>
 * <br>
 * The applet's life cycles through 7 runlevels.
 * 
 * @author zoppke
 */
public class Manager {

    /*
     * ========================= applet lifecycle states =======================
     */

	
    /**
     * Default lifecycle state on startup
     */
    public static final short STATE_DEFAULT = 0;

    /**
     * Livecycle state indicating a successful initialization
     */
    public static final short STATE_INITIALIZED = 1;

    /**
     * Lifecycle state indicating that the application is running and the gui
     * expects input.
     */
    public static final short STATE_RUNNING = 2;

    /**
     * Lifecycle state indicating that the application is running but the gui is
     * blocked.
     */
    public static final short STATE_BLOCKED = 3;

    /**
     * Lifecycle state indicating that the application is stopped.
     */
    public static final short STATE_STOPPED = 4;

    /**
     * Lifecycle state indicating that the application is destroyed.
     */
    public static final short STATE_DESTROYED = 5;

    // START_UP = 0, // default state on startup
    // INITIALIZING = 1, // singleton instance is set
    // IDLE = 2, // applet is running in idle mode
    // BUSY = 3, // applet is running but busy
    // STOPPING = 4, // applet is shutting down
    // DESTROYING = 5, // applet is getting destroyed
    // DESTROYED = 6; // applet is destroyed

    /*
     * =============================== fields ==================================
     */

    // flag indicating, whether we have privileged local file access
    private boolean _ioAccess;
    
    // reference to the applet. Stays null, if started as application. 
    private Applet _applet = null;

    // list of mainListeners to be informed when run level changes
    private List<LifecycleListener> _lifecycleListeners = new LinkedList<>();

    // encoding the current lifecycle state
    short _lifecycleState = STATE_DEFAULT;

    /*
     * ========================== singleton stuff ==============================
     */

    private Manager() {
        // empty constructor
    }

    // singleton self reference
    private static Manager instance = null;

    public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    /*
     * =================== lifecycle state transitions =========================
     */

    /**
     * creates and initializes all instances
     */
    public void init() {

        // check lifecycle state
        if (_lifecycleState != STATE_DEFAULT) {
            System.out.println("unallowed call of init() at state "
                    + _lifecycleState);
            return;
        }

        // init defaults
        DictionaryIO.loadDefaultLanguage();
        SkinIO.loadDefaultSkin();

        setLifecycleState(STATE_INITIALIZED);
    }

    public void start() {

        // check lifecycle state
        if (_lifecycleState != STATE_INITIALIZED) {
            System.out.println("illegal call of start() at state "
                    + _lifecycleState);
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

            	// load types
            	TypeHandler.loadTypes();
            	
                // load default configuration
                ConfigIO.loadDefaultConfiguration();

                // check, if we haven't been interrupted
                if (_lifecycleState == STATE_INITIALIZED) {

                    // open eframe and adjust runlevel
                    EFrame.getInstance().toScreen();
                    LogWindow.getInstance();
                    setLifecycleState(STATE_RUNNING);
                }
            }
        });
    }

    public void stop() {

        // check lifecycle state
        if (_lifecycleState != STATE_RUNNING) {
            System.out.println("illegal call of start() at state "
                    + _lifecycleState);
            return;
        }

        // TODO: check, if we need to block first

        // dispose configuration
        Configuration config = (Configuration) StatusMap.get(Status.CONFIGURATION);
        if (config != null) {
            config.dispose();
        }
        StatusMap.set(Status.CONFIGURATION, null);

        // announce that applet is shutting down
        setLifecycleState(STATE_STOPPED);
    }

    public void destroy() {

        // check that we haven't been destroyed before
        if (_lifecycleState != STATE_STOPPED) {
            return;
        }

        // dispose Main
        _lifecycleListeners.clear();

        // run finalization.
        // Though it probably has no effect, the purpose provides good fortune.
        System.runFinalization();

        // announce that applet is destroyed
        setLifecycleState(STATE_DESTROYED);
    }

    /*
     * ============================== methods ==================================
     */

    public void setApplet(Applet applet) {
    	_applet = applet;
    }
    
    public void addMainListener(LifecycleListener listener) {
        _lifecycleListeners.add(listener);
    }

    public short getLifecycleState() {
        return _lifecycleState;
    }

    public void makeDialog(DialogPanel content, String title) {
        // create dialog. Add listener and set content pane
        final JDialog dialog = new JDialog(Progressor.getInstance(), title,
                true);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ((DialogPanel) dialog.getContentPane()).performCancelAction();
            }
        });
        dialog.setContentPane(content);

        // bring it to the screen.
        dialog.pack();
        dialog.setLocationRelativeTo(Progressor.getInstance());
        content.setWindow(dialog);
        dialog.setVisible(true);
    }

    /*
     * public boolean isPrivileged() { // if we are started as an application,
     * we have all privileges if (!isApplet()) { return true; } // check, if we
     * already tried to set our SecurityManager if (_privileged != null) {
     * return _privileged.booleanValue(); } // This is an applet. Ask for
     * priviliges. try { // anonymous security manager granting any permission
     * new SecurityManager() { public void checkPermission(Permission
     * permission) { // grant any permission }
     * 
     * public void checkPermission(Permission permission, Object obj) { // grant
     * any permission } }; // user allowed the signed applet. Set flag.
     * _privileged = new Boolean(true); return true; } catch
     * (AccessControlException ace) { // User didn't allow the signed applet. //
     * Reset flag and display message. // ace.printStackTrace();
     * Log.log(LogWords.NO_PRIVILEGES_GRANTED, JOptionPane.INFORMATION_MESSAGE,
     * ace.getMessage(), true); _privileged = new Boolean(false); return false; } }
     */
    // indicates whether this program is started as applet or as application.
    // private boolean isApplet() {
    // return getAppletContext() == null;
    // }
    public void block() {

        // check lifecycle state
        if (_lifecycleState == STATE_RUNNING) {
            setLifecycleState(STATE_BLOCKED);
        }
    }

    public void unblock() {

        // check lifecycle state
        if (_lifecycleState == STATE_BLOCKED) {
            setLifecycleState(STATE_RUNNING);
        }
    }

    public void setLifecycleState(short newVal) {
        // System.out.println("switching to runlevel " + newVal);
        if (_lifecycleState != newVal) {
            short oldVal = _lifecycleState;
            _lifecycleState = newVal;

            // inform lifecycle listeners
            for (LifecycleListener l : _lifecycleListeners) {
                // System.out.println(listener);
                l.runLevelChanged(oldVal, newVal);
            }
        }
        // System.out.println("reached runlevel " + newVal);
    }

    // ============================ io methods //===============================

    /**
     * Opens an <code>inputStream</code> to the specified resource.
     * 
     * @param name
     *            a <code>string</code> specifying the resource to load
     * @return an <code>inputStream</code> if the resource could be found,
     *         <br>
     *         or <code>null</code> otherwise
     */
    public InputStream getResourceAsStream(String name) {
        return Manager.class.getClassLoader().getResourceAsStream(name);
    }

    public void setIOAccess(boolean ioAccess) {
        _ioAccess = ioAccess;
    }

    public boolean hasIOAccess() {
        return _ioAccess;
    }
}
