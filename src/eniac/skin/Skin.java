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
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Skin {

	public enum Tag{
		SKIN, NAME, BOTH,
		
		// lod
		LOD, MIN_HEIGHT, MAX_HEIGHT,

		// descriptor
		DESCRIPTOR, TYPE, FILL, WIDTH, HEIGHT, CLASS, ARRAY, ENTRY, SINGLE,

		// descriptor keys
		BACK_IMAGE, BACK_IMAGE_ARRAY, FORE_IMAGE, FORE_IMAGE_ARRAY, COLOR, RECTANGLE, RECTANGLE_ARRAY, AREAS, ACTIONATOR, CABLE_COLOR, CABLE_COLOR_HIGHLIGHT, CABLE_PIXELS, UNPLUGGED, PLUGGED, LOADBOX, POINT, GRID_X, GRID_Y, NONE, HORIZONTAL, VERTICAL,

	}
	
    // default image and its static initialization
    public static final Image DEFAULT_IMAGE;
    static {
        DEFAULT_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics g = DEFAULT_IMAGE.getGraphics();
        g.setColor(StringConverter.toColor(EProperties.getInstance().getProperty(
                "DEFAULT_COLOR")));
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
