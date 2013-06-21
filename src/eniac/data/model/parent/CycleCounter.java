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
 * Created on 30.03.2004
 */
package eniac.data.model.parent;

import java.util.Observable;
import java.util.Observer;

import eniac.data.model.EData;
import eniac.data.model.sw.Switch;
import eniac.data.type.EType;

/**
 * @author zoppke
 */
public class CycleCounter extends ParentData implements Observer {

    public CycleCounter() {
        // empty
    }

    public void init() {
        super.init();
        EData clear = getGarten().getKind(EType.CYCLE_COUNTER_CLEAR, 0);
        clear.addObserver(this);
    }

    public void setValue(int value) {
        EData[] ciphers = getGarten().getKinder(EType.CIPHER);
        for (int i = 0; i < ciphers.length; ++i) {
            Switch c = (Switch) ciphers[i];
            c.setValue(value % 10);
            value /= 10;
        }
    }

    public void incrementValue() {
        setValue(getValue() + 1);
    }

    public int getValue() {
        int value = 0;
        EData[] ciphers = getGarten().getKinder(EType.CIPHER);
        for (int i = ciphers.length - 1; i >= 0; --i) {
            Switch c = (Switch) ciphers[i];
            value *= 10;
            value += c.getValue();
        }
        return value;
    }

    /**
     * @param data
     * @see eniac.data.DataListener#dataChanged(eniac.data.EData)
     */
    public void update(Observable o, Object args) {
        if (((Switch) o).isValue()) {
            setValue(0);
        }
    }

}
