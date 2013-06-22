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
 * Created on 11.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.model.parent;

import java.util.LinkedList;
import java.util.List;
import java.util.Observer;

import eniac.data.IDManager;
import eniac.data.KinderGarten;
import eniac.data.model.EData;
import eniac.data.model.unit.Unit;
import eniac.io.Progressor;
import eniac.io.XMLUtil;

/**
 * @author zoppke
 * 
 * Class Unit is the superclass of all units that the eniac consists of. So over
 * here methods and fields are defined that are necessary for them to work
 * together and to display them in several perspectives.
 */
public class ParentData extends EData {

    // object garten to store children
    private KinderGarten _garten = null;

    // list of children to collect them during initialization.
    // When initialization finished, they will moved to the garten.
    protected List<EData> _childList = new LinkedList<>();

    //============================= lifecycle
    // ==================================

    public ParentData() {
        // empty
    }

    public void init() {
        super.init();
        getGarten();
    }

    public void dispose() {
        super.dispose();

        // recursively call dispose on all children
        EData[] children = getGarten().getAllKinder();
        for (int i = 0; i < children.length; ++i) {
            children[i].dispose();
        }

        // remove all children
        removeAllChildren();
    }

    //============================= methods
    // ====================================

    public boolean hasPower() {
        // default status for trunks, trays, configuration
        return true;
    }

    /**
     * Adds a child dataObject to this dataObject
     */
    public void addChild(EData child) {
        // add child to collector and set parent reference
        _childList.add(child);
        child.setParent(this);
    }

    public KinderGarten getGarten() {
        if (_garten == null) {

        	// convert list to garten
            _garten = new KinderGarten(_childList);
            _childList = null;

            // recurse on children and init
            for (EData child : _garten.getAllKinder()) {
            	assertInit(child);
            }
        }
        return _garten;
    }

    public void removeAllChildren() {
        _childList = null;
        _garten = null;
    }

    public EData getChild(int gridx, int gridy) {

        // recurse on all children and check their location in grid
        EData[] children = _garten.getAllKinder();
        for (int i = 0; i < children.length; ++i) {
            int[] numbers = children[i].getGridNumbers();
            if (numbers[0] <= gridx && numbers[2] > gridx
                    && numbers[1] <= gridy && numbers[3] > gridy) {

                // return child
                return children[i];
            }
        }
        return null;
    }

    public Unit getUnit(int gridx) {
        return (Unit) getChild(gridx, 2);
    }

    public void addObserverToTree(Observer o) {
        addObserver(o);
        EData[] children = getGarten().getAllKinder();
        for (int i = 0; i < children.length; ++i) {
            children[i].addObserverToTree(o);
        }
    }

    //============================== xml methods
    // ===============================

    /**
     * Returns a list containing all Tag and child-Tag of this dataObject.
     * 
     * @return a <code>List</code> containing all tags.
     */
    public void appendTags(List<String> l, int indent) {

        // if low indentation level, write comment line and increment progressor
        if (indent <= 2) {
            XMLUtil.appendCommentLine(l, indent, getName());
            Progressor.getInstance().incrementValue();
        }
        // append open tag, child tags and the close tag
        l.add(getOpenTag(indent));
        appendChildTags(l, indent);
        l.add(getCloseTag(indent));
    }

    protected void appendChildTags(List<String> l, int indent) {

        // increase indentation level
        indent++;

        // recurse over all children and append tag lists
        EData[] children = getGarten().getAllKinder();
        for (int i = 0; i < children.length; ++i) {
            children[i].appendTags(l, indent);
        }
    }
}
