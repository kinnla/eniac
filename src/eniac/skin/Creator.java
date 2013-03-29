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
        //		cdata.trim();
        if (_cdata == null) {
            _cdata = cdata;
        } else {
            _cdata += cdata;
        }
    }

    public abstract void startElement(String name, Attributes attrs);

    public abstract void endElement(String name);

    public Object getObject() {
        return _object;
    }
}