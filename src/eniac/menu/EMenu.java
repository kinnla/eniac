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
package eniac.menu;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;

import eniac.lang.Dictionary;
import eniac.util.Status;

public class EMenu extends JMenu implements PropertyChangeListener {

	private String _sid;
	
	public EMenu(String sid) {
		_sid = sid;
		Status.getInstance().addListener("language", this);
		setText(Dictionary.get(_sid));
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setText(Dictionary.get(_sid));
	}
}
