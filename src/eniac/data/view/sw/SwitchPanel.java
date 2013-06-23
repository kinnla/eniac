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
 * Created on 21.03.2004
 */
package eniac.data.view.sw;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import eniac.data.model.sw.Switch;
import eniac.data.view.EPanel;
import eniac.skin.Descriptor;

/**
 * @author zoppke
 */
public class SwitchPanel extends EPanel {

    // the old value before the mouse was pressed.
    // This will be used to restore the value in case the mouse will be
    // released outside the component.
    protected int _oldValue = 0;

    // indicates whether the left mouse button is currently down.
    // This is useful to determine if one is dragging with right or left button.
    protected boolean _leftDown = false;

    //============================== lifecycle
    // =================================

    public SwitchPanel() {
        // empty
    }

    //=============================== methods
    // ==================================

    protected void paintComponent(Graphics g, int x, int y, int width,
            int height, int lod) {

        // get descriptor. If no descriptor, just return.
        Descriptor descriptor = getDescriptor(lod);
        if (descriptor == null) {
            return;
        }

        // helper variables
        Image img;
        Image[] images;
        Switch sw = (Switch) _data;
        int value = sw.getValue();

        // get array of background images, if defined
        images = (Image[]) descriptor.get(Descriptor.Key.BACK_IMAGE_ARRAY);
        if (images == null) {
            // if no array defined, look for a single background image
            img = (Image) descriptor.get(Descriptor.Key.BACK_IMAGE);
        } else {
            // otherwise take image according to value
            img = images[value];
        }

        if (img == null) {
            // if no image, draw background
            drawBackground(g, x, y, width, height, lod, descriptor);
        } else {
            // paint background image.
            g.drawImage(img, x, y, width, height, this);
        }

        // check, if we have power to paint the foreground
        if (sw.hasPower()) {

            // get array of foreground images, if defined
            images = (Image[]) descriptor.get(Descriptor.Key.FORE_IMAGE_ARRAY);
            if (images == null) {
                // if no array defined, look for a single foreground image
                img = (Image) descriptor.get(Descriptor.Key.FORE_IMAGE);
            } else {
                // otherwise take image according to value
                img = images[value];
            }

            // paint image, if defined
            if (img != null) {

                // define variables
                Rectangle rect = null;
                Rectangle[] rectangles;

                // get array of rectangles, if defined
                rectangles = (Rectangle[]) descriptor.get(Descriptor.Key.RECTANGLE_ARRAY);
                if (rectangles == null) {
                    // if no array defined, look for a single rectangle
                    rect = (Rectangle) descriptor.get(Descriptor.Key.RECTANGLE);
                } else {
                    // otherwise take image according to value
                    rect = rectangles[value];
                }

                // if a rectangle found, scale bounds
                if (rect != null) {
                    x += rect.x * width / descriptor.getWidth();
                    y += rect.y * height / descriptor.getHeight();
                    width = width * rect.width / descriptor.getWidth();
                    height = height * rect.height / descriptor.getHeight();
                }

                // draw foreground image
                g.drawImage(img, x, y, width, height, this);
            }
        }
    }

    public boolean isEnabled() {
        return ((Switch) _data).isEnabled() && super.isEnabled();
    }

    /**
     * Sets the new value to the related IValue-Object. If the new value is -1,
     * the value is set back to the old value. A value of -1 indicates that the
     * mouse-cursor was released outside this component.
     */
    protected void setValue(int value) {
        if (value == -1) {
            ((Switch) _data).setValue(_oldValue);
        } else {
            ((Switch) _data).setValue(value);
        }
    }
}
