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
 * Created on 21.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.property;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ConditionedProperty extends Property {

    private String _value;

    private JTextField _field;

    public ConditionedProperty(String name, String value) {
        _name = name;
        _value = value;
    }

    public String getValue() {
        setValue(_field.getText());
        return _value;
    }

    public void setValue(String value) {
        if (checkValue(value)) {
            _value = value;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eniac.data.property.AbstractProperty#getValueComponent()
     */
    public JComponent getValueComponent() {
        _field = new JTextField(_value);
        _field.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                lostFocus();
            }
        });
        return _field;
    }

    protected abstract boolean checkValue(String value);

    void lostFocus() {
        setValue(_field.getText());
        _field.setText(_value);
    }
}
