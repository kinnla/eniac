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

	public enum Key {

		/**
		 * unique key identifying this action
		 */
		KEY,

		/**
		 * the button produced from this action.
		 */
		BUTTON,

		/**
		 * The menu item
		 */
		ITEM,

		/**
		 * The model for the button and the menu item
		 */
		MODEL,

		/**
		 * The SID for the name
		 */
		SID_NAME,

		/**
		 * The SID for the short description
		 */
		SID_SHORT_DESCRIPTION,

		/**
		 * in case the action modifies a property registered at the StatusMap,
		 * this is the properties name
		 */
		STATUS_PROPERTY;
	}

	//=============================== lifecycle //=============================

	public void init() {

		// create objects
		ButtonModel model = new DefaultButtonModel();
		JButton button = new JButton(this);
		button.setModel(model);
		button.setText(null);

		// store objects
		putValue(Key.BUTTON.toString(), button);
		putValue(Key.MODEL.toString(), model);
		putValue(Key.ITEM.toString(), new JMenuItem(this));

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
    	String sid = (String)getValue(Key.SID_NAME.toString());
    	Dictionary key = Enum.valueOf(Dictionary.class, sid);
        putValue(Action.NAME, key.getText());
        
        sid =(String)getValue(Key.SID_SHORT_DESCRIPTION.toString());
        key = Enum.valueOf(Dictionary.class, sid);
        putValue(Action.SHORT_DESCRIPTION, key.getText());

        // hide text
        ((AbstractButton)getValue(Key.BUTTON.toString())).setText(null);
    }
}
