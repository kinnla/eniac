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
package eniac.data.model.unit;

import java.util.Observable;

import eniac.data.model.sw.Switch;
import eniac.data.type.EType;

/**
 * @author zoppke
 */
public class ConstantTransmitter2 extends Unit {

    /**
     * @return @see eniac.data.model.unit.Unit#getHeaters()
     */
    public Switch getHeaters() {
        return (Switch) getGarten().getKind(EType.HEATERS, 0);
    }

    /**
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub

    }

}
