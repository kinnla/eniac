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
 * Created on 23.05.2004
 */
package eniac.data.type;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.Manager;
import eniac.io.IOUtil;
import eniac.io.XMLUtil;
import eniac.log.Log;
import eniac.util.EProperties;

/**
 * @author zoppke
 */
public class TypeHandler extends DefaultHandler {

    //=============================== fields
    // ===================================

    private EType _type;

    private List<String> _listOfCodes = new LinkedList<>();

    // character data as parsed by characters()
    private String _cdata = null;

    // flag indicating whether we are reading whitespace
    private boolean _readWhitespace = false;

    //============================= constructor
    // ================================

    public TypeHandler() {
        // empty
    }

    //========================== defaultHandler methods
    // ========================

    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {
//        System.out.println(qName);
        try {
            if (qName.equalsIgnoreCase(EType.Tag.TYPE.toString())) {
                // create new EType by name
                String name = XMLUtil.parseString(attrs, EType.Tag.NAME);
                _type = Enum.valueOf(EType.class, name.toUpperCase());

            } else if (qName.equalsIgnoreCase(EType.Tag.CODES.toString())) {
                // init list of codes
                _listOfCodes.clear();
                // set codeName
                String codeName = XMLUtil.parseString(attrs, EType.Tag.NAME).toUpperCase();
                _type.setCodeName(Enum.valueOf(EType.Tag.class, codeName));

            } else if (qName.equalsIgnoreCase(EType.Tag.MODEL.toString()) || qName.equalsIgnoreCase(EType.Tag.VIEW.toString())
                    || qName.equalsIgnoreCase(EType.Tag.CODE.toString())) {
                // start reading whitespace
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
            if (qName.equalsIgnoreCase(EType.Tag.TYPE.toString())) {
                // finished parsing this type. Set it to Prototypes.
             //   ProtoTypes.setType(_type);

            } else if (qName.equalsIgnoreCase(EType.Tag.CODES.toString())) {
                // convert list of codes to an array and set it to type
            	String[] codes = new String[_listOfCodes.size()];
                _listOfCodes.toArray(codes);
                _type.setCodes(codes);

            } else if (qName.equalsIgnoreCase(EType.Tag.MODEL.toString())) {
                // set edata class and stop reading whitespace
                _type.setEDataClass(_cdata);
                _cdata = null;
                _readWhitespace = false;

            } else if (qName.equalsIgnoreCase(EType.Tag.VIEW.toString())) {
                // set epanel class and stop reading whitespace
                _type.setEPanelClass(_cdata);
                _cdata = null;
                _readWhitespace = false;

            } else if (qName.equalsIgnoreCase(EType.Tag.CODE.toString())) {
                // add code to list of codes and stop reading whitespace
                _listOfCodes.add(_cdata);
                _cdata = null;
                _readWhitespace = false;
            }
        } catch (Exception e) {
            // in case of exception, print stack trace and rethrow as sax
            e.printStackTrace();
            throw new SAXException(e);
        }
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
    
    public static void loadTypes(){
    	String file = EProperties.getInstance().getProperty("PROTOTYPES_FILE");
        InputStream in = Manager.class.getClassLoader().getResourceAsStream(file);
        TypeHandler handler = new TypeHandler();
        try {
            IOUtil.parse(in, handler);
        } catch (Exception e) {
            System.out.println("Error in initializing types"); //$NON-NLS-1$
        }
    }

    
}
