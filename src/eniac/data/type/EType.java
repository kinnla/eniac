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
 * Created on 22.03.2004
 */
package eniac.data.type;

import java.util.Arrays;

import eniac.data.model.EData;
import eniac.data.view.EPanel;
import eniac.skin.Descriptor;
import eniac.util.EProperties;
import eniac.util.StringConverter;

public enum EType {

	/**
	 * A configuration of 3 units
	 */
	CONFIGURATION_3,

	/**
	 * A configuration of 4 units
	 */
	CONFIGURATION_4,

	/**
	 * A configuration of 8 units
	 */
	CONFIGURATION_8,

	/**
	 * A configuration of 12 units
	 */
	CONFIGURATION_12,

	/**
	 * A configuration of 16 units
	 */
	CONFIGURATION_16,

	/**
	 * A configuration of 20 units
	 */
	CONFIGURATION_20,

	/**
	 * the accumulator
	 */
	ACCUMULATOR_UNIT,

	/**
	 * the cycling unit
	 */
	CYCLING_UNIT,

	/**
	 * the initiating unit
	 */
	INITIATING_UNIT,

	/**
	 * the 1st constant transmitter unit
	 */
	CONSTANT_TRANSMITTER_1_UNIT,

	/**
	 * the 2nd constant transmitter unit
	 */
	CONSTANT_TRANSMITTER_2_UNIT,

	/**
	 * an upper trunk which is 2 units width
	 */
	UPPER_TRUNK_2,

	/**
	 * an upper trunk which is 4 units width
	 */
	UPPER_TRUNK_4,

	/**
	 * a lower trunk which is 3 units width
	 */
	LOWER_TRUNK_3,

	/**
	 * a lower trunk which is 4 units width
	 */
	LOWER_TRUNK_4,

	/**
	 * a tray (part of a trunk) which is 2 units width
	 */
	TRAY_2,

	/**
	 * a tray (part of a trunk) which is 3 units width
	 */
	TRAY_3,

	/**
	 * a tray (part of a trunk) which is 4 units width
	 */
	TRAY_4,

	/**
	 * a blinken lights of an accumulator
	 */
	BLINKEN_LIGHTS,

	/**
	 * the heaters switch of an accumulator (may display the accumulator icon)
	 */
	ACCU_HEATERS,

	/**
	 * a standard heaters switch
	 */
	HEATERS,

	/**
	 * a heaters switch that displays 0 - 1
	 */
	HEATERS_01,

	/**
	 * the step button of the cycling unit
	 */
	STEP_BUTTON,

	/**
	 * the go button of the initiating unit
	 */
	GO_BUTTON,

	/**
	 * the clear button of the initiating unit
	 */
	CLEAR_BUTTON,

	/**
	 * the clear button for the cycle counter
	 */
	CYCLE_COUNTER_CLEAR,

	/**
	 * the iteration switch of the cycling unit
	 */
	ITERATION_SWITCH,

	/**
	 * the significant figures switch of an accumulator
	 */
	SIGNIFICIANT_FIGURES_SWITCH,

	/**
	 * the operations switch of an accumulator
	 */
	OPERATION_SWITCH,

	/**
	 * the repeat switch of an accumulator
	 */
	REPEAT_SWITCH,

	/**
	 * a cipher of the cycle counter at the cycling unit
	 */
	CIPHER,

	/**
	 * a digit of the blinken lights
	 */
	BLINKEN_NUMBER_SWITCH,

	/**
	 * the sign of the blinken lights
	 */
	BLINKEN_SIGN_SWITCH,

	/**
	 * the cycling lights -- display the pulse
	 */
	CYCLING_LIGHTS,

	/**
	 * the go lights of the initiating unit
	 */
	GO_LIGHTS,

	/**
	 * a digit connector (10 digits + sign + carryover)
	 */
	DIGIT_CONNECTOR,

	/**
	 * a program connector (1 pulse)
	 */
	PROGRAM_CONNECTOR,

	/**
	 * an inter-connector (can connect 2 accumulator units)
	 */
	INTER_CONNECTOR,

	/**
	 * a digit connector in horizontal layout (e.g. at the edges of a tray)
	 */
	DIGIT_CONNECTOR_CROSS,

	/**
	 * a pair of program connectors (in + out)
	 */
	PROGRAM_CONNECTOR_PAIR,

	/**
	 * a cover, without functionality (accumulator, between heaters and
	 * significant figures switch)
	 */
	BLEND_8,

	/**
	 * a cover, without functionality (accumulator, above the repeat switches)
	 */
	BLEND_16,

	/**
	 * a cover, without functionality (constant transmitter panel)
	 */
	BLEND_A_10,

	/**
	 * the symbol for the cycling unit
	 */
	CYCLING_SYMBOL,

	/**
	 * the symbol for the initiating unit
	 */
	INITIATING_SYMBOL,

	/**
	 * the symbol for the constant transmitter unit 1
	 */
	CONSTANT_TRANSMITTER_1_SYMBOL,

