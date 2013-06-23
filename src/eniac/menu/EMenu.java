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

import javax.swing.JMenu;

import eniac.lang.Dictionary;
import eniac.util.Status;
import eniac.util.StatusListener;

public class EMenu extends JMenu implements StatusListener {

	private Dictionary _sid;

	public EMenu(String sid) {
		_sid = Enum.valueOf(Dictionary.class, sid);
		Status.LANGUAGE.addListener(this);
		setText(_sid.getText());
	}

	public void statusChanged(Status status, Object newValue) {
		setText(_sid.getText());
	}
}
