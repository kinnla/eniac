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

    CONFIGURATION_3, CONFIGURATION_4, CONFIGURATION_8,
    CONFIGURATION_12, CONFIGURATION_16, CONFIGURATION_20,
    ACCUMULATOR_UNIT, CYCLING_UNIT, INITIATING_UNIT,
    CONSTANT_TRANSMITTER_1_UNIT, CONSTANT_TRANSMITTER_2_UNIT,
    UPPER_TRUNK_2, UPPER_TRUNK_4, LOWER_TRUNK_3, LOWER_TRUNK_4, TRAY_2,
    TRAY_3, TRAY_4, BLINKEN_LIGHTS, ACCU_HEATERS, HEATERS, HEATERS_01,
    STEP_BUTTON, GO_BUTTON, CLEAR_BUTTON, CYCLE_COUNTER_CLEAR,
    ITERATION_SWITCH, SIGNIFICIANT_FIGURES_SWITCH, OPERATION_SWITCH,
    REPEAT_SWITCH, CIPHER, BLINKEN_NUMBER_SWITCH, BLINKEN_SIGN_SWITCH,
    CYCLING_LIGHTS, GO_LIGHTS, DIGIT_CONNECTOR, PROGRAM_CONNECTOR,
    INTER_CONNECTOR, DIGIT_CONNECTOR_CROSS, PROGRAM_CONNECTOR_PAIR,
    BLEND_8, BLEND_16, BLEND_A_10, CYCLING_SYMBOL, INITIATING_SYMBOL,
    CONSTANT_TRANSMITTER_1_SYMBOL, CONSTANT_TRANSMITTER_2_SYMBOL,
    CONSTANT_SELECTOR_SWITCH_AB, CONSTANT_SELECTOR_SWITCH_CD,
    CONSTANT_SELECTOR_SWITCH_EF, CONSTANT_SELECTOR_SWITCH_GH,
    CONSTANT_SELECTOR_SWITCH_JK, CONSTANT_SWITCH, INITIATING_IMAGE,
    FREQUENCY_SLIDER, BENCHMARK, CYCLE_COUNTER, XOR_IMAGE,
    CONSTANT_SIGN_TOGGLE_JL, CONSTANT_SIGN_TOGGLE_JR,
    CONSTANT_SIGN_TOGGLE_KL, CONSTANT_SIGN_TOGGLE_KR,
    CONSTANT_2_LIGHTS, CONSTANT_BLINKEN_CIPHER, CONSTANT_BLINKEN_SIGN,
    CONSTANT_TRANSMITTION_CIPHER, CONSTANT_TRANSMITTION_SIGN,
    CONSTANT_TRANSMITTION_LIGHTS;
	

    
	public enum Tag{
		NAME, TYPE, MODEL, VIEW, CODE, CODES, VALUE;
	}
	
    private String _edataClass;

    private String _epanelClass;

    private EType.Tag _codeName;

    private String[] _codes;

    private Descriptor[] _descriptors;

    private Grid[] _gridCache;

    private EType() {
        _gridCache = new Grid[StringConverter.toInt(EProperties.getInstance()
                .getProperty("GRID_CACHE_SIZE"))];
    }

    //========================== getters and setters
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
        System.out.println(edataClass);
    }

    public void setEPanelClass(String epanelClass) {
        _epanelClass = epanelClass;
    }

    public void setDescriptors(Descriptor[] descriptors) {
        // empty grid cache
        Arrays.fill(_gridCache, null);
        _descriptors = descriptors;
    }

    //================================ methods
    // =================================

    public EData makeEData() throws InstantiationException,
            ClassNotFoundException, IllegalAccessException {

        EData edata = (EData) Class.forName(_edataClass).newInstance();
        edata.setType(this);
        return edata;
    }

    public EPanel makeEPanel() {
        try {
            return (EPanel) Class.forName(_epanelClass).newInstance();
        } catch (Exception e) {
            //System.out.println(_epanelClass);
            e.printStackTrace();
        }
        return null;
    }

    public Descriptor getDescriptor(int lod) {
        return _descriptors[lod];
    }

    public Grid getGrid(int width, int height, int lod) {
        int index = computeIndex(width, height);
        if (_gridCache[index] == null || _gridCache[index].width != width
                || _gridCache[index].height != height) {

            _gridCache[index] = getDescriptor(lod).makeGrid(width, height);
        }
        return _gridCache[index];
    }

    private int computeIndex(int width, int height) {
        return (width + height) % _gridCache.length;
    }
}
