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

import java.io.InputStream;
import java.lang.reflect.Field;

import eniac.Manager;
import eniac.io.IOUtil;
import eniac.log.Log;
import eniac.util.EProperties;

/**
 * @author zoppke
 */
public class ProtoTypes {

    static {
    	String file = EProperties.getInstance().getProperty("PROTOTYPES_FILE");
        InputStream in = Manager.class.getClassLoader().getResourceAsStream(file);
        TypeHandler handler = new TypeHandler();
        try {
            IOUtil.parse(in, handler);
        } catch (Exception e) {
            System.out.println("Error in initializing types"); //$NON-NLS-1$
        }
    }

    //=============================== methods
    // ==================================

    public static void setType(EType type) {
        try {
            String name = type.toString().toUpperCase();
            Field f = ProtoTypes.class.getField(name);
            f.set(null, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EType[] getTypes() {
        Field[] fields = ProtoTypes.class.getFields();
        EType[] types = new EType[fields.length];
        for (int i = 0; i < types.length; ++i) {
            try {
                types[i] = (EType) fields[i].get(null);
                //System.out.println(fields[i].getName() + "------" +
                // types[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return types;
    }

    public static EType getType(String name) {
        try {
            Field f = ProtoTypes.class.getField(name.toUpperCase());
            return (EType) f.get(null);
        } catch (Exception e) {
            return null;
        }
    }
}
