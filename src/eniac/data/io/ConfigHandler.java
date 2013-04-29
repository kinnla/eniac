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
import eniac.data.type.ProtoTypes;
import eniac.log.Log;

/**
 * @author zoppke
 */
public class ConfigHandler extends DefaultHandler {

    //================================ fields
    // ==================================

    // stack to push nested dataObject to
    private Stack _stack = new Stack();

    // reference to the configuration as the root of our dataObject tree.
    // if parsing was not successfull, this will stay null.
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
            //System.out.println(qName);
            // create dataObject and push it to the stack.
            EType type = ProtoTypes.getType(qName);
            if (type == null) {
                //Log.log("this is not an etype: "+qName); //$NON-NLS-1$
                return;
            }
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
            if (ProtoTypes.getType(qName) != null) {
                // pop object from stack
                Object o = _stack.pop();
                if (o instanceof Configuration) {
                    // special case: assign configuration field
                    _configuration = (Configuration) o;
                } else {
                    // add object as new child to the top-of_stack-dataObject.
                    ParentData parent = (ParentData) _stack.peek();
                    parent.addChild((EData) o);
                }
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

}