	/**
	 * the symbol for the constant transmitter unit 2
	 */
	CONSTANT_TRANSMITTER_2_SYMBOL,

	/**
	 * the constant selector switch for A+B
	 */
	CONSTANT_SELECTOR_SWITCH_AB,

	/**
	 * the constant selector switch for C+D
	 */
	CONSTANT_SELECTOR_SWITCH_CD,

	/**
	 * the constant selector switch for E+F
	 */
	CONSTANT_SELECTOR_SWITCH_EF,

	/**
	 * the constant selector switch for G+H
	 */
	CONSTANT_SELECTOR_SWITCH_GH,

	/**
	 * the constant selector switch for J+K
	 */
	CONSTANT_SELECTOR_SWITCH_JK,

	/**
	 * a number switch at the constant transmitter unit 2
	 */
	CONSTANT_SWITCH,

	/**
	 * the image displayed at the initiating unit
	 */
	INITIATING_IMAGE,

	/**
	 * the frequency slider (cycling unit)
	 */
	FREQUENCY_SLIDER,

	/**
	 * the benchmark, displaying the number of addition cycles per second
	 */
	BENCHMARK,

	/**
	 * the addition cycle counter
	 */
	CYCLE_COUNTER,

	/**
	 * image to be used in the overview panel, as marker for the currently
	 * visible area
	 */
	XOR_IMAGE,

	/**
	 * toggle switch for the constant transmitter 2, row J, left
	 */
	CONSTANT_SIGN_TOGGLE_JL,

	/**
	 * toggle switch for the constant transmitter 2, row J, right
	 */
	CONSTANT_SIGN_TOGGLE_JR,

	/**
	 * toggle switch for the constant transmitter 2, row K, left
	 */
	CONSTANT_SIGN_TOGGLE_KL,

	/**
	 * toggle switch for the constant transmitter 2, row K, right
	 */
	CONSTANT_SIGN_TOGGLE_KR,

	/**
	 * the numbers display at the constant transmitter 2
	 */
	CONSTANT_2_LIGHTS,

	/**
	 * a decimal cipher of the constant transmitter 2 lights
	 */
	CONSTANT_BLINKEN_CIPHER,

	/**
	 * the +/- sign for a number at the constant transmitter 2 lights
	 */
	CONSTANT_BLINKEN_SIGN,

	/**
	 * a cipher displayed at constant transmitter 1, to be shown during a
	 * transmission
	 */
	CONSTANT_TRANSMITTION_CIPHER,

	/**
	 * a =/- sign displayed at constant transmitter 1, to be shown during a
	 * transmission
	 */
	CONSTANT_TRANSMITTION_SIGN,

	/**
	 * the lights at constant transmitter 1, will display number and sign during
	 * a transmission
	 */
	CONSTANT_TRANSMITTION_LIGHTS;

	public enum Tag {
		NAME, TYPE, MODEL, VIEW, CODE, CODES, VALUE;
	}

	private String _edataClass;

	private String _epanelClass;

	private EType.Tag _codeName;

	private String[] _codes;

	private Descriptor[] _descriptors;

	private Grid[] _gridCache;

	private EType() {
		_gridCache = new Grid[StringConverter.toInt(EProperties.getInstance().getProperty("GRID_CACHE_SIZE"))];
	}

	// ========================== getters and setters
	// ===========================

	public void setCodes(String[] codes) {
		_codes = codes;
	}

	public String[] getCodes() {
		return _codes;
	}

	public void setCodeName(EType.Tag codeName) {
		_codeName = codeName;
	}

	public EType.Tag getCodeName() {
		return _codeName;
	}

	public void setEDataClass(String edataClass) {
		_edataClass = edataClass;
	}

	public void setEPanelClass(String epanelClass) {
		_epanelClass = epanelClass;
	}

	public void setDescriptors(Descriptor[] descriptors) {
		// empty grid cache
		Arrays.fill(_gridCache, null);
		_descriptors = descriptors;
	}

	// ================================ methods
	// =================================

	public EData makeEData() throws InstantiationException, ClassNotFoundException, IllegalAccessException {

		EData edata = (EData) Class.forName(_edataClass).newInstance();
		edata.setType(this);
		return edata;
	}

	public EPanel makeEPanel() {
		try {
			return (EPanel) Class.forName(_epanelClass).newInstance();
		} catch (Exception e) {
			// System.out.println(_epanelClass);
			e.printStackTrace();
		}
		return null;
	}

	public Descriptor getDescriptor(int lod) {
		return _descriptors[lod];
	}

	public Grid getGrid(int width, int height, int lod) {
		int index = computeIndex(width, height);
		if (_gridCache[index] == null || _gridCache[index].width != width || _gridCache[index].height != height) {

			_gridCache[index] = getDescriptor(lod).makeGrid(width, height);
		}
		return _gridCache[index];
	}

	private int computeIndex(int width, int height) {
		return (width + height) % _gridCache.length;
	}
}
