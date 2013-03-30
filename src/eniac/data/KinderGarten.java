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
 * Created on 31.01.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package eniac.data;

import java.util.Arrays;
import java.util.List;

import eniac.data.model.EData;
import eniac.data.type.EType;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class KinderGarten {

    // all children in an array
    private EData[] _kinder;

    // data types
    private EType[] _types;

    // indices of the first element in each section
    private int[] _sections;

    ////////////////////////////// lifecycle
    // ///////////////////////////////////

    public KinderGarten(List listOfDataObjects) {
        _kinder = new EData[listOfDataObjects.size()];
        listOfDataObjects.toArray(_kinder);
        init();
    }

    private void init() {

        // special case: no children
        if (_kinder.length == 0) {
            _types = new EType[0];
            _sections = new int[] { 0 };
            return;
        }

        // sort kinder by type and index
        Arrays.sort(_kinder);

        // variables
        int[] sections = new int[_kinder.length + 1];
        sections[0] = 0;
        EType[] types = new EType[_kinder.length];
        types[0] = _kinder[0].getType();
        int counter = 0;

        // determine types and sections
        for (int i = 1; i < _kinder.length; ++i) {
            EType type = _kinder[i].getType();
            if (type != types[counter]) {
                types[++counter] = type;
                sections[counter] = i;
            }
        }
        sections[++counter] = _kinder.length;

        // copy to arrays
        _types = new EType[counter];
        _sections = new int[counter + 1];
        System.arraycopy(types, 0, _types, 0, _types.length);
        System.arraycopy(sections, 0, _sections, 0, _sections.length);
    }

    ///////////////////////////////// methods
    // //////////////////////////////////

    public EData getKind(EType type, int index) {
        assert index >= 0;
        int section = _sections[getTypeIndex(type)];
        EData retour = _kinder[section + index];
        assert retour.getType() == type;
        return retour;
    }

    public EData[] getKinder(EType type) {
        int ti = getTypeIndex(type);
        if (ti < 0) {
            return new EData[0];
        }
        int start = _sections[ti];
        int number = _sections[ti + 1] - start;
        EData[] retour = new EData[number];
        System.arraycopy(_kinder, start, retour, 0, number);
        return retour;
    }

    public EData[] getAllKinder() {
        return _kinder;
    }

    private int getTypeIndex(EType type) {
        for (int i = 0; i < _types.length; ++i) {
            if (type == _types[i]) {
                return i;
            }
        }
        return -1;
    }

    public int getNumber(EType type) {
        int ti = getTypeIndex(type);
        if (ti < 0) {
            return 0;
        }
        return _sections[ti + 1] - _sections[ti];
    }

    public void disposeAll() {
        for (int i = 0; i < _kinder.length; ++i) {
            _kinder[i].dispose();
        }
    }
}
