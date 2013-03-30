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
 * Created on 27.03.2004
 */
package eniac.data.control;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import eniac.data.model.sw.Switch;
import eniac.data.view.sw.SwitchPanel;

/**
 * @author zoppke
 */
public class BasicControler implements Controler {

    public BasicControler() {
        // empty constructor
    }

    /////////////////////////// mouselistener methods
    // //////////////////////////

    public void mpressed(MouseEvent e) {
        // no action
    }

    public void mreleased(MouseEvent e) {
        // no action
    }

    public void mdragged(MouseEvent e) {
        // no action
    }

    //////////////////////// static helper methods
    // /////////////////////////////

    protected static SwitchPanel getSwitchPanel(MouseEvent e) {
        return (SwitchPanel) e.getSource();
    }

    protected static Switch getSwitch(MouseEvent e) {
        return (Switch) getSwitchPanel(e).getData();
    }

    protected static void setValue(MouseEvent e, int value) {
        getSwitch(e).setValue(value);
    }

    protected static int getValue(MouseEvent e) {
        return getSwitch(e).getValue();
    }

    protected static void toggleValue(MouseEvent e) {
        getSwitch(e).toggleValue();
    }

    protected static boolean isInside(MouseEvent e) {
        SwitchPanel sp = getSwitchPanel(e);
        Rectangle r = new Rectangle(0, 0, sp.getWidth(), sp.getHeight());
        return r.contains(e.getX(), e.getY());
    }
}
