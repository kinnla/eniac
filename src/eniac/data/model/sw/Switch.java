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
 * Created on 21.03.2004
 */
package eniac.data.model.sw;

import java.util.List;

import org.xml.sax.Attributes;

import eniac.data.io.DataParsingException;
import eniac.data.model.EData;
import eniac.data.type.EType;
import eniac.io.XMLUtil;
import eniac.property.ChoiceProperty;
import eniac.property.Property;

/**
 * @author zoppke
 */
public class Switch extends EData {

    protected int _value;

    private boolean _enabled = true;

    //============================== lifecycle
    // =================================

    public Switch() {
        // empty
    }

    public void setAttributes(Attributes attrs) {
        super.setAttributes(attrs);

        // parse value from attributes
        Enum<?>[] codes = _type.getCodes();
        int value = XMLUtil.parseInt(attrs, _type.getCodeName(), codes);

        // If value is in bounds, set it. Otherwise throw exception.
        if (isInbound(value)) {
            setValue(value);
        } else {
            throw new DataParsingException(value, _type.getCodeName(),
                    getClass());
        }
    }

    //============================== methods
    // ===================================

    public void setValue(int value) {
        if (_value != value) {
            _value = value;
            setChanged();
            notifyObservers(EData.REPAINT);
        }
    }

    public int getValue() {
        return _value;
    }

    public void toggleValue() {
        _value = 1 - _value;
        setChanged();
        notifyObservers(EData.REPAINT);
    }

    public boolean isValue() {
        return _value == 1;
    }

    public EType.Tag encode() {
    	EType.Tag[] codes = _type.getCodes();
        return codes[_value];
    }

    protected boolean isInbound(int value) {
    	EType.Tag[] codes = _type.getCodes();
        return value >= 0 && value < codes.length;
    }

    public String getAttributes() {
        return super.getAttributes()
                + XMLUtil.wrapAttribute(_type.getCodeName(), encode().toString());
    }

    public List<Property> getProperties() {
        List<Property> l = super.getProperties();
        EType.Tag[] codes = _type.getCodes();
        l.add(new ChoiceProperty(_type.getCodeName(), codes, _value));
        return l;
    }

    public void setProperties(List<Property> l) {
        for (Property p : l) { 
            if (p.getName().equals(_type.getCodeName().name().toLowerCase())) {
                setValue(((ChoiceProperty) p).getSelection());
                // it.remove(); ===> need to remove ???
            }
        }
        super.setProperties(l);
    }

    public void setEnabled(boolean b) {
        _enabled = b;
    }

    public boolean isEnabled() {
        return _enabled;
    }
}
