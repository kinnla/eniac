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
 * Created on 10.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.skin;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import eniac.io.Proxy;
import eniac.util.EProperties;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class Skin {

	/**
	 * enumeration of all tags in the skin xml.
	 */
	public enum Tag {

		/**
		 * the root element
		 */
		SKIN,

		/**
		 * the level of detail (should be 2 elements in a skin)
		 */
		LOD,

		/**
		 * a descriptor, containing all attributes for an edata
		 */
		DESCRIPTOR,

		/**
		 * a single element in a descriptor
		 */
		SINGLE,

		/**
		 * an array of elements in a descriptor
		 */
		ARRAY,

		/**
		 * an entry in an array of elements
		 */
		ENTRY,

		/**
		 * a point of a polygon entry
		 */
		POINT,
	}

	/**
	 * enumeration of all attributes in the skin xml
	 */
	public enum Attribute {

		/**
		 * the minimum height for an lod in pixel
		 */
		MIN_HEIGHT,

		/**
		 * the maximum height for an lod in pixel
		 */
		MAX_HEIGHT,

		/**
		 * the type of a descriptor (reference to etype)
		 */
		TYPE,

		/**
		 * the width of a descriptor (default width of the epanel)
		 */
		WIDTH,

		/**
		 * the height of a descriptor (default height of the epanel)
		 */
		HEIGHT,

		/**
		 * the fill mode of a descriptor
		 */
		FILL,

		/**
		 * the creator class name of an element in a descriptor
		 */
		CLASS,

		/**
		 * the descriptor key for this element
		 */
		NAME,

		/**
		 * the x coordinate of a polygon point
		 */
		X,

		/**
		 * the y coordinate of a polygon point
		 */
		Y,
	}

	// default image and its static initialization
	public static final Image DEFAULT_IMAGE;
	static {
		DEFAULT_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics g = DEFAULT_IMAGE.getGraphics();
		g.setColor(StringConverter.toColor(EProperties.getInstance().getProperty("DEFAULT_COLOR")));
		g.drawLine(0, 0, 0, 0);
	}

	// ============================== fields
	// ====================================

	// array of lods
	private int[] _minHeight;

	private int[] _maxHeight;

	// array od zoom steps
	int[] _zoomSteps;

	// proxy to the current skin
	private Proxy _proxy;

	// ============================== lifecycle
	// =================================

	// private constructor for instantiating singleton object
	public Skin(Proxy proxy) {
		_proxy = proxy;

		// init lods
		String s = _proxy.get(Proxy.Tag.NUMBER_OF_LODS);
		int numberOfLods = StringConverter.toInt(s);
		_minHeight = new int[numberOfLods];
		_maxHeight = new int[numberOfLods];

		// init zoom steps
		s = _proxy.get(Proxy.Tag.ZOOM_STEPS);
		_zoomSteps = StringConverter.toIntArray(s);
	}

	// ============================ methods
	// =====================================

	public Proxy getProxy() {
		return _proxy;
	}

	public int getLodByHeight(int height) {
		// recurse on all levels of detail and return fitting zoom
		for (int i = 0; i < _minHeight.length; ++i) {
			if (_minHeight[i] <= height && height <= _maxHeight[i]) {
				return i;
			}
		}
		// no appropriate lod found.
		return -1;
	}

	public void setLod(int lod, int minHeight, int maxHeight) {
		_minHeight[lod] = minHeight;
		_maxHeight[lod] = maxHeight;
	}

	public int[] getZoomSteps() {
		return _zoomSteps;
	}
}
