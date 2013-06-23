package eniac.util;

import java.util.LinkedList;

public enum Status {

	/**
	 * the active configuration or null
	 */
	CONFIGURATION,

	/**
	 * flag indicating, whether the overview window is shown or not
	 */
	SHOW_OVERVIEW,

	/**
	 * the default height of the configuration panel
	 */
	BASIC_CONFIGURATION_HEIGHT,

	/**
	 * the height of the configuration panel
	 */
	ZOOMED_HEIGHT,

	/**
	 * flag indicating, whether the overview window is shown or not
	 */
	SHOW_LOG,

	/**
	 * the currently loaded skin (currently only buttercream) or null
	 */
	SKIN,

	/**
	 * the currently loaded language
	 */
	LANGUAGE,

	/**
	 * flag indicating, whether the pulses shall be highlighted or not
	 */
	HIGHLIGHT_PULSE,

	/**
	 * the current simulation time
	 */
	SIMULATION_TIME;

	/*
	 * ============================= fields ==================================
	 */

	// the value for this status key
	private Object _value;

	private LinkedList<StatusListener> _listeners = new LinkedList<>();

	/*
	 * ========================= singleton stuff =======================
	 */

	public static void initValues() {
		CONFIGURATION._value = null;
		SHOW_OVERVIEW._value = StringConverter.toBoolean(EProperties.getInstance().getProperty(SHOW_OVERVIEW));
		ZOOMED_HEIGHT._value = StringConverter.toInt(EProperties.getInstance().getProperty(BASIC_CONFIGURATION_HEIGHT));
		SHOW_LOG._value = StringConverter.toBoolean(EProperties.getInstance().getProperty(SHOW_LOG));
		SKIN._value = null;
		LANGUAGE._value = null;
		HIGHLIGHT_PULSE._value = StringConverter.toBoolean(EProperties.getInstance().getProperty(HIGHLIGHT_PULSE));
		SIMULATION_TIME._value = -1;
	}

	// =========================== getter and setter
	// ============================

	public void setValue(Object newValue) {
		Object oldValue = _value;
		if (oldValue == null) {
			if (newValue != null) {
				_value = newValue;
				informListeners();
			}
		}
		else if (!oldValue.equals(newValue)) {
			_value = newValue;
			informListeners();
		}
	}

	public boolean toggle() {
		return (boolean) (_value = !((boolean) _value));
	}

	public Object getValue() {
		return _value;
	}

	/*
	 * ========================= listener stuff =======================
	 */

	public void addListener(StatusListener listener) {
		_listeners.add(listener);
	}

	public void removeListener(StatusListener listener) {
		_listeners.remove(listener);
	}

	private void informListeners() {
		for (StatusListener listener : _listeners) {
			listener.statusChanged(this, _value);
		}
	}
}
