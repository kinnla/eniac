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
package eniac.util;

import java.util.Properties;

import eniac.Manager;

public class EProperties extends Properties {

    /**
     * Properties file to be loaded from the class path
     */
    public static final String fileName = "eniac.properties";

    /*
     * ========================= singleton stuff ===============================
     */

    // singleton self reference
    private static EProperties instance;

    // singleton private constructor
    private EProperties() {

        // load properties
        try {
            load(Manager.class.getClassLoader().getResourceAsStream(fileName));
        } catch (Exception e) {
            System.out.println("Error: Cannot load properties file.");
        }
    }

    public String getProperty(Status status) {
    	return super.getProperty(status.toString());
    }
    
    // note: this method has to be synchronized, because during loading a skin
    // or scanning for proxies there are separate threads started.
    // So we have to make sure, that the new StatusMap object is created AND
    // it is initialized, befor another thread can enter this method and
    // can find a non-null reference.
    public synchronized static EProperties getInstance() {
        if (instance == null) {
            instance = new EProperties();
        }
        return instance;
    }
}
