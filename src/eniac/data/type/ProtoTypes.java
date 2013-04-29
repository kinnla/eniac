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

import java.io.InputStream;
import java.lang.reflect.Field;

import eniac.Manager;
import eniac.io.IOUtil;
import eniac.log.Log;
import eniac.util.EProperties;

/**
 * @author zoppke
 */
public class ProtoTypes {

    static {
    		String file = EProperties.getInstance().getProperty("PROTOTYPES_FILE");
        InputStream in = Manager.class.getClassLoader().getResourceAsStream(file);
        TypeHandler handler = new TypeHandler();
        try {
            IOUtil.parse(in, handler);
        } catch (Exception e) {
            Log.log("Error in initializing types"); //$NON-NLS-1$
        }
    }

    public static EType
    //
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

    //=============================== methods
    // ==================================

    public static void setType(EType type) {
        try {
            String name = type.getName().toUpperCase();
            Field f = ProtoTypes.class.getField(name);
            f.set(null, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EType[] getTypes() {
        Field[] fields = ProtoTypes.class.getFields();
        EType[] types = new EType[fields.length];
        for (int i = 0; i < types.length; ++i) {
            try {
                types[i] = (EType) fields[i].get(null);
                //System.out.println(fields[i].getName() + "------" +
                // types[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return types;
    }

    public static EType getType(String name) {
        try {
            Field f = ProtoTypes.class.getField(name.toUpperCase());
            return (EType) f.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
