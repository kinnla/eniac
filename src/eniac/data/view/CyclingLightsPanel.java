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
 * Created on 03.04.2004
 */
package eniac.data.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import eniac.data.model.CyclingLights;
import eniac.data.model.parent.Configuration;
import eniac.data.model.sw.Switch;
import eniac.data.type.ParentGrid;
import eniac.io.Tags;
import eniac.skin.Descriptor;
import eniac.util.Status;

/**
 * @author zoppke
 */
public class CyclingLightsPanel extends EPanel implements
        PropertyChangeListener {

    private Switch _heaters;

    /**
     * @param data
     */
    public CyclingLightsPanel() {
        // empty
    }

    public void init() {
        super.init();

        // observe heaters of cycling unit to get notified about power switches
        Configuration config = (Configuration) Status.get("configuration");
        _heaters = config.getUnit(_data.getGridNumbers()[0]).getHeaters();
        _heaters.addObserver(this);

        // add this as propertychangelistener to status for receiving
        // simulation-time updates
        Status.getInstance().addListener(this);
    }

    public void dispose() {
        super.dispose();
        Status.getInstance().removeListener(this);
        _heaters.deleteObserver(this);
    }

    protected void paintComponent(Graphics g, int x, int y, int width,
            int height, int lod) {

        // get descriptor. If no descriptor, just return.
        Descriptor descriptor = getDescriptor(lod);
        if (descriptor == null) {
            return;
        }
        // paint bgimage, if defined
        Image bgimage = (Image) descriptor.get(Tags.BACK_IMAGE);
        if (bgimage != null) {
            g.drawImage(bgimage, x, y, width, height, this);
        }
        // if power, paint vertical line
        if (_data.hasPower()) {

            // get variables
            long time = Status.getLong("simulation_time");
            ParentGrid grid = (ParentGrid) _data.getType().getGrid(width,
                    height, lod);
            int gridWidth = grid.xValues[1] - grid.xValues[0];
            int offset = (int) time % CyclingLights.ADDITION_CYCLE;
            int scaledOff = offset * gridWidth / CyclingLights.ADDITION_CYCLE;
            x += grid.xValues[0] + scaledOff;
            Color color = (Color) descriptor.get(Tags.COLOR);

            // paint vertical line
            g.setColor(color);
            g.drawLine(x, y + grid.yValues[0], x, y + grid.yValues[1]);
        }
    }

    /**
     * @param evt
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        // track simulation time for updating cycle
        if (evt.getPropertyName().equals("simulation_time")) {
            // if we are highlightning, paint immediately.
            //			if (Status.getBoolean(Status.HIGHLIGHT_PULSE)) {
            //				paintComponent(getGraphics());
            //			} else {
            // Otherwise call for repaint.
            repaint();
            //			}
        }
    }
}
