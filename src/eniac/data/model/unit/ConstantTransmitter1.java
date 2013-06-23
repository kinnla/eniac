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
 * Created on 28.05.2004
 */
package eniac.data.model.unit;

import java.util.Observable;

import eniac.data.PulseInteractor;
import eniac.data.model.Connector;
import eniac.data.model.CyclingLights;
import eniac.data.model.EData;
import eniac.data.model.parent.ConstantTransmittionLights;
import eniac.data.model.parent.ProgramConnectorPair;
import eniac.data.model.sw.Switch;
import eniac.data.model.sw.SwitchAndFlag;
import eniac.data.type.EType;
import eniac.simulation.EEvent;
import eniac.simulation.EEventListener;

/**
 * @author zoppke
 */
public class ConstantTransmitter1 extends Unit implements EEventListener, PulseInteractor {

	private static final int[] SEND_1P = {1, 3, 5, 7, 9};

	private static final int[] SEND_2P = {2, 3, 4, 5, 6, 7, 8, 9};

	private static final int[] SEND_2AP = {4, 5, 8, 9};

	private static final int[] SEND_4P = {6, 7, 8, 9};

	private boolean _transmitting;

	private long _startTime;

	private SwitchAndFlag _program;

	public void init() {
		super.init();

		// init as eventlinstener
		CyclingLights lights = getConfiguration().getCyclingLights();
		lights.addEEventListener(this, EEvent.CPP);
		lights.addEEventListener(this, EEvent.PULSE_1P);
		lights.addEEventListener(this, EEvent.PULSE_2P);
		lights.addEEventListener(this, EEvent.PULSE_2AP);
		lights.addEEventListener(this, EEvent.PULSE_4P);
		lights.addEEventListener(this, EEvent.PULSE_1AP);

		// observe our switches and toggles in order to notify the lights
		EData[] kinder = getGarten().getAllKinder();
		for (int i = 0; i < kinder.length; ++i) {
			if (kinder[i].getType() != EType.PROGRAM_CONNECTOR_PAIR) {
				kinder[i].addObserver(this);
			}
		}
	}

	/**
	 * @return @see eniac.data.model.unit.Unit#getHeaters()
	 */
	public Switch getHeaters() {
		return (Switch) getGarten().getKind(EType.HEATERS_01, 0);
	}

	public boolean isTransmitting() {
		return _transmitting;
	}

	/**
	 * @param o
	 * @param arg
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object args) {
		if (o instanceof Switch) {
			Switch sw = (Switch) o;
			if (sw.getType() == EType.HEATERS_01) {
				boolean power = sw.isValue();
				if (!power) {
					// power switched of. delete program transmittion data
					_program = null;
					_startTime = -1;
					_transmitting = false;
				}
			}
			// notify constantTransmittionLights to repaint its number
			setChanged();
			notifyObservers(ConstantTransmittionLights.PAINT_LIGHTS);
		}
	}

	public long getNumber() {

		// if no program, return 0
		if (_program == null) {
			return 0;
		}

		// if number is reiceived from ibm card reader, return 0
		if (_program.getType() != EType.CONSTANT_SELECTOR_SWITCH_JK) {
			// TODO: implement ibm card reader.
			return 0;
		}

		// constant transmitter panel 2
		ConstantTransmitter2 trans2 = (ConstantTransmitter2) getConfiguration().getGarten().getKind(
				EType.CONSTANT_TRANSMITTER_2_UNIT, 0);

		// if trans2 has no power, we cannot get a number and return 0
		if (!trans2.hasPower()) {
			return 0;
		}

		// getting number
		long number = 0;
		int index = _program.isFlag() ? 10 : 0;
		if (_program.getValue() <= 1) {
			// left
			for (int i = 0; i < 5; ++i) {
				Switch sw = (Switch) trans2.getGarten().getKind(EType.CONSTANT_SWITCH, index + i);
				number *= 10;
				number += sw.getValue();
			}
		}
		if (_program.getValue() >= 1) {
			// right
			for (int i = 5; i < 10; ++i) {
				Switch sw = (Switch) trans2.getGarten().getKind(EType.CONSTANT_SWITCH, index + i);
				number *= 10;
				number += sw.getValue();
			}
		}
		return number;
	}

	public boolean isNegative() {

		// if no program, we are not negative
		if (_program == null) {
			return false;
		}

		// if constant selector doesn't point to the transmittor 2,
		// return as nonegative in order to not send the complement of 0.
		if (!(_program.getType() == EType.CONSTANT_SELECTOR_SWITCH_JK)) {
			return false;
		}
		// get transition partner
		Switch sw;
		ConstantTransmitter2 trans2 = (ConstantTransmitter2) getConfiguration().getGarten().getKind(
				EType.CONSTANT_TRANSMITTER_2_UNIT, 0);

		// no power means not negative
		if (!trans2.hasPower()) {
			return false;
		}

		// determine sign
		if (_program.getValue() <= 1) {
			if (!_program.isFlag()) {
				sw = (Switch) trans2.getGarten().getKind(EType.CONSTANT_SIGN_TOGGLE_JL, 0);
			}
			else {
				sw = (Switch) trans2.getGarten().getKind(EType.CONSTANT_SIGN_TOGGLE_KL, 0);
			}
		}
		else {
			if (!_program.isFlag()) {
				sw = (Switch) trans2.getGarten().getKind(EType.CONSTANT_SIGN_TOGGLE_JR, 0);
			}
			else {
				sw = (Switch) trans2.getGarten().getKind(EType.CONSTANT_SIGN_TOGGLE_KR, 0);
			}
		}
		return !sw.isValue();
	}

	/**
	 * @param e
	 * @see eniac.simulation.EEventListener#process(eniac.simulation.EEvent)
	 */
	public void process(EEvent e) {
		switch (e.type) {
			case EEvent.CPP :
				if (_transmitting && hasPower() && _startTime < e.time) {
					_transmitting = false;
					sendProgram(e.time, this);
					setChanged();
					notifyObservers(ConstantTransmittionLights.PAINT_LIGHTS);
				}
				break;

			case EEvent.PULSE_1P :
				maybeSendBy(SEND_1P, e.time);
				break;

			case EEvent.PULSE_2P :
				maybeSendBy(SEND_2P, e.time);
				break;

			case EEvent.PULSE_2AP :
				maybeSendBy(SEND_2AP, e.time);
				break;

			case EEvent.PULSE_4P :
				maybeSendBy(SEND_4P, e.time);
				break;

			case EEvent.PULSE_1AP :
				if (_transmitting && hasPower() && isNegative()) {
					sendDigit(e.time, 1, this);
				}
		}
	}

