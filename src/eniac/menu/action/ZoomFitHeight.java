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
public class ZoomFitHeight extends EAction {

    /**
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        // compute new height
        ConfigPanel cp = EFrame.getInstance().getConfigPanel();
        JScrollPane scrollPane = (JScrollPane) cp.getParent().getParent();
        int newHeight = scrollPane.getHeight();

        // check, if new height affords scrollbars
        EType configType = cp.getData().getType();
        Skin skin = (Skin) Status.get("skin");
        int lod = skin.getLodByHeight(newHeight);
        Descriptor d = configType.getDescriptor(lod);
        int newWidth = d.getWidth() * newHeight / d.getHeight();
        if (newWidth > scrollPane.getWidth()) {
            // adjust height
            newHeight -= scrollPane.getHorizontalScrollBar().getHeight();
        }
        // subtract some pixels.
        newHeight -= 4;

        // set new height.
        Status.set("zoomed_height", newHeight);
    }
}