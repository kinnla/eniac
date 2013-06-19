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
 * Created on 28.03.2004
 */
package eniac.data.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import eniac.data.control.Controler;
import eniac.data.model.Slider;
import eniac.io.Tag;
import eniac.skin.Descriptor;

/**
 * @author zoppke
 */
public class SliderPanel extends EPanel implements Controler {

    public SliderPanel() {
        // empty
    }

    protected void paintComponent(Graphics g, int x, int y, int width,
            int height, int lod) {

        // get descriptor. If no descriptor, just return.
        Descriptor descriptor = getDescriptor(lod);
        if (descriptor == null) {
            return;
        }

        // paint bgcolor, if defined
        Color color = (Color) descriptor.get(Tag.COLOR);
        if (color != null) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }

        // paint background image
        Image img = (Image) descriptor.get(Tag.BACK_IMAGE);
        g.drawImage(img, x, y, width, height, this);

        // get variables
        Rectangle rect = (Rectangle) descriptor.get(Tag.RECTANGLE);
        float value = ((Slider) _data).getValue();
        float xFactor = width / (float) descriptor.getWidth();
        float yFactor = height / (float) descriptor.getHeight();

        // check whether horizontal or vertical sliding
        Object o = descriptor.get(Tag.X);
        if (o == null) {

            // vertical sliding. Adjust point
            int y2 = ((Integer) descriptor.get(Tag.X)).intValue();
            x += rect.x * xFactor;
            y += (rect.y + (y2 - rect.y) * value) * yFactor;
        } else {

            // horizontal sliding. Adjust point
            int x2 = ((Integer) o).intValue();
            x += (rect.x + (x2 - rect.x) * value) * xFactor;
            y += rect.y * yFactor;
        }
        // scale bounds
        width = (int) (rect.width * yFactor);
        height = (int) (rect.height * yFactor);

        // paint foreground image
        img = (Image) descriptor.get(Tag.FORE_IMAGE);
        g.drawImage(img, x, y, width, height, this);
    }

    protected Controler getController() {
        return this;
    }

    public void mpressed(MouseEvent e) {
        setValueByPoint(e);
    }

    public void mreleased(MouseEvent e) {
        setValueByPoint(e);
    }

    public void mdragged(MouseEvent e) {
        setValueByPoint(e);
    }

    private void setValueByPoint(MouseEvent e) {

        // get variables
        Descriptor d = getDescriptor(getLod());
        Rectangle rect = (Rectangle) d.get(Tag.RECTANGLE);
        Slider slider = (Slider) _data;
        float value;

        // check whether horizontal or vertical sliding
        Object o = d.get(Tag.X);
        if (o == null) {

            // vertical sliding. Adjust point
            // compute value by y coordinate of click-point
            int y2 = ((Integer) d.get(Tag.X)).intValue();
            float y = e.getY() * d.getHeight() / (float) getHeight();
            y -= rect.height >> 1;
            value = (y - rect.y) / (y2 - rect.y);
        } else {

            // horizontal sliding.
            // compute value by x coordinate of click-point
            int x2 = ((Integer) o).intValue();
            float x = e.getX() * d.getWidth() / (float) getWidth();
            x -= rect.width >> 1;
            value = (x - rect.x) / (x2 - rect.x);
        }

        // adjust value to bounds and set to slider.
        value = Math.min(value, 1);
        value = Math.max(value, 0);
        slider.setValue(value);
    }
}
