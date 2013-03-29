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
    private Proxy _proxy = new Proxy();

    // flag indicating if we are inside the proxy tag
    private boolean _inProxy = false;

    public ProxyHandler() {
        // empty
    }

    public Proxy getProxy() {
        return _proxy;
    }

    public void reset() {
        _proxy = new Proxy();
        _inProxy = false;
        _cdata = null;
    }

    /////////////////////////// defaultHandler methods
    // /////////////////////////

    public void startElement(String uri, String localName, String qName,
            Attributes attr) throws SAXException {

        try {
            // in case of proxy tag, set flag.
            if (!_inProxy && qName.equals(Tags.PROXY)) {
                _inProxy = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

        // ignore elements, if we are not in the proxy tag
        if (_inProxy) {
            try {
                if (qName.equals(Tags.PROXY)) {

                    // end of proxy. reset flag.
                    _inProxy = false;
                } else {

                    // otherwise store attribute value pair in the hashtable
                    _proxy.put(qName, _cdata.trim());
                    _cdata = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SAXException(e);
            }
        }
    }

    public void characters(char[] ch, int start, int length) {
        // if we are in the proxy-tag, parse character data into temp variable.
        if (_inProxy) {
            String s = new String(ch, start, length);
            if (_cdata == null) {
                _cdata = s;
            } else {
                _cdata += s;
            }
        }
    }

}