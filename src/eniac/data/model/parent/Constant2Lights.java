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
 * Created on 29.05.2004
 */
package eniac.data.model.parent;

import java.util.Observable;
import java.util.Observer;

import eniac.data.model.EData;
import eniac.data.model.sw.Switch;
import eniac.data.model.unit.ConstantTransmitter2;
import eniac.data.type.EType;

/**
 * @author zoppke
 */
public class Constant2Lights extends ParentData implements Observer {

    public void init() {
        super.init();
        
        // assert, that constant transmitter already initialized
        ConstantTransmitter2 unit = getTransmitter();
        assertInit(unit);
        
        // observe sign switchs
        unit.getGarten().getKind(EType.CONSTANT_SIGN_TOGGLE_JL, 0)
                .addObserver(this);
        unit.getGarten().getKind(EType.CONSTANT_SIGN_TOGGLE_JR, 0)
                .addObserver(this);
        unit.getGarten().getKind(EType.CONSTANT_SIGN_TOGGLE_KL, 0)
                .addObserver(this);
        unit.getGarten().getKind(EType.CONSTANT_SIGN_TOGGLE_KR, 0)
                .addObserver(this);

        // observe constant switchs
        EData[] switchs = unit.getGarten()
                .getKinder(EType.CONSTANT_SWITCH);
        for (int i = 0; i < switchs.length; ++i) {
            switchs[i].addObserver(this);
        }
        EData heaters = unit.getGarten().getKind(EType.HEATERS, 0);
        heaters.addObserver(this);
    }

    public boolean hasPower() {
        return getTransmitter().hasPower();
    }

    private ConstantTransmitter2 getTransmitter() {
        return (ConstantTransmitter2) getConfiguration().getGarten().getKind(
                EType.CONSTANT_TRANSMITTER_2_UNIT, 0);
    }

    /**
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        EData d = (EData) o;
        EType t = d.getType();
        if (t == EType.CONSTANT_SIGN_TOGGLE_JL) {
            Switch sign = (Switch) getGarten().getKind(
                    EType.CONSTANT_BLINKEN_SIGN, 0);
            sign.setValue(((Switch) d).getValue());
        } else if (t == EType.CONSTANT_SIGN_TOGGLE_JR) {
            Switch sign = (Switch) getGarten().getKind(
                    EType.CONSTANT_BLINKEN_SIGN, 1);
            sign.setValue(((Switch) d).getValue());
        } else if (t == EType.CONSTANT_SIGN_TOGGLE_KL) {
            Switch sign = (Switch) getGarten().getKind(
                    EType.CONSTANT_BLINKEN_SIGN, 2);
            sign.setValue(((Switch) d).getValue());
        } else if (t == EType.CONSTANT_SIGN_TOGGLE_KR) {
            Switch sign = (Switch) getGarten().getKind(
                    EType.CONSTANT_BLINKEN_SIGN, 3);
            sign.setValue(((Switch) d).getValue());
        } else if (t == EType.CONSTANT_SWITCH) {
            Switch sw = (Switch) getGarten().getKind(
                    EType.CONSTANT_BLINKEN_CIPHER, d.getIndex());
            sw.setValue(((Switch) d).getValue());
        } else if (t == EType.HEATERS) {
            setChanged();
            notifyObservers(EData.REPAINT);
        }
        setChanged();
        notifyObservers(ConstantTransmittionLights.PAINT_LIGHTS);
    }
}
