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
 * Created on 21.04.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;

import eniac.data.type.EType;
import eniac.data.view.parent.ConfigPanel;
import eniac.skin.Descriptor;
import eniac.skin.Skin;
import eniac.util.Status;
import eniac.util.StatusMap;
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
        Skin skin = (Skin) StatusMap.get(Status.SKIN);
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
        StatusMap.set(Status.ZOOMED_HEIGHT, newHeight);
    }
}
