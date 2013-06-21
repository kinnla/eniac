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
 * SkinTags.java
 * 
 * Created on 06.02.2004
 */
package eniac.io;

/**
 * @author zoppke
 */
public enum Tag implements ITag{

	/*
	 * ========================= general =======================
	 */

	X, Y, FALSE, TRUE,

	/*
	 * ========================= menu =======================
	 */

	MENU, GROUP, ACTION, ICON, NUMBER_OF_ACTIONS,

	/*
	 * ========================= language =======================
	 */

	FOLDER;

	/*
	 * ========================= type =======================
	 */

	

	@Override
	public String toLowerCase() {
		return name().toLowerCase();
	}
	
	public static Tag from(String s) {
		try {
			return Enum.valueOf(Tag.class, s.toUpperCase());
		}catch(IllegalArgumentException exc) {
			return null;
		}
	}
}
