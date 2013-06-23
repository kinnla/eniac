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

import javax.swing.ButtonModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToggleButton.ToggleButtonModel;

import eniac.util.Status;
import eniac.util.StatusListener;
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
		Status key = (Status) getValue(STATUS_PROPERTY);
		ButtonModel model = new JToggleButton.ToggleButtonModel();
		model.setSelected((Boolean) StatusMap.get(key));

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

		// add listener for status of boolean property bound to this action
		StatusMap.getInstance().addListener(key, new StatusListener() {

			@Override
			public void statusChanged(Status status, Object newValue) {
				// value was toggeled by another party. update selection
				((ToggleButtonModel) getValue(MODEL)).setSelected((boolean) newValue);
			}
		});

		// add listener and init text
		StatusMap.getInstance().addListener(Status.LANGUAGE, new StatusListener() {

			@Override
			public void statusChanged(Status status, Object newValue) {
				// language changed. update action values and hide text
				updateText();
			}
		});
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
		Status key = (Status) getValue(STATUS_PROPERTY);
		StatusMap.set(key, ((ToggleButtonModel) getValue(MODEL)).isSelected());
	}
}
