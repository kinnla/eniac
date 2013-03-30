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
import eniac.data.model.unit.ConstantTransmitter1;
import eniac.data.type.ProtoTypes;

/**
 * @author zoppke
 */
public class ConstantTransmittionLights extends ParentData implements Observer {

    public static final String PAINT_LIGHTS = "PAINT_LIGHTS"; //$NON-NLS-1$

    public void init() {
        super.init();

        // add as observer to corresponding unit and to our neighbour lights
        getUnit().addObserver(this);
        getConfiguration().getGarten().getKind(ProtoTypes.CONSTANT_2_LIGHTS, 0)
                .addObserver(this);
    }

    public boolean hasPower() {
        ConstantTransmitter1 unit = getUnit();
        return unit.hasPower() && unit.isTransmitting();
    }

    private ConstantTransmitter1 getUnit() {
        EData unit = getConfiguration().getGarten().getKind(
                ProtoTypes.CONSTANT_TRANSMITTER_1_UNIT, 0);
        return (ConstantTransmitter1) unit;
    }

    /**
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {

        if (arg == PAINT_LIGHTS) {
            // set number to our ciphers
            EData[] ciphers = getGarten().getKinder(
                    ProtoTypes.CONSTANT_TRANSMITTION_CIPHER);
            ConstantTransmitter1 unit = getUnit();
            long number = unit.getNumber();
            for (int i = ciphers.length - 1; i >= 0; --i) {
                ((Switch) ciphers[i]).setValue((int) (number % 10));
                number /= 10;
            }
            // set sign
            EData sign = getGarten().getKind(
                    ProtoTypes.CONSTANT_TRANSMITTION_SIGN, 0);
            ((Switch) sign).setValue(unit.isNegative() ? 0 : 1);

            // notify for repaint
            setChanged();
            notifyObservers(EData.REPAINT);
        }
    }
}
