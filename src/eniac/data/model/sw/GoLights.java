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
 * Created on 24.04.2004
 */
package eniac.data.model.sw;

import java.util.Observable;
import java.util.Observer;

import eniac.data.model.EData;
import eniac.data.model.parent.Configuration;
import eniac.data.model.unit.Unit;
import eniac.data.type.EType;
import eniac.data.view.EPanel;

/**
 * @author zoppke
 */
public class GoLights extends Switch implements Observer {

    /**
     * @param type
     */
    public GoLights() {
        // empty
    }

    public EPanel makePanel() {

        // create golightsPanel
        EPanel goLightsPanel = super.makePanel();

        // register golightspanel as observer at corresponding heaters
        Configuration config = (Configuration) getParent();
        Unit unit = config.getUnit(_gridNumbers[0]);
        unit.getHeaters().addObserver(goLightsPanel);

        // register this as observer at gobutton
        EData goButton = unit.getGarten().getKind(EType.GO_BUTTON, 0);
        goButton.addObserver(this);

        // return goLightspanel
        return goLightsPanel;
    }

    /**
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        toggleValue();
    }

}
