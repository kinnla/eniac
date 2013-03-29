/*
 * Created on 28.03.2004
 */
package eniac.data.model;

import org.xml.sax.Attributes;

import eniac.io.Tags;
import eniac.io.XMLUtil;

/**
 * @author zoppke
 */
public class Slider extends EData {

    private float _value;

    /////////////////////////////// lifecycle /////////////////////////////////

    public Slider() {
        // empty
    }

    public void setAttributes(Attributes attrs) {
        super.setAttributes(attrs);
        _value = XMLUtil.parseFloat(attrs, Tags.VALUE);
    }

    //////////////////////////////// methods //////////////////////////////////

    public void setValue(float value) {
        if (_value != value) {
            _value = value;
            setChanged();
            notifyObservers(EData.REPAINT);
        }
    }

    public float getValue() {
        return _value;
    }

    protected String getAttributeName() {
        return Tags.VALUE;
    }

    /**
     * @return a string representation of
     */
    public String getAttributes() {
        return super.getAttributes()
                + XMLUtil.wrapAttribute(getAttributeName(), Float
                        .toString(_value));
    }
}