/*
 * Created on 20.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.property;

import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ConstantProperty extends Property {

    private String _value;

    public ConstantProperty(String name, String value) {
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