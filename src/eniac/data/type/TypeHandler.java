/*
 * Created on 23.05.2004
 */
package eniac.data.type;

import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.io.Tags;
import eniac.io.XMLUtil;
import eniac.log.Log;

/**
 * @author zoppke
 */
public class TypeHandler extends DefaultHandler {

    ///////////////////////////////// fields
    // ///////////////////////////////////

    private EType _type;

    private List _listOfCodes = new Vector();

    // character data as parsed by characters()
    private String _cdata = null;

    // flag indicating whether we are reading whitespace
    private boolean _readWhitespace = false;

    /////////////////////////////// constructor
    // ////////////////////////////////

    public TypeHandler() {
        // empty
    }

    //////////////////////////// defaultHandler methods
    // ////////////////////////

    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {
        //System.out.println(qName);
        try {
            if (qName.equals(Tags.TYPE)) {
                // create new EType by name
                String name = XMLUtil.parseString(attrs, Tags.NAME);
                _type = new EType(name);

            } else if (qName.equals(Tags.CODES)) {
                // init list of codes
                _listOfCodes.clear();
                // set codeName
                String codeName = XMLUtil.parseString(attrs, Tags.NAME);
                _type.setCodeName(codeName);

            } else if (qName.equals(Tags.MODEL) || qName.equals(Tags.VIEW)
                    || qName.equals(Tags.CODE)) {
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
            if (qName.equals(Tags.TYPE)) {
                // finished parsing this type. Set it to Prototypes.
                ProtoTypes.setType(_type);

            } else if (qName.equals(Tags.CODES)) {
                // convert list of codes to an array and set it to type
                String[] codes = new String[_listOfCodes.size()];
                _listOfCodes.toArray(codes);
                _type.setCodes(codes);

            } else if (qName.equals(Tags.MODEL)) {
                // set edata class and stop reading whitespace
                _type.setEDataClass(_cdata);
                _cdata = null;
                _readWhitespace = false;

            } else if (qName.equals(Tags.VIEW)) {
                // set epanel class and stop reading whitespace
                _type.setEPanelClass(_cdata);
                _cdata = null;
                _readWhitespace = false;

            } else if (qName.equals(Tags.CODE)) {
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
}