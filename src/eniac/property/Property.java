/*
 * Created on 20.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.property;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public abstract class Property {

    protected String _name;

    public String getName() {
        return _name;
    }

    public JLabel getNameLabel() {
        return new JLabel(_name);
    }

    public abstract JComponent getValueComponent();
}