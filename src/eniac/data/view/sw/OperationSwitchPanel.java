/*
 * Created on 23.05.2004
 */
package eniac.data.view.sw;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import eniac.data.model.sw.SwitchAndFlag;
import eniac.io.Tags;
import eniac.skin.Descriptor;

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
        images = (Image[]) descriptor.get(Tags.BACK_IMAGE_ARRAY);
        img = images[value];
        g.drawImage(img, x, y, width, height, this);

        // paint foreground image. Foreground is the clear-correct value.
        images = (Image[]) descriptor.get(Tags.FORE_IMAGE_ARRAY);
        img = flag ? images[1] : images[0];

        // scale rectangle
        Rectangle rect = (Rectangle) descriptor.get(Tags.RECTANGLE);
        x += rect.x * width / descriptor.getWidth();
        y += rect.y * height / descriptor.getHeight();
        width = width * rect.width / descriptor.getWidth();
        height = height * rect.height / descriptor.getHeight();

        // draw foreground image
        g.drawImage(img, x, y, width, height, this);
    }
}