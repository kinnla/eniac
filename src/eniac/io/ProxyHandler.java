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
 * XMLHandler.java
 * 
 * Created on 11.02.2004
 */
package eniac.io;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author zoppke
 */
public class ProxyHandler extends DefaultHandler {

	// temporary variable for storing parsed character data
	private String _cdata;

	// hashtable to store parsed attribute-value-pairs
	private Proxy _proxy;

	// flag indicating if we are inside the proxy tag
	private boolean _inProxy = false;

	public ProxyHandler() {
		_proxy = new Proxy();
	}

	public Proxy getProxy() {
		return _proxy;
	}

	public void reset() {
		_proxy = new Proxy();
		_inProxy = false;
		_cdata = null;
	}

	// ========================= defaultHandler methods
	// =========================

	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {

		try {
			// in case of proxy tag, set flag.
			if (!_inProxy && qName.equalsIgnoreCase(Proxy.Tag.PROXY.toString())) {
				_inProxy = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SAXException(e);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		// ignore elements, if we are not in the proxy tag
		if (!_inProxy) {
			return;
		}

		// read tag from string
		Proxy.Tag tag;
		try {
			tag = Enum.valueOf(Proxy.Tag.class, qName);
		} catch (IllegalArgumentException e) {
			System.out.println("unknown tag " + qName + "in proxy. Ignoring.");
			return;
		}
		// switch on the tag
		switch (tag) {

		// end of proxy. reset flag.
			case PROXY :
				_inProxy = false;
				break;

			// store tag & value to the proxy and prepare for next element
			default :
				_proxy.put(tag, _cdata.trim());
				_cdata = null;
		}
	}

	public void characters(char[] ch, int start, int length) {
		// if we are in the proxy-tag, parse character data into temp variable.
		if (_inProxy) {
			String s = new String(ch, start, length);
			if (_cdata == null) {
				_cdata = s;
			}
			else {
				_cdata += s;
			}
		}
	}
}
