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
 * Created on 26.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.model.parent;

import java.util.List;

import eniac.data.model.EData;
import eniac.data.model.sw.Switch;
import eniac.data.model.sw.SwitchAndFlag;
import eniac.data.model.unit.Unit;
import eniac.data.type.ProtoTypes;
import eniac.io.Tag;
import eniac.property.ConditionedProperty;
import eniac.property.Property;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class BlinkenLights extends ParentData {

    public static final long MODVAL = 20000000000L;

    private boolean _enabled;

    //============================= lifecycle
    // ==================================

    public BlinkenLights() {
        // empty
    }

    //============================== methods
    // ===================================

    public boolean hasPower() {
        Configuration config = (Configuration) getParent();
        Unit unit = config.getUnit(_gridNumbers[0]);
        return unit.hasPower();
    }

    public List<Property> getProperties() {
        List<Property> list = super.getProperties();
        if (hasPower()) {
            String s = Long.toString(getNumber());
            list.add(new ConditionedProperty(Tag.NUMBER, s) {
                protected boolean checkValue(String value) {
                    try {
                        long l = Long.parseLong(value);
                        return (l >= 0 && l < MODVAL);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            });
        }
        return list;
    }

    public void setProperties(List<Property> l) {
        for (Property p : l) {
            if (p.getName().equals(Tag.NUMBER)) {
                String s = ((ConditionedProperty) p).getValue();
                setNumber(StringConverter.toLong(s));
               // it.remove(); ==> need to remove from iterator (== property list) ?
            }
        }
        super.setProperties(l);
    }

    public void setEnabled(boolean b) {
        if (b != _enabled) {
            EData[] children = getGarten().getAllKinder();
            for (int i = 0; i < children.length; ++i) {
                Switch sw = (Switch) children[i];
                sw.setEnabled(b);
            }
        }
        _enabled = b;
        setChanged();
        notifyObservers(EData.REPAINT);
    }

    //============================ number methods
    // ==============================

    public long getNumber() {

        // initialize start value for number according to sign
        Switch sign = (Switch) getGarten().getKind(
                ProtoTypes.BLINKEN_SIGN_SWITCH, 0);
        long number = sign.getValue();

        // add digit by digit
        EData[] digits = getGarten()
                .getKinder(ProtoTypes.BLINKEN_NUMBER_SWITCH);
        for (int i = 0; i < 10; ++i) {
            number *= 10;
            number += ((Switch) digits[i]).getValue();
        }

        // return number
        return number;
    }

    /**
     * Sets the number to the specified value.
     * 
     * @param l
     *            The new value for the number
     */
    public void setNumber(long l) {
        assert (0 <= l && l < MODVAL);

        // set digits
        EData[] digits = getGarten()
                .getKinder(ProtoTypes.BLINKEN_NUMBER_SWITCH);
        for (int i = 9; i >= 0; --i) {
            ((Switch) digits[i]).setValue((int) (l % 10));
            l = l / 10;
        }
        // set sign
        Switch sign = (Switch) getGarten().getKind(
                ProtoTypes.BLINKEN_SIGN_SWITCH, 0);
        sign.setValue((int) l);
    }

    public void rotateNumber(long pulse) {

        // rotate digits
        EData[] digits = getGarten()
                .getKinder(ProtoTypes.BLINKEN_NUMBER_SWITCH);
        for (int i = digits.length - 1; i >= 0; --i) {
            if (pulse % 10 > 0) {
                ((SwitchAndFlag) digits[i]).rotateValue();
            }
            pulse /= 10;
        }
        // rotate sign
        if (pulse % 10 > 0) {
            Switch sign = (Switch) getGarten().getKind(
                    ProtoTypes.BLINKEN_SIGN_SWITCH, 0);
            sign.toggleValue();
        }
    }

    public boolean carryOver() {
        // perform carryOver at number switches
        EData[] digits = getGarten()
                .getKinder(ProtoTypes.BLINKEN_NUMBER_SWITCH);
        for (int i = digits.length - 1; i > 0; --i) {
            if (((SwitchAndFlag) digits[i]).isFlag()) {
                ((SwitchAndFlag) digits[i - 1]).rotateValue();
            }
        }
        // return the flag of the highest decade counter
        return ((SwitchAndFlag) digits[0]).isFlag();
    }

    public void clearCarry() {
        // clear carryOver-flags for all number switches
        EData[] digits = getGarten()
                .getKinder(ProtoTypes.BLINKEN_NUMBER_SWITCH);
        for (int i = 0; i < digits.length; ++i) {
            ((SwitchAndFlag) digits[i]).setFlag(false);
        }
    }

    public long computePulse(int transmittionCycle) {

        // set sign
        EData sign = getGarten().getKind(ProtoTypes.BLINKEN_SIGN_SWITCH, 0);
        long pulse = ((Switch) sign).getValue();

        // set digits
        EData[] digits = getGarten()
                .getKinder(ProtoTypes.BLINKEN_NUMBER_SWITCH);
        for (int i = 0; i < digits.length; ++i) {
            pulse = pulse * 10;
            if (((Switch) digits[i]).getValue() < transmittionCycle) {
                pulse++;
            }
        }
        //System.out.println(Integer.toBinaryString(pulse));
        return pulse;
    }
}
