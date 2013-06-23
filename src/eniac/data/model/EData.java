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
 * Created on 25.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.xml.sax.Attributes;

import eniac.data.IDManager;
import eniac.data.model.parent.Configuration;
import eniac.data.model.parent.ParentData;
import eniac.data.model.unit.Unit;
import eniac.data.type.EType;
import eniac.data.view.EPanel;
import eniac.io.XMLUtil;
import eniac.property.ConditionedProperty;
import eniac.property.ConstantProperty;
import eniac.property.Property;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class EData extends Observable implements Comparable<EData> {

	// static key indicating that that a repaint is recommended.
	public static final String REPAINT = "repaint"; //$NON-NLS-1$

	public static final String PAINT_IMMEDIATELY = "paint_immediately"; //$NON-NLS-1$

	public enum Tag {
		// tags
		ENIAC, PATH, NAME,

		// attributes
		ID, VALUE, NUMBER, POWER, PARTNER, LOCATION, IO, GRID, SIZE, INDEX, FLAG,

		// attribute values
		IN, OUT, BOTH, FALSE, TRUE,
	}

	// ============================= fields
	// =====================================

	// type of this dataObject as encoded by class DataTypes.
	// parsed from xml-tag
	protected EType _type;

	// unique id of this dataObject. All dataObjects have different ids
	// parsed from xml-attributes
	protected int _id;

	// name of this dataObject.
	// parsed from xml-attributes
	protected String _name;

	// numbers in parent's grid for layouting
	// parsed from xml-attributes
	protected int[] _gridNumbers;

	// index at parent.
	// this is not necessarily the same index as in the kindergarten.
	// two children may share the same index
	// (eg. two programconnectors in a tray)
	protected int _index;

	// parent dataObject. Configuration is root dataObject.
	protected ParentData _parent = null;

	// ============================= lifecycle
	// ==================================

	// default Constructor
	public EData() {
		// empty constructor
	}

	// constructor used by the parser's default handler
	public void setAttributes(Attributes attrs) {
		_id = XMLUtil.parseInt(attrs, Tag.ID);
		_index = XMLUtil.parseInt(attrs, Tag.INDEX);
		_name = XMLUtil.parseString(attrs, Tag.NAME);
		_gridNumbers = XMLUtil.parseIntArray(attrs, Tag.GRID);
	}

	/**
	 * initializes this dataObject. This default method just does nothing and
	 * should be overwritten by subclasses in case.
	 */
	public void init() {
		// register at idManager
		getConfiguration().getIDManager().put(this);
	}

	public void dispose() {
		// unregister at idManager
		getConfiguration().getIDManager().remove(_id);
	}

	// =========================== getter and setter
	// ============================

	public int getID() {
		return _id;
	}

	public void setID(int id) {
		_id = id;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public ParentData getParent() {
		return _parent;
	}

	public void setParent(ParentData pd) {
		_parent = pd;
	}

	public void setType(EType type) {
		_type = type;
	}

	public EType getType() {
		return _type;
	}

	public int[] getGridNumbers() {
		return _gridNumbers;
	}

	public void setIndex(int index) {
		_index = index;
	}

	public int getIndex() {
		return _index;
	}

	// =============================== methods
	// ==================================

	public Configuration getConfiguration() {
		return getParent().getConfiguration();
	}

	public boolean hasPower() {
		EData parent = getParent();
		if (parent instanceof Configuration) {
			Configuration config = (Configuration) parent;
			Unit unit = config.getUnit(_gridNumbers[0]);
			return unit.hasPower();
		}
		return parent.hasPower();
	}

	/**
	 * Returns the name of this dataObject. Useful to add this to a JList.
	 * 
	 * @return the string containing the name of this dataObject.
	 */
	public String toString() {
		return _name;
	}

	public int compareTo(EData data) {
		// compare by type, then by index
		int i = _type.compareTo(data.getType());
		if (i == 0) {
			i = _index - data.getIndex();
		}
		return i;
	}

	public List<Property> getProperties() {
		List<Property> l = new LinkedList<>();
		l.add(new ConstantProperty(Tag.NAME.toString(), _name));
		l.add(new ConstantProperty(Tag.ID.toString(), Integer.toString(_id)));
		return l;
	}

	public void setProperties(List<Property> l) {
		for (Property p : l) {
			if (p.getName().equals(Tag.NAME.name().toLowerCase())) {
				_name = ((ConditionedProperty) p).getValue();
			}
		}
	}

	public EPanel makePanel() {
		EPanel panel = _type.makeEPanel();
		panel.setData(this);
		return panel;
	}

	protected void assertInit(EData data) {
		IDManager idManager = getConfiguration().getIDManager();
		if (!idManager.containsID(data)) {
			data.init();
		}
	}

	// ============================= observer stuff
	// =============================

	public void addObserverToTree(Observer o) {
		addObserver(o);
	}

	// =========================== xml methods
	// ==================================

	/**
	 * Returns a String representing all Attributes and values for xml output.
	 * Over here the basic attributes <code>id</code> and <code>name</code> are
	 * added. If subclasses have more attributes to encode, then they should
	 * overwrite this method the following way: <br>
	 * 1. do a super-call to this method <br>
	 * 2. append additional attributes to the returned string <br>
	 * 3. return the string to caller. <br>
	 * 
	 * @return a <code>String</code> containing all attributes and values.
	 */
	protected String getAttributes() {
		return XMLUtil.wrapAttribute(Tag.ID, Integer.toString(_id)) + XMLUtil.wrapAttribute(Tag.NAME, _name)
				+ XMLUtil.wrapAttribute(Tag.GRID, StringConverter.toString(_gridNumbers))
				+ XMLUtil.wrapAttribute(Tag.INDEX, Integer.toString(_index));
	}

	/**
	 * Returns a list containing all Tag and child-Tag of this dataObject.
	 * 
	 * @return a <code>List</code> containing all tags.
	 */
	public void appendTags(List<String> l, int indent) {

		// if low indentation level, write comment line
		if (indent <= 2) {
			XMLUtil.appendCommentLine(l, indent, getName());
		}
		// add open-close-tag, a tag that closes in the same line.
		l.add(getOpenCloseTag(indent));
	}

	protected String getOpenTag(int indent) {
		return XMLUtil.TABS[indent] + XMLUtil.wrapOpenTag(_type.name().toLowerCase() + getAttributes());
	}

	protected String getCloseTag(int indent) {
		return XMLUtil.TABS[indent] + XMLUtil.wrapCloseTag(_type.name().toLowerCase());
	}

	protected String getOpenCloseTag(int indent) {
		return XMLUtil.TABS[indent] + XMLUtil.wrapOpenCloseTag(_type.name().toLowerCase() + getAttributes());
	}
}
