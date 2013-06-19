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
import eniac.util.StatusMap;

public class EMenu extends JMenu implements PropertyChangeListener {

	private Dictionary _sid;
	
	public EMenu(String sid) {
		_sid = Enum.valueOf(Dictionary.class, sid);
		StatusMap.getInstance().addListener(Status.LANGUAGE.toString(), this);
		setText(_sid.getText());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setText(_sid.getText());
	}
}
