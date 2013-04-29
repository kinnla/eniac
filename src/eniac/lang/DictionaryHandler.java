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

import java.lang.reflect.Field;
import java.util.Hashtable;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.io.Progressor;
import eniac.io.Tags;
import eniac.io.XMLUtil;
import eniac.log.Log;

/**
 * @author zoppke
 */
public class DictionaryHandler extends DefaultHandler {

    //=============================== fields
    // ===================================

    // language to load
    private Hashtable _table = new Hashtable();

    // current entry
    private String _entry;

    // character data as parsed by characters()
    private String _cdata = null;

    // flag indicating whether we are reading whitespace
    private boolean _readWhitespace = false;

    public DictionaryHandler() {
        // empty
    }

    //========================= defaultHandler methods //======================

    public void startDocument() {
        Progressor.getInstance().setText(Dictionary.DICTIONARY_LOADING);
        Progressor.getInstance().setProgress(0,
                Dictionary.class.getFields().length);
    }

    public void endDocument() {

        // init words
        Field[] fields = Dictionary.class.getFields();
        for (int i = 0; i < fields.length; ++i) {
            String key = fields[i].getName().toLowerCase();
            String value = (String) _table.get(key);
            if (value == null) {
                value = key;
                Log.log("missing key: " + key); //$NON-NLS-1$
            }
            try {
                fields[i].set(null, value);
            } catch (Exception e) {
                // this should never occure, because only static strings
                // in class Words.
                e.printStackTrace();
            }
        }
        _table = null;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {
        //System.out.println(qName);
        try {
            if (qName.equals(Tags.ENTRY)) {
                // set current entry
                _entry = XMLUtil.parseString(attrs, Tags.KEY);
                _readWhitespace = true;
            }
        } catch (Exception e) {
            // important: catch any exception and print its tree.
            // otherwise you won't get the error's source.
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        try {
            if (qName.equals(Tags.ENTRY)) {
                // read character data. If data is null, take i as empty string
                if (_cdata == null) {
                    _cdata = ""; //$NON-NLS-1$
                }
                // trim data from whitespace and add to language
                _cdata = _cdata.trim();
                _table.put(_entry, _cdata);
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
            } else {
                _cdata += new String(ch, start, length);
            }
        }
    }
}
