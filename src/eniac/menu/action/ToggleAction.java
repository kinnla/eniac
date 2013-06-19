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
 * Created on 10.04.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.ButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToggleButton.ToggleButtonModel;

import eniac.util.Status;
import eniac.util.StatusMap;

/**
 * @author zoppke
 */
public class ToggleAction extends EAction {

	/*
	 * ============================ lifecycle =================================
	 */

	public void init() {

        // create buttonModel and init selection state
		Status key = (Status) getValue(Key.STATUS_PROPERTY.toString());
        ButtonModel model = new JToggleButton.ToggleButtonModel();
        model.setSelected((Boolean)StatusMap.get(key));

        // create button
        JToggleButton button = new JToggleButton(this);
        button.setText(null);
        button.setModel(model);

        // create menuItem
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(this);
        item.setModel(model);

		// store objects
        putValue(Key.BUTTON.toString(), button);
        putValue(Key.MODEL.toString(), model);
        putValue(Key.ITEM.toString(), item);

        // add listener and init text
        StatusMap.getInstance().addListener(this);
        updateText();
    }

    /*
     * ============================= methods ================================
     */
	
    /**
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
    		Status key = (Status) getValue(Key.STATUS_PROPERTY.toString());
        StatusMap.set(key, ((ToggleButtonModel) getValue(Key.MODEL.toString())).isSelected());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
		Status key = (Status) getValue(Key.STATUS_PROPERTY.toString());
        if (evt.getPropertyName().equals(key.toString())) {
            // value was toggeled by another party. update selection
        		ToggleButtonModel model = (ToggleButtonModel) getValue(Key.MODEL.toString());
        		model.setSelected((Boolean)StatusMap.get(key));
        }
    }
}
