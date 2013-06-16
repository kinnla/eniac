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
 * Created on 18.09.2003
 */
package eniac.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import eniac.LifecycleListener;
import eniac.Manager;
import eniac.io.Tags;

/**
 * @author zoppke
 */
public class Status implements LifecycleListener {

	/*
	 * ============================= fields ==================================
	 */
	
    // for handling the listeners
    private PropertyChangeSupport _pcs = null;

    // containing all properties as key - value pairs
    private HashMap<String, Object> _map;

    // ============================ singleton stuff
    // =============================

    // singleton self reference
    private static Status instance;

    // singleton private constructor
    private Status() {
        // empty
    }

    // note: this method has to be synchronized, because during loading a skin
    // or scanning for proxies there are separate threads started.
    // So we have to make sure, that the new Status object is created AND
    // it is initialized, befor another thread can enter this method and
    // can find a non-null reference.
    public synchronized static Status getInstance() {
        if (instance == null) {
            instance = new Status();
            instance.init();
        }
        return instance;
    }

    private void init() {

        // add as singleton to starter
        Manager.getInstance().addMainListener(this);

        // property change support for fireing property change events
        _pcs = new PropertyChangeSupport(this);

        // helper variables
        int i;
        boolean b;

        // init map
        _map = new HashMap<>();

        // configuration
        _map.put(Tags.CONFIGURATION, null);

        // show_overview
        b = StringConverter.toBoolean(EProperties.getInstance().getProperty(
                "SHOW_OVERVIEW"));
        _map.put(Tags.SHOW_OVERVIEW, new Boolean(b));

        // zoomed_height
        i = StringConverter.toInt(EProperties.getInstance().getProperty(
                "BASIC_CONFIGURATION_HEIGHT"));
        _map.put(Tags.ZOOMED_HEIGHT, new Integer(i));

        // show_log
        b = StringConverter.toBoolean(EProperties.getInstance()
                .getProperty("SHOW_LOG"));
        _map.put(Tags.SHOW_LOG, new Boolean(b));

        // skin
        _map.put(Tags.SKIN, null);

        // lifecycle
        // _map.put(Tags.LIFECYCLE, new Short(Starter.INIT));

        // language
        _map.put(Tags.LANGUAGE, null);

        // highlight_pulse
        b = StringConverter.toBoolean(EProperties.getInstance().getProperty(
                "HIGHLIGHT_PULSE"));
        _map.put("highlight_pulse", new Boolean(b));

        // simulation_time
        _map.put("simulation_time", new Long(-1));
    }

    // =========================== getter and setter
    // ============================

    public static void set(String key, Object value) {

        // make sure that we are initialized
        getInstance();

        // set value
        Object oldValue = get(key);
        if (oldValue == null) {
            if (value != null) {
                instance._map.put(key, value);
                instance._pcs.firePropertyChange(key, oldValue, value);
            }
        } else {
            if (value == null) {
                instance._map.put(key, value);
                instance._pcs.firePropertyChange(key, oldValue, value);
            } else if (!oldValue.equals(value)) {
                instance._map.put(key, value);
                instance._pcs.firePropertyChange(key, oldValue, value);
            }
        }
    }

    public static boolean toggle(String key) {
        boolean newValue = !((Boolean)get(key));
        set(key, newValue);
        return newValue;
    }

    public static Object get(String key) {
        return getInstance()._map.get(key);
    }

    public static int getInt(String key) {
        return ((Integer) get(key)).intValue();
    }

    public static long getLong(String key) {
        return ((Long) get(key)).longValue();
    }

    // ========================= listener stuff
    // =================================

    public void addListener(PropertyChangeListener listener) {
        _pcs.addPropertyChangeListener(listener);
    }

    public void addListener(String key, PropertyChangeListener listener) {
        _pcs.addPropertyChangeListener(key, listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        _pcs.removePropertyChangeListener(listener);
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
