package eniac.util;

import java.io.IOException;
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

    // note: this method has to be synchronized, because during loading a skin
    // or scanning for proxies there are separate threads started.
    // So we have to make sure, that the new Status object is created AND
    // it is initialized, befor another thread can enter this method and
    // can find a non-null reference.
    public synchronized static EProperties getInstance() {
        if (instance == null) {
            instance = new EProperties();
        }
        return instance;
    }
}
