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
package eniac.data.model.sw;

import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;

import eniac.data.model.EData;
import eniac.io.Tags;
import eniac.io.XMLUtil;
import eniac.property.ChoiceProperty;
import eniac.property.Property;

/**
 * @author zoppke
 */
public class SwitchAndFlag extends Switch {

    private static String[] FALSE_TRUE = new String[] {
            Boolean.FALSE.toString(), Boolean.TRUE.toString() };

    private boolean _flag;

    /**
     * @param type
     */
    public SwitchAndFlag() {
        // empty
    }

    public void setAttributes(Attributes attrs) {
        super.setAttributes(attrs);

        // parse clearCorrect from attributes
        //TODO: maybe include name of flag to etype
        _flag = XMLUtil.parseBoolean(attrs, Tags.FLAG);
    }

    public void rotateValue() {
        //		System.out.println("rotate value: " + _value + " -> " + (_value +
        // 1));
        _value = (_value + 1) % _type.getCodes().length;
        if (_value == 0) {
            _flag = true;
        }
        setChanged();
        notifyObservers(EData.REPAINT);
    }

    public void toggleFlag() {
        _flag = !_flag;
        setChanged();
        notifyObservers(EData.REPAINT);
    }

    public boolean isFlag() {
        //System.out.println("flag is checked. Result=" + _flag);
        return _flag;
    }

    public void setFlag(boolean b) {
        if (_flag != b) {
            _flag = b;
            setChanged();
            notifyObservers(EData.REPAINT);
        }
    }

    public String getAttributes() {
        return super.getAttributes()
                + XMLUtil.wrapAttribute(Tags.FLAG, Boolean.toString(_flag));
    }

    public List getProperties() {
        List l = super.getProperties();
        l.add(new ChoiceProperty(Tags.FLAG, FALSE_TRUE, _flag ? 1 : 0));
        return l;
    }

    public void setProperties(List l) {
        Iterator it = l.iterator();
        while (it.hasNext()) {
            Property p = (Property) it.next();
            if (p.getName().equals(Tags.FLAG)) {
                setFlag(((ChoiceProperty) p).getSelection() == 1);
                it.remove();
            }
        }
        super.setProperties(l);
    }

}
