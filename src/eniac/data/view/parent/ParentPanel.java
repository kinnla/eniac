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
 * Created on 07.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.view.parent;

import java.awt.Graphics;
import java.awt.Rectangle;

import eniac.data.model.EData;
import eniac.data.model.parent.ParentData;
import eniac.data.type.EType;
import eniac.data.type.Grid;
import eniac.data.type.ParentGrid;
import eniac.data.view.EPanel;
import eniac.util.EProperties;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ParentPanel extends EPanel {

	// reference to the guiGarten containing all child components
	protected EPanel[] _children;

	// cache to store bounds
	private Rectangle[][] _boundsCache;

	private int[] _widthCache;

	private int[] _heightCache;

	// ============================= lifecycle
	// ==================================

	public ParentPanel() {

		// init cache
		int l = StringConverter.toInt(EProperties.getInstance().getProperty("BOUNDS_CACHE_SIZE"));
		_boundsCache = new Rectangle[l][];
		_widthCache = new int[l];
		_heightCache = new int[l];
	}

	public void init() {
		super.init();

		// create garten
		EData[] datas = ((ParentData) _data).getGarten().getAllKinder();
		_children = new EPanel[datas.length];
		for (int i = 0; i < _children.length; ++i) {
			_children[i] = datas[i].makePanel();
			// add panel as child component
			add(_children[i]);
			// init child component
			_children[i].init();
		}
	}

	// TODO: how to dispose? dataObject calls its tree,
	// dataPanel calls its tree?
	public void dispose() {
		super.dispose();
		for (int i = 0; i < _children.length; ++i) {
			_children[i].dispose();
		}
		_children = null;
		removeAll();
	}

	// ============================= methods
	// ====================================

	public EPanel[] getChildren() {
		return _children;
	}

	public void paintAsIcon(Graphics g, int x, int y, int w, int h, int lod) {

		// paint this panel
		paintComponent(g, x, y, w, h, lod);

		// compute rectangles. If no rectangles, return.
		Rectangle[] rectangles = getRectangles(lod, w, h);
		if (rectangles == null) {
			return;
		}

		// get children and call paintAsIcon on them.
		EPanel[] children = getChildren();
		for (int i = 0; i < children.length; i++) {
			Rectangle r = rectangles[i];
			children[i].paintAsIcon(g, r.x + x, r.y + y, r.width, r.height, lod);
		}
	}

	public void doLayout() {

		// compute bounds for all children
		int lod = getLod();
		Rectangle[] rectangles = getRectangles(lod, getWidth(), getHeight());

		// if no rectangles, return.
		if (rectangles == null) {
			return;
		}

		// set bounds for all of the children
		EPanel[] children = getChildren();
		for (int i = 0; i < children.length; ++i) {
			Rectangle r = rectangles[i];
			children[i].setBounds(r.x, r.y, r.width, r.height);
		}
	}

	protected Rectangle[] getRectangles(int lod, int width, int height) {

		// get index
		int index = computeIndex(width, height);
		if (_boundsCache[index] == null || _widthCache[index] != width || _heightCache[index] != height) {

			// get grid.
			EType type = _data.getType();
			Grid grid = type.getGrid(width, height, lod);
			// check, whether grid has numbers to layout children
			if (grid instanceof ParentGrid) {
				ParentGrid pg = (ParentGrid) grid;
				EPanel[] children = getChildren();
				_boundsCache[index] = new Rectangle[children.length];
				// recurse on children and compute bounds
				for (int i = 0; i < children.length; ++i) {
					_boundsCache[index][i] = children[i].computeBound(pg, lod);
				}
			}
			else {
				_boundsCache[index] = null;
			}
			// set width and height
			_widthCache[index] = width;
			_heightCache[index] = height;
		}
		// return rectangles
		return _boundsCache[index];
	}

	private int computeIndex(int width, int height) {
		return (width + height) % _boundsCache.length;
	}
}
