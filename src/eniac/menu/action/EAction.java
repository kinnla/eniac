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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonModel;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import eniac.lang.Dictionary;
import eniac.util.Status;

/**
 * @author zoppke
 */
public abstract class EAction extends AbstractAction implements
        PropertyChangeListener {

	/*
	 * ======================== keys to store objects =====================
	 */

	/**
	 * unique key identifying this action
	 */
	public static final String KEY = "key";
    
	/**
	 * the button produced from this action.
	 */
	public static final String BUTTON = "button";

	/**
	 * The menu item
	 */
	public static final String ITEM = "item";

	/**
	 * The model for the button and the menu item
	 */
	public static final String MODEL = "model";

	/**
	 * The SID for the name
	 */
	public static final String SID_NAME = "SID_Name";
	
	/**
	 * The SID for the short description
	 */
	public static final String SID_SHORT_DESCRIPTION = "SID_ShortDescription";

	/**
	 * in case the action modifies a property registered at the Status, this is
	 * the properties name
	 */
	public static final String STATUS_PROPERTY = "status_property";

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
        Status.getInstance().addListener(this);
        updateText();
	}
    
    //=============================== methods //===============================

    protected void updateText() {

        // get values from dictionary and put them
    		String name = Dictionary.get((String)getValue(SID_NAME));
        putValue(Action.NAME, name);
        String shortDescription = Dictionary.get((String)getValue(SID_SHORT_DESCRIPTION));
        putValue(Action.SHORT_DESCRIPTION, shortDescription);

        // hide text
        ((AbstractButton)getValue(BUTTON)).setText(null);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("language")) {
            // language changed. update action values and hide text
            updateText();
        }
    }
}
