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
 * Created on 23.05.2004
 */
package eniac.data.view.sw;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import eniac.data.model.sw.SwitchAndFlag;
import eniac.skin.Descriptor;
import eniac.skin.Skin;

/**
 * @author zoppke
 */
public class OperationSwitchPanel extends SwitchPanel {

    /**
     * @param sw
     */
    public OperationSwitchPanel() {
        // empty
    }

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
        SwitchAndFlag os = ((SwitchAndFlag) _data);
        int value = os.getValue();
        boolean flag = os.isFlag();

        // draw background image. Background is the operation value.
        images = (Image[]) descriptor.get(Skin.Tag.BACK_IMAGE_ARRAY);
        img = images[value];
        g.drawImage(img, x, y, width, height, this);

        // paint foreground image. Foreground is the clear-correct value.
        images = (Image[]) descriptor.get(Skin.Tag.FORE_IMAGE_ARRAY);
        img = flag ? images[1] : images[0];

        // scale rectangle
        Rectangle rect = (Rectangle) descriptor.get(Skin.Tag.RECTANGLE);
        x += rect.x * width / descriptor.getWidth();
        y += rect.y * height / descriptor.getHeight();
        width = width * rect.width / descriptor.getWidth();
        height = height * rect.height / descriptor.getHeight();

        // draw foreground image
        g.drawImage(img, x, y, width, height, this);
    }
}
