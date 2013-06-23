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
 * Created on 26.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import eniac.data.model.EData;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class IDManager {

	private static final int INITIAL_SIZE = 50;

	private static final float GROW_RATE = 1.5f;

	private EData[] _data = new EData[INITIAL_SIZE];

	private List<EData> _invalids = new LinkedList<>();

	private int _minFree = 0;

	// ============================== lifecycle
	// =================================

	public IDManager() {
		// empty constructor
	}

	public void dispose() {
		Arrays.fill(_data, null);
		_data = null;
		_invalids.clear();
		_invalids = null;
	}

	// ============================= methods
	// ====================================

	public boolean containsID(int id) {
		return id < _data.length && _data[id] != null;
	}

	public boolean containsID(EData d) {
		return containsID(d.getID());
	}

	public boolean put(EData d) {
		int id = d.getID();
		if (containsID(id)) {
			_invalids.add(d);
			return false;
		}
		checkSize(id);
		_data[id] = d;
		return true;
	}

	public int getFreeID() {
		while (_minFree < _data.length) {
			if (_data[_minFree] == null) {
				return _minFree;
			}
			_minFree++;
		}
		return _minFree;
	}

	public EData get(int id) {
		return _data[id];
	}

	public EData remove(int id) {

		// if id is out of range, return null.
		if (id >= _data.length) {
			return null;
		}

		// otherwise adjust minfree pointer and remove object.
		EData retour = _data[id];
		_data[id] = null;
		_minFree = Math.min(_minFree, id);

		// return object
		return retour;
	}

	public boolean hasInvalids() {
		return _invalids.size() > 0;
	}

	public void integrateInvalids() {
		for (EData d : _invalids) {
			d.setID(getFreeID());
			put(d);
		}
		_invalids.clear();
	}

	public String toString() {
		String s = "IDManager: "; //$NON-NLS-1$
		for (int i = 0; i < _data.length; ++i) {
			s = s + i + "=" + _data[i] + "; "; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return s;
	}

	// ========================== private methods
	// ===============================

	private void checkSize(int id) {
		int size = _data.length;
		while (size <= id) {
			size = (int) (size * GROW_RATE);
		}
		if (size > _data.length) {
			EData[] newData = new EData[size];
			System.arraycopy(_data, 0, newData, 0, _data.length);
			_data = newData;
		}
	}
}
