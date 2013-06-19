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
 * Created on 20.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.property;

import javax.swing.JComponent;
import javax.swing.JTextField;

import eniac.io.ITag;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ConstantProperty extends Property {

    private String _value;

    public ConstantProperty(ITag name, String value) {
        _name = name;
        _value = value;
    }

    public String getValue() {
        return _value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eniac.data.property.AbstractProperty#getValueComponent()
     */
    public JComponent getValueComponent() {
        JTextField field = new JTextField(_value);
        field.setEditable(false);
        field.setFocusable(false);
        return field;
    }
}
