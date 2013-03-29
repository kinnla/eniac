/*
 * Created on 21.04.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;

import eniac.data.type.EType;
import eniac.data.view.parent.ConfigPanel;
import eniac.lang.Dictionary;
import eniac.skin.Descriptor;
import eniac.skin.Skin;
import eniac.util.Status;
import eniac.window.EFrame;

/**
 * @author zoppke
 */
public class ZoomFitWidth extends EAction {

    /**
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        // get variables
        ConfigPanel cp = EFrame.getInstance().getConfigPanel();
        JScrollPane scrollPane = (JScrollPane) cp.getParent().getParent();
        int newWidth = scrollPane.getWidth();
        EType configType = cp.getData().getType();
        Skin skin = (Skin) Status.get("skin");
        int lod = cp.getLod();
        Descriptor d = configType.getDescriptor(lod);

        // compute new height according to current lod
        int newHeight = newWidth * d.getHeight() / d.getWidth();

        // compute lod according to new hight
        lod = skin.getLodByHeight(newHeight);

        // compute new height according to new lod
        d = configType.getDescriptor(lod);
        newHeight = newWidth * d.getHeight() / d.getWidth();

        // if new height affords vertical scrollbars,
        // we need to adjust the widht.
        if (newHeight > scrollPane.getHeight()) {
            newWidth -= scrollPane.getVerticalScrollBar().getWidth();
        }
        newWidth -= 4;
        newHeight = newWidth * d.getHeight() / d.getWidth();

        // set new height.
        Status.set("zoomed_height", newHeight);
    }

}