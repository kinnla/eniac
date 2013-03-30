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

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.xml.sax.Attributes;

import eniac.data.model.parent.Configuration;
import eniac.data.model.parent.ParentData;
import eniac.data.model.unit.Unit;
import eniac.data.type.EType;
import eniac.data.view.EPanel;
import eniac.io.Tags;
import eniac.io.XMLUtil;
import eniac.property.ConditionedProperty;
import eniac.property.ConstantProperty;
import eniac.property.Property;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class EData extends Observable implements Comparable {

    // static key indicating that that a repaint is recommended.
    public static final String REPAINT = "repaint"; //$NON-NLS-1$

    public static final String PAINT_IMMEDIATELY = "paint_immediately"; //$NON-NLS-1$

    /////////////////////////////// fields
    // /////////////////////////////////////

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

    /////////////////////////////// lifecycle
    // //////////////////////////////////

    // default Constructor
    public EData() {
        // empty constructor
    }

    // constructor used by the parser's default handler
    public void setAttributes(Attributes attrs) {
        _id = XMLUtil.parseInt(attrs, Tags.ID);
        _index = XMLUtil.parseInt(attrs, Tags.INDEX);
        _name = XMLUtil.parseString(attrs, Tags.NAME);
        _gridNumbers = XMLUtil.parseIntArray(attrs, Tags.GRID);
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

    ///////////////////////////// getter and setter
    // ////////////////////////////

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

    ///////////////////////////////// methods
    // //////////////////////////////////

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

    public int compareTo(Object o) {
        // compare by type, then by index
        EData data = (EData) o;
        int i = _type.compareTo(data.getType());
        if (i == 0) {
            i = _index - data.getIndex();
        }
        return i;
    }

    public List getProperties() {
        List l = new Vector();
        l.add(new ConstantProperty(Tags.NAME, _name));
        l.add(new ConstantProperty(Tags.ID, Integer.toString(_id)));
        return l;
    }

    public void setProperties(List l) {
        Iterator it = l.iterator();
        while (it.hasNext()) {
            Property p = (Property) it.next();
            if (p.getName() == Tags.NAME) {
                _name = ((ConditionedProperty) p).getValue();
            }
        }
    }

    public EPanel makePanel() {
        EPanel panel = _type.makeEPanel();
        panel.setData(this);
        return panel;
    }

    /////////////////////////////// observer stuff
    // /////////////////////////////

    public void addObserverToTree(Observer o) {
        addObserver(o);
    }

    ///////////////////////////// xml methods
    // //////////////////////////////////

    /**
     * Returns a String representing all Attributes and values for xml output.
     * Over here the basic attributes <code>id</code> and <code>name</code>
     * are added. If subclasses have more attributes to encode, then they should
     * overwrite this method the following way: <br>
     * 1. do a super-call to this method <br>
     * 2. append additional attributes to the returned string <br>
     * 3. return the string to caller. <br>
     * 
     * @return a <code>String</code> containing all attributes and values.
     */
    protected String getAttributes() {
        return XMLUtil.wrapAttribute(Tags.ID, Integer.toString(_id))
                + XMLUtil.wrapAttribute(Tags.NAME, _name)
                + XMLUtil.wrapAttribute(Tags.GRID, StringConverter
                        .toString(_gridNumbers))
                + XMLUtil.wrapAttribute(Tags.INDEX, Integer.toString(_index));
    }

    /**
     * Returns a list containing all Tags and child-Tags of this dataObject.
     * 
     * @return a <code>List</code> containing all tags.
     */
    public void appendTags(List l, int indent) {

        // if low indentation level, write comment line
        if (indent <= 2) {
            XMLUtil.appendCommentLine(l, indent, getName());
        }
        // add open-close-tag, a tag that closes in the same line.
        l.add(getOpenCloseTag(indent));
    }

    protected String getOpenTag(int indent) {
        return XMLUtil.TABS[indent]
                + XMLUtil.wrapOpenTag(_type.getName() + getAttributes());
    }

    protected String getCloseTag(int indent) {
        return XMLUtil.TABS[indent] + XMLUtil.wrapCloseTag(_type.getName());
    }

    protected String getOpenCloseTag(int indent) {
        return XMLUtil.TABS[indent]
                + XMLUtil.wrapOpenCloseTag(_type.getName() + getAttributes());
    }
}