	private static long toComplement(long number) {
		return 9999999999L - number;
	}

	private void maybeSendBy(int[] ciphers, long time) {
		// if we are not transmitting, don't send.
		if (!_transmitting || !hasPower()) {
			return;
		}

		// get number
		long number = getNumber();
		boolean negative = isNegative();
		if (negative) {
			number = toComplement(number);
		}
		// compute pulse
		long factor = 1;
		long pulse = 0;
		while (number > 0) {
			int cipher = (int) (number % 10);
			for (int i = 0; i < ciphers.length; ++i) {
				if (ciphers[i] == cipher) {
					pulse += factor;
				}
			}
			factor *= 10;
			number /= 10;
		}
		if (negative) {
			pulse += factor;
		}
		// send pulse
		sendDigit(time, pulse, this);
	}

	// ========================= pulseinteractor methods
	// ========================

	public boolean canReceiveProgram(long time, PulseInteractor source) {
		return hasPower();
	}

	public void receiveProgram(long time, PulseInteractor source) {

		// send program pulse, if a program ended just now.
		if (_transmitting) {
			sendProgram(time, this);
		}
		_transmitting = true;
		_startTime = time;
		int index = ((EData) source).getIndex();
		int modIndex = index % 10;
		int row = index / 10;
		if (modIndex < 2) {
			_program = (SwitchAndFlag) getGarten().getKind(EType.CONSTANT_SELECTOR_SWITCH_AB, row * 2 + modIndex);
		}
		else if (modIndex < 4) {
			_program = (SwitchAndFlag) getGarten().getKind(EType.CONSTANT_SELECTOR_SWITCH_CD, row * 2 + modIndex - 2);
		}
		else if (modIndex < 6) {
			_program = (SwitchAndFlag) getGarten().getKind(EType.CONSTANT_SELECTOR_SWITCH_EF, row * 2 + modIndex - 4);
		}
		else if (modIndex < 8) {
			_program = (SwitchAndFlag) getGarten().getKind(EType.CONSTANT_SELECTOR_SWITCH_GH, row * 2 + modIndex - 6);
		}
		else {
			_program = (SwitchAndFlag) getGarten().getKind(EType.CONSTANT_SELECTOR_SWITCH_JK, row * 2 + modIndex - 8);
		}
		setChanged();
		notifyObservers(ConstantTransmittionLights.PAINT_LIGHTS);
	}

	public void sendProgram(long time, PulseInteractor source) {
		int index = _program.getIndex();
		if (index > 2) {
			index += 8;
		}
		else if (index > 4) {
			index += 16;
		}
		if (_program.getType() == EType.CONSTANT_SELECTOR_SWITCH_CD) {
			index += 2;
		}
		else if (_program.getType() == EType.CONSTANT_SELECTOR_SWITCH_EF) {
			index += 4;
		}
		else if (_program.getType() == EType.CONSTANT_SELECTOR_SWITCH_GH) {
			index += 6;
		}
		else if (_program.getType() == EType.CONSTANT_SELECTOR_SWITCH_JK) {
			index += 8;
		}
		ProgramConnectorPair pair = (ProgramConnectorPair) getGarten().getKind(EType.PROGRAM_CONNECTOR_PAIR, index);
		pair.sendProgram(time, this);
	}

	/**
	 * @param time
	 * @param source
	 * @return @see eniac.data.PulseInteractor#canReceiveDigit(long,
	 *         eniac.data.PulseInteractor)
	 */
	public boolean canReceiveDigit(long time, PulseInteractor source) {
		return false;
	}

	public void receiveDigit(long time, long value, PulseInteractor source) {
		// cannot receive digit, so nothing to do.
	}

	public void sendDigit(long time, long value, PulseInteractor source) {
		Connector con = (Connector) getGarten().getKind(EType.DIGIT_CONNECTOR_CROSS, 0);
		con.sendDigit(time, value, this);
	}

}
