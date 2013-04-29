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
 * Created on 11.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.model.parent;

import eniac.data.IDManager;
import eniac.data.model.CyclingLights;
import eniac.data.type.ProtoTypes;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Configuration extends ParentData {

    private IDManager _idManager = new IDManager();

    //============================ lifecycle
    // ===================================

    public Configuration() {
        // empty
    }

    public void init() {
        super.init();
    }

    public void dispose() {
        super.dispose();
        _idManager.dispose();
        _idManager = null;
    }

    //============================== methods
    // ===================================

    public IDManager getIDManager() {
        return _idManager;
    }

    public Configuration getConfiguration() {
        return this;
    }

    public CyclingLights getCyclingLights() {
        return (CyclingLights) getGarten()
                .getKind(ProtoTypes.CYCLING_LIGHTS, 0);
    }
}
