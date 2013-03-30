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

import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ChoiceProperty extends Property {

    private Object[] _values;

    private JComboBox _box;

    public ChoiceProperty(String name, Object[] values, int selection) {
        _name = name;
        _values = values;
        _box = new JComboBox(_values);
        _box.setSelectedIndex(selection);
    }

    /*
     * (non-Javadoc)
     * 
     * @see eniac.data.property.AbstractProperty#getValueComponent()
     */
    public JComponent getValueComponent() {
        return _box;
    }

    public int getSelection() {
        return _box.getSelectedIndex();
    }
}
