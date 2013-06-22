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
 * Created on 11.04.2004
 */
package eniac.lang;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.io.Progressor;
import eniac.io.XMLUtil;
import eniac.log.Log;

/**
 * @author zoppke
 */
public class DictionaryHandler extends DefaultHandler {

	// =============================== fields
	// ===================================

	// current entry
	private String _key;

	// character data as parsed by characters()
	private String _cdata = null;

	// flag indicating whether we are reading whitespace
	private boolean _readWhitespace = false;

	public DictionaryHandler() {
		// empty
	}

	// ========================= defaultHandler methods //======================

	public void startDocument() {
		Progressor.getInstance().setText(Dictionary.DICTIONARY_LOADING.getText());
		Progressor.getInstance().setProgress(0, Dictionary.class.getFields().length);
	}

	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
		// System.out.println(qName);
		try {
			if (qName.equals(Dictionary.Tag.ENTRY.name().toLowerCase())) {
				// set current entry
				_key = XMLUtil.parseString(attrs, Dictionary.Tag.KEY);
				_readWhitespace = true;
			}
		} catch (Exception e) {
			// important: catch any exception and print its tree.
			// otherwise you won't get the error's source.
			e.printStackTrace();
			throw new SAXException(e);
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		try {
			if (qName.equals(Dictionary.Tag.ENTRY.name().toLowerCase())) {
				// read character data. If data is null, take it as empty string
				if (_cdata == null) {
					_cdata = ""; //$NON-NLS-1$
				}

				// trim data from whitespace and add to language
				try {
					(Enum.valueOf(Dictionary.class, _key)).setText(_cdata.trim());
				} catch (IllegalArgumentException exc) {
					System.out.println("Ignoring unknown dictionary key: " + _key);
				}

				// finish reading: reset string and reset flag.
				_cdata = null;
				_readWhitespace = false;
			}
		} catch (Exception e) {
			// in case of exception, print stack trace and rethrow as sax
			e.printStackTrace();
			throw new SAXException(e);
		}
		Progressor.getInstance().incrementValue();
	}

	public void warning(SAXParseException e) throws SAXException {
		Log.log(e.toString());
	}

	public void error(SAXParseException e) throws SAXException {
		Log.log(e.toString());
	}

	public void characters(char[] ch, int start, int length) {
		if (_readWhitespace) {
			if (_cdata == null) {
				_cdata = new String(ch, start, length);
			}
			else {
				_cdata += new String(ch, start, length);
			}
		}
	}
}
