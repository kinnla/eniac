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
 * Created on 26.03.2004
 */
package eniac.skin;

import org.xml.sax.Attributes;

/**
 * @author zoppke
 */
public abstract class Creator {

	Creator() {
		// empty
	}

	protected String _cdata = null;

	protected Object _object = null;

	public void characters(String cdata) {
		// cdata.trim();
		if (_cdata == null) {
			_cdata = cdata;
		}
		else {
			_cdata += cdata;
		}
	}

	public abstract void startElement(String name, Attributes attrs);

	public abstract void endElement(String name);

	public Object getObject() {
		return _object;
	}
}
