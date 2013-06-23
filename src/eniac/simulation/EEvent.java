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
 * Created on 25.03.2004
 */
package eniac.simulation;

/**
 * @author zoppke
 */
public class EEvent {

	public static final short
	// event types
			GENERATE_NEW = 0,
			CPP = 1, PULSE_10P = 2, PULSE_9P = 3, PULSE_1P = 4, PULSE_2P = 5, PULSE_2AP = 6,
			PULSE_4P = 7,
			PULSE_1AP = 8, CCG_UP = 9, CCG_DOWN = 10, RP = 11, NOP = 12, ALARM = 13;

	/**
	 * Timestamp of this event
	 */
	public long time;

	/**
	 * Type of this event
	 */
	public short type;

	/**
	 * eevent listener for this event. If this is null, this event is dispatched
	 * by the eeventManager.dispatch(eevent) method.
	 */
	public EEventListener listener;

	public EEvent(long time, short type) {
		this.time = time;
		this.type = type;
	}

	public EEvent(long time, short type, EEventListener listener) {
		this.time = time;
		this.type = type;
		this.listener = listener;
	}

	public String toString() {
		return "[EEvent; time=" + time + ", type=" + type + "]"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
	}
}
