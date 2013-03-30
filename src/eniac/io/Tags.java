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
 * SkinTags.java
 * 
 * Created on 06.02.2004
 */
package eniac.io;

/**
 * @author zoppke
 */
public class Tags {

    // static initialization of keys
    static {
        XMLUtil.initByLowerCase(Tags.class.getFields());
    }

    // private constructor to avoid that this class is getting instantiated
    private Tags() {
        // empty
    }

    // keys
    public static String

    ///////////////////////////////// general
    // //////////////////////////////////

            PROXY, NAME, X, Y, KEY,

            /////////////////////////////////// skin
            // ///////////////////////////////////

            // proxy
            AUTHOR, EMAIL, NUMBER_OF_LODS, NUMBER_OF_DESCRIPTORS, ZOOM_STEPS,
            PREVIEW, PATH_TO_THIS_FILE,

            // lod
            LOD, MIN_HEIGHT, MAX_HEIGHT,

            // descriptor
            DESCRIPTOR, TYPE, FILL, WIDTH, HEIGHT, CLASS, ARRAY, ENTRY, SINGLE,

            // descriptor keys
            BACK_IMAGE, BACK_IMAGE_ARRAY, FORE_IMAGE, FORE_IMAGE_ARRAY, COLOR,
            RECTANGLE, RECTANGLE_ARRAY, AREAS, ACTIONATOR, CABLE_COLOR,
            CABLE_COLOR_HIGHLIGHT, CABLE_PIXELS, UNPLUGGED, PLUGGED, LOADBOX,
            POINT, GRID_X, GRID_Y, NONE, HORIZONTAL, VERTICAL,

            /////////////////////////////////// edata
            // //////////////////////////////////

            // tags
            ENIAC, PATH, DESCRIPTION,

            // attributes
            ID, VALUE, NUMBER, POWER, PARTNER, LOCATION, IO, GRID, SIZE, INDEX,
            FLAG,

            // attribute values
            IN, OUT, BOTH,

            /////////////////////////////////// menu
            // ///////////////////////////////////

            MENU, GROUP, ACTION, ICON, NUMBER_OF_ACTIONS,

            ////////////////////////////////// status
            // //////////////////////////////////

            CONFIGURATION, SHOW_OVERVIEW, ZOOMED_HEIGHT, SHOW_LOG, SKIN,
            LANGUAGE, HIGHLIGHT_PULSE, SIMULATION_TIME,

            //////////////////////////////// language
            // //////////////////////////////////

            FOLDER,

            ////////////////////////////////// type
            // ////////////////////////////////////

            MODEL, VIEW, CODE, CODES;
}
