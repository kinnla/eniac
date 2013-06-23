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
 * Created on 21.03.2004
 */
package eniac.data.model.unit;

import java.util.Observer;

import eniac.data.PulseInteractor;
import eniac.data.model.EData;
import eniac.data.model.parent.Configuration;
import eniac.data.model.parent.ParentData;
import eniac.data.model.sw.Switch;

/**
 * @author zoppke
 */
public abstract class Unit extends ParentData implements Observer, PulseInteractor {

	// ============================== lifecycle
	// =================================

	public Unit() {
		// empty
	}

	public void init() {

		// super inits children
		super.init();

		// add this as dataListener to heaters in order to track power changing
		getHeaters().addObserver(this);
	}

	// ================================ methods
	// =================================

	public abstract Switch getHeaters();

	public boolean hasPower() {
		return getHeaters().isValue();
	}

	public EData getBlinkens() {
		return ((Configuration) getParent()).getChild(_gridNumbers[0], 0);
	}

	// ======================== pulseinteractor methods
	// =========================

	/**
	 * @param time
	 * @param source
	 * @return @see eniac.data.PulseInteractor#canReceiveProgram(long,
	 *         eniac.data.PulseInteractor)
	 */
	public boolean canReceiveProgram(long time, PulseInteractor source) {
		return hasPower();
	}

	public void receiveProgram(long time, PulseInteractor source) {
		// empty
	}

	public void sendProgram(long time, PulseInteractor source) {
		// empty
	}

	/**
	 * @param time
	 * @param source
	 * @return @see eniac.data.PulseInteractor#canReceiveDigit(long,
	 *         eniac.data.PulseInteractor)
	 */
	public boolean canReceiveDigit(long time, PulseInteractor source) {
		return hasPower();
	}

	public void receiveDigit(long time, long value, PulseInteractor source) {
		// empty
	}

	public void sendDigit(long time, long value, PulseInteractor source) {
		// empty
	}
}
