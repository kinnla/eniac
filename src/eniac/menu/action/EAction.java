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
 * EniacAction.java
 * 
 * Created on 06.02.2004
 */
package eniac.menu.action;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import eniac.lang.Dictionary;
import eniac.util.Status;
import eniac.util.StatusListener;
import eniac.util.StatusMap;

/**
 * @author zoppke
 */
public abstract class EAction extends AbstractAction {

	/*
	 * ======================== keys to store objects =====================
	 */

	public static final String

		/**
		 * unique key identifying this action
		 */
		KEY = "key",

		/**
		 * the button produced from this action.
		 */
		BUTTON = "button",

		/**
		 * The menu item
		 */
		ITEM = "item",

		/**
		 * The model for the button and the menu item
		 */
		MODEL = "model",

		/**
		 * The SID for the name
		 */
		SID_NAME = "SID_Name",

		/**
		 * The SID for the short description
		 */
		SID_SHORT_DESCRIPTION = "SID_ShortDescription",

		/**
		 * in case the action modifies a property registered at the StatusMap,
		 * this is the properties name
		 */
		STATUS_PROPERTY = "status_property";
	

	//=============================== lifecycle //=============================

	public void init() {

		// create objects
		ButtonModel model = new DefaultButtonModel();
		JButton button = new JButton(this);
		button.setModel(model);
		button.setText(null);

		// store objects
		putValue(BUTTON, button);
		putValue(MODEL, model);
		putValue(ITEM, new JMenuItem(this));

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
    
    //=============================== methods //===============================

    protected void updateText() {

		// get values from dictionary and put them
		String sid = (String) getValue(SID_NAME);
		try {
			sid = Enum.valueOf(Dictionary.class, sid).getText();
		} catch (IllegalArgumentException exc) {
			System.out.println("missing SID: " + sid);
		}
		putValue(Action.NAME, sid);

		sid = (String) getValue(SID_SHORT_DESCRIPTION);
		try {
			sid = Enum.valueOf(Dictionary.class, sid).getText();
		} catch (IllegalArgumentException exc) {
			System.out.println("missing SID: " + sid);
		}
		putValue(Action.SHORT_DESCRIPTION, sid);

        // hide text
        ((AbstractButton)getValue(BUTTON)).setText(null);
    }
}
