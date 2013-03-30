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
package eniac;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import eniac.util.EProperties;

public class ApplicationStarter implements ResourceProvider {

    public static void main(String[] args) {

        System.out.println((new File(".")).getAbsolutePath());
        // register resource provider
        Manager.getInstance().setResourceProvider(new ApplicationStarter());

        // init properties
        EProperties properties = EProperties.getInstance();

        // recurse on list of arguments
        for (int i = 0; i < args.length; ++i) {

            // parse key and value
            String[] keyVal = args[i].split("=");

            // if argument is not known as property name, announce to the user
            if (!properties.containsKey(keyVal[0])) {
                System.out.println("Warning: Property \"" + keyVal
                        + "\" is unknown. Mistyping?");
            }

            // set property
            properties.setProperty(keyVal[0], keyVal[1]);
        }

        // create manager, init and start.
        Manager.getInstance().init();
        Manager.getInstance().start();
    }

    public InputStream openStream(String file) {
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
