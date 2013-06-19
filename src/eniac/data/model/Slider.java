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
 * Created on 28.03.2004
 */
package eniac.data.model;

import org.xml.sax.Attributes;

import eniac.io.ITag;
import eniac.io.Tag;
import eniac.io.XMLUtil;

/**
 * @author zoppke
 */
public class Slider extends EData {

    private float _value;

    //============================= lifecycle //===============================

    public Slider() {
        // empty
    }

    public void setAttributes(Attributes attrs) {
        super.setAttributes(attrs);
        _value = XMLUtil.parseFloat(attrs, Tag.VALUE);
    }

    //============================== methods //================================

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

    protected ITag getAttributeName() {
        return Tag.VALUE;
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
