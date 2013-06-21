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
 * ConfigurationHandler.java
 * 
 * Created on 10.02.2004
 */
package eniac.data.io;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.data.model.EData;
import eniac.data.model.parent.Configuration;
import eniac.data.model.parent.ParentData;
import eniac.data.type.EType;
import eniac.io.Proxy;
import eniac.log.Log;

/**
 * @author zoppke
 */
public class ConfigHandler extends DefaultHandler {

    //================================ fields
    // ==================================

    // stack for the data objects to be parsed
    private Stack<EData> _stack = new Stack<>();

    // reference to the configuration as the root of our dataObject tree.
    // if parsing was not successful, this will stay null.
    private Configuration _configuration = null;

    /**
     * Creates a new configurationHandler
     */
    public ConfigHandler() {
        // empty constructor
    }

    public Configuration getConfiguration() {
        return _configuration;
    }

    //===================== overriding DefaultHandler methods
    // ==================

    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {

        try {
            // read type from string
        	EType type = convertToType(qName);
            if (type == null) {
                return;
            }

            // create dataObject and push it to the stack.
            EData data = type.makeEData();
            data.setAttributes(attrs);
            _stack.push(data);
        } catch (Exception e) {
            // in case of exception, print stack trace and rethrow as sax
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        try {
            // read type from string
        	EType type = convertToType(qName);
            if (type == null) {
                return;
            }

            // pop object from stack
            EData data = _stack.pop();
            if (data instanceof Configuration) {
                // special case: assign configuration field
                _configuration = (Configuration) data;
            } else {
                // add object as new child to the top-of_stack-dataObject.
                ParentData parent = (ParentData) _stack.peek();
                parent.addChild(data);
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

    private EType convertToType(String name){
        try {
        	return Enum.valueOf(EType.class, name.toUpperCase());
        }catch (IllegalArgumentException exc) {
        	
        	// check, if this is a proxy tag
            try {
            	Enum.valueOf(Proxy.Tag.class, name.toUpperCase());
            }
        	catch(IllegalArgumentException exc2) {
        		
        		// completely unknown tag.
        		System.out.println("Ignoring unknown type: "+name);
        	}
        	return null;
        }
    }

}
