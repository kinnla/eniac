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

/**
 * @author zoppke
 */
public class ToggleAction extends EAction {

	/*
	 * ============================ lifecycle =================================
	 */

	public void init() {

        // create buttonModel and init selection state
		String key = (String) getValue(STATUS_PROPERTY);
        ButtonModel model = new JToggleButton.ToggleButtonModel();
        model.setSelected((Boolean)Status.get(key));

        // create button
        JToggleButton button = new JToggleButton(this);
        button.setText(null);
        button.setModel(model);

        // create menuItem
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(this);
        item.setModel(model);

		// store objects
        putValue(BUTTON, button);
        putValue(MODEL, model);
        putValue(ITEM, item);

        // add listener and init text
        Status.getInstance().addListener(this);
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
    		String key = (String) getValue(STATUS_PROPERTY);
        Status.set(key, ((ToggleButtonModel) getValue(MODEL)).isSelected());
    }

    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
		String key = (String) getValue(STATUS_PROPERTY);
        if (evt.getPropertyName().equals(key)) {
            // value was toggeled by another party. update selection
        		ToggleButtonModel model = (ToggleButtonModel) getValue(MODEL);
        		model.setSelected((Boolean)Status.get(key));
        }
    }
}
