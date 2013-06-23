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
 * Created on 28.03.2004
 */
package eniac.data.model.unit;

import java.util.Observable;

import eniac.data.KinderGarten;
import eniac.data.PulseInteractor;
import eniac.data.model.Connector;
import eniac.data.model.CyclingLights;
import eniac.data.model.EData;
import eniac.data.model.parent.Configuration;
import eniac.data.model.sw.Switch;
import eniac.data.model.sw.SwitchAndFlag;
import eniac.data.type.EType;
import eniac.simulation.EEvent;
import eniac.simulation.EEventListener;

/**
 * @author zoppke
 */
public class Initiating extends Unit implements EEventListener {

	private boolean _goPressed = false;

	private boolean _clearPressed = false;

	// ============================= lifecycle
	// ==================================

	public Initiating() {
		// empty
	}

	public void init() {

		// super inits children and adds listener to heaters
		super.init();

		// add listeners to buttons
		getGarten().getKind(EType.GO_BUTTON, 0).addObserver(this);
		getGarten().getKind(EType.CLEAR_BUTTON, 0).addObserver(this);

		// add as listener to eevent manager
		CyclingLights lights = getConfiguration().getCyclingLights();
		lights.addEEventListener(this, EEvent.CPP);
		lights.addEEventListener(this, EEvent.RP);
	}

	// ================================ methods
	// =================================

	/**
	 * @return @see eniac.data.unit.Unit#getHeaters()
	 */
	public Switch getHeaters() {
		return (Switch) getGarten().getKind(EType.HEATERS, 0);
	}

	public void update(Observable o, Object args) {
		Switch sw = (Switch) o;
		EType type = sw.getType();
		if (type == EType.GO_BUTTON && hasPower()) {

			// go button pressed
			if (sw.isValue()) {
				_goPressed = true;
			}
		}
		else if (type == EType.CLEAR_BUTTON && hasPower()) {

			// clear button pressed
			if (sw.isValue()) {
				_clearPressed = true;
			}
		}
	}

	/**
	 * @param e
	 * @see eniac.simulation.EEventListener#process(eniac.simulation.EEvent)
	 */
	public void process(EEvent e) {
		if (hasPower()) {
			switch (e.type) {

			// CPP
				case EEvent.CPP :
					if (_goPressed) {
						sendProgram(e.time, this);
						_goPressed = false;
					}
					break;

				// RP
				case EEvent.RP :

					// if clearbutton has not been pressed,
					// there is nothing to do
					if (!_clearPressed) {
						return;
					}

					// if carry clear gate is hight, nothing to do.
					Configuration config = getConfiguration();
					if (config.getCyclingLights().isCCG()) {
						return;
					}

					// clear accumulators, if their selective clear is set.
					_clearPressed = false;
					EData[] accus = config.getGarten().getKinder(EType.ACCUMULATOR_UNIT);
					for (int i = 0; i < accus.length; ++i) {
						Accumulator accu = (Accumulator) accus[i];
						KinderGarten og = accu.getGarten();
						EData d = og.getKind(EType.SIGNIFICIANT_FIGURES_SWITCH, 0);
						if (((SwitchAndFlag) d).isFlag()) {
							accu.clear();
						}
					}
					break;
			}
		}
	}

	// ========================== pulseInteractor methods
	// =======================

	public void sendProgram(long time, PulseInteractor source) {
		EData d = getGarten().getKind(EType.PROGRAM_CONNECTOR, 0);
		Connector con = (Connector) d;
		con.sendProgram(time, this);
	}
}
