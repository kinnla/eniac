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
 * Created on 05.03.2004
 */
package eniac.skin;

import java.util.EnumMap;

import eniac.data.type.Grid;
import eniac.data.type.ParentGrid;

/**
 * @author zoppke
 */
public class Descriptor {

	/**
	 * Enumeration of all keys that are available in a descriptor
	 * @author till
	 *
	 * TODO
	 */
	public enum Key {
		
		/**
		 * a background image
		 */
		BACK_IMAGE, 
		
		/**
		 * an array of background images, as used by switches
		 */
		BACK_IMAGE_ARRAY, 
		
		/**
		 * a foreground image
		 */
		FORE_IMAGE, 
		
		/**
		 * an array of foreground images, as used by switchAndFlag
		 */
		FORE_IMAGE_ARRAY, 
		
		/**
		 * The color, given as 6-digit rgb hex string
		 */
		COLOR, 
		
		/**
		 * A rectangle defining the bounds of the epanel
		 */
		RECTANGLE, 
		
		/**
		 * An array of rectangles, as for the light bulbs in blinkenlights
		 */
		RECTANGLE_ARRAY, 
		
		/**
		 * An array of polygons
		 */
		AREAS, 
		
		/**
		 * The controller class, used by switch
		 */
		ACTIONATOR, 
		
		/**
		 * the color of the cable, as defined by the connectors
		 */
		CABLE_COLOR, 
		
		/**
		 * the cable color in pulse highlighting mode
		 */
		CABLE_COLOR_HIGHLIGHT, 
		
		/**
		 * the cable diameter in pixels
		 */
		CABLE_PIXELS, 
		
		/**
		 * image for an unplugged connector
		 */
		UNPLUGGED, 
		
		/**
		 * image for a plugged connector
		 */
		PLUGGED, 
		
		/**
		 * image for a connector with a loadbox
		 */
		LOADBOX, 
		
		/**
		 * the vertical lines of the grid 
		 */
		GRID_X, 
		
		/**
		 * the horizontal lines of the grid
		 */
		GRID_Y, 
		
		/**
		 * the width of a slider (distance between min and max value)
		 */
		X,
	}
	
	public enum Fill{ NONE, BOTH, HORIZONTAL, VERTICAL;}

    private int _width;

    private int _height;

    private Fill _fill = Fill.NONE;
    
    private EnumMap<Key, Object> _map; 

    //============================= lifecycle
    // ==================================

    public Descriptor() {
    	_map = new EnumMap<>(Key.class);
    }

    //============================= getters and setters
    // ========================

    public void setWidth(int width) {
        _width = width;
    }

    public int getWidth() {
        return _width;
    }

    public void setHeight(int height) {
        _height = height;
    }

    public int getHeight() {
        return _height;
    }

    public void setFill(Fill fill) {
        _fill = fill;
    }

    public Fill getFill() {
        return _fill;
    }

    //=============================== methods
    // ==================================

    public Object get(Descriptor.Key key) {
    	return _map.get(key);
    }
    
    public void put(Descriptor.Key key, Object value) {
    	_map.put(key, value);
    }
    
    public Grid makeGrid(int width, int height) {

        // get gridx. if gridx is null, return a simple grid.
        int[] _gridX = (int[]) get(Key.GRID_X);
        if (_gridX == null) {
            return new Grid(width, height);
        }

        //otherwise get gridy, too. Create ParentGrid
        int[] _gridY = (int[]) get(Key.GRID_Y);
        ParentGrid grid = new ParentGrid(width, height);

        // compute zoom
        grid.zoomX = (float) width / (float) _width;
        grid.zoomY = (float) height / (float) _height;

        // create arrays
        grid.xValues = new int[_gridX.length];
        grid.yValues = new int[_gridY.length];

        // copy grid numbers
        for (int i = 0; i < grid.xValues.length; ++i) {
            grid.xValues[i] = _gridX[i] * width / _width;
        }
        for (int i = 0; i < grid.yValues.length; ++i) {
            grid.yValues[i] = _gridY[i] * height / _height;
        }

        // return grid.
        return grid;
    }
}
