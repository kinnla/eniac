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

import java.util.Hashtable;

import eniac.data.type.Grid;
import eniac.data.type.ParentGrid;
import eniac.io.Tags;

/**
 * @author zoppke
 */
public class Descriptor extends Hashtable {

    public final static String[] CODES = { Tags.NONE, Tags.BOTH,
            Tags.HORIZONTAL, Tags.VERTICAL };

    public static final short NONE = 0;

    public static final short BOTH = 1;

    public static final short HORIZONTAL = 2;

    public static final short VERTICAL = 3;

    private int _width;

    private int _height;

    private short _fill = NONE;

    /////////////////////////////// lifecycle
    // //////////////////////////////////

    public Descriptor() {
        // empty
    }

    /////////////////////////////// getters and setters
    // ////////////////////////

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

    public void setFill(short fill) {
        _fill = fill;
    }

    public short getFill() {
        return _fill;
    }

    ///////////////////////////////// methods
    // //////////////////////////////////

    public Grid makeGrid(int width, int height) {

        // get gridx. if gridx is null, return a simple grid.
        int[] _gridX = (int[]) get(Tags.GRID_X);
        if (_gridX == null) {
            return new Grid(width, height);
        }

        //otherwise get gridy, too. Create ParentGrid
        int[] _gridY = (int[]) get(Tags.GRID_Y);
        ParentGrid grid = new ParentGrid(width, height);

        // compute zoom
        grid.zoomX = ((float) width) / ((float) _width);
        grid.zoomY = ((float) height) / ((float) _height);

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
