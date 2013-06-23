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

import java.util.List;

import org.xml.sax.Attributes;

import eniac.data.model.EData;
import eniac.io.XMLUtil;
import eniac.property.ChoiceProperty;
import eniac.property.Property;

/**
 * @author zoppke
 */
public class SwitchAndFlag extends Switch {

	private static String[] FALSE_TRUE = {EData.Tag.FALSE.name().toLowerCase(), EData.Tag.TRUE.name().toLowerCase()};

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
		// TODO: maybe include name of flag to etype
		_flag = XMLUtil.parseBoolean(attrs, Tag.FLAG);
	}

	public void rotateValue() {
		// System.out.println("rotate value: " + _value + " -> " + (_value +
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
		// System.out.println("flag is checked. Result=" + _flag);
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
		return super.getAttributes() + XMLUtil.wrapAttribute(Tag.FLAG, Boolean.toString(_flag));
	}

	public List<Property> getProperties() {
		List<Property> l = super.getProperties();
		l.add(new ChoiceProperty(Tag.FLAG.name(), FALSE_TRUE, _flag ? 1 : 0));
		return l;
	}

	public void setProperties(List<Property> l) {
		for (Property p : l) {
			if (p.getName().equalsIgnoreCase(Tag.FLAG.name())) {
				setFlag(((ChoiceProperty) p).getSelection() == 1);
// it.remove(); ===> need to remove from list ???
			}
		}
		super.setProperties(l);
	}

}
