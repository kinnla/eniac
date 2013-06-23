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
 * Created on 23.02.2004
 */
package eniac.menu.action.gui;

import java.awt.LayoutManager;
import java.awt.Window;

import javax.swing.JPanel;

/**
 * @author zoppke
 */
public abstract class DialogPanel extends JPanel {

	protected Window _window = null;

	public DialogPanel(LayoutManager lm) {
		super(lm);
	}

	public abstract void performCancelAction();

	public void setWindow(Window window) {
		_window = window;
	}

}
