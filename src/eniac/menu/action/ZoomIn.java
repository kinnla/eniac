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
 * Created on 13.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import eniac.skin.Skin;
import eniac.util.Status;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ZoomIn extends EAction implements PropertyChangeListener {

    public ZoomIn() {
        Status.getInstance().addListener(this);
    }

    private int getNewHeight() {

        // get current height and zoomSteps
        int height = Status.getInt("zoomed_height");
        Skin skin = (Skin) Status.get("skin");
        int[] steps = skin.getZoomSteps();

        // search index of current height in zoomSteps
        int index = Arrays.binarySearch(steps, height);
        if (index < 0) {
            // we are between 2 steps. Choose the lower one.
            index = -index - 2;
        }
        // increse step, but check that we stay inside array bounds
        return steps[Math.min(index + 1, steps.length - 1)];
    }

    public void actionPerformed(ActionEvent e) {
        Status.set("zoomed_height", getNewHeight());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("zoomed_height")) {
            int height = Status.getInt("zoomed_height");
            setEnabled(getNewHeight() != height);
        } else {
            super.propertyChange(evt);
        }
    }
}
