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
 * Created on 28.03.2004
 */
package eniac.data.control;

import java.awt.event.MouseEvent;

/**
 * @author zoppke
 */
public interface Controler {

	public static final BasicControler NONE = new BasicControler();

	public void mpressed(MouseEvent e);

	public void mreleased(MouseEvent e);

	public void mdragged(MouseEvent e);

}
