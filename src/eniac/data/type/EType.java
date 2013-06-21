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
 * Created on 22.03.2004
 */
package eniac.data.type;

import java.util.Arrays;

import eniac.data.model.EData;
import eniac.data.view.EPanel;
import eniac.io.ITag;
import eniac.skin.Descriptor;
import eniac.util.EProperties;
import eniac.util.StringConverter;

public class EType {

	public enum Tag{
		NAME, TYPE, MODEL, VIEW, CODE, CODES;
	}
	
    private EType.Tag _name;

    private String _edataClass;

    private String _epanelClass;

    private EType.Tag _codeName;

    private EType.Tag[] _codes;

    private Descriptor[] _descriptors;

    private Grid[] _gridCache;

    public EType(EType.Tag name) {
        _name = name;
        _gridCache = new Grid[StringConverter.toInt(EProperties.getInstance()
                .getProperty("GRID_CACHE_SIZE"))];
    }

    //========================== getters and setters
    // ===========================

    public void setName(EType.Tag name) {
        _name = name;
    }

    public EType.Tag getName() {
        return _name;
    }

    public void setCodes(EType.Tag[] codes) {
        _codes = codes;
    }

    public EType.Tag[] getCodes() {
        return _codes;
    }

    public void setCodeName(EType.Tag codeName) {
        _codeName = codeName;
    }

    public EType.Tag getCodeName() {
        return _codeName;
    }

    public void setEDataClass(String edataClass) {
        _edataClass = edataClass;
    }

    public void setEPanelClass(String epanelClass) {
        _epanelClass = epanelClass;
    }

    public void setDescriptors(Descriptor[] descriptors) {
        // empty grid cache
        Arrays.fill(_gridCache, null);
        _descriptors = descriptors;
    }

    //================================ methods
    // =================================

    public String toString() {
        return _name.toString();
    }

    public EData makeEData() throws InstantiationException,
            ClassNotFoundException, IllegalAccessException {

        EData edata = (EData) Class.forName(_edataClass).newInstance();
        edata.setType(this);
        return edata;
    }

    public EPanel makeEPanel() {
        try {
            return (EPanel) Class.forName(_epanelClass).newInstance();
        } catch (Exception e) {
            //System.out.println(_epanelClass);
            e.printStackTrace();
        }
        return null;
    }

    public int compareTo(EType type) {
        return _name.toString().compareTo(type.toString());
    }

    public Descriptor getDescriptor(int lod) {
        return _descriptors[lod];
    }

    public Grid getGrid(int width, int height, int lod) {
        int index = computeIndex(width, height);
        if (_gridCache[index] == null || _gridCache[index].width != width
                || _gridCache[index].height != height) {

            _gridCache[index] = getDescriptor(lod).makeGrid(width, height);
        }
        return _gridCache[index];
    }

    private int computeIndex(int width, int height) {
        return (width + height) % _gridCache.length;
    }
}
