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
 * Created on 11.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import eniac.Manager;
import eniac.data.model.EData;
import eniac.data.model.parent.Configuration;
import eniac.io.IOUtil;
import eniac.io.Progressor;
import eniac.io.Proxy;
import eniac.io.XMLUtil;
import eniac.lang.Dictionary;
import eniac.log.Log;
import eniac.log.LogWords;
import eniac.menu.action.OpenConfiguration;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StatusMap;
import eniac.util.StringConverter;
import eniac.window.EFrame;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ConfigIO {

	
    // private constructor to prevent from initializing this class
    private ConfigIO() {
        // empty constructor
    }

    // =========================== public methods
    // ===============================

    /**
     * Writes an xml representation of this configuration to a file. If the file
     * already exists, the user will be asked if he wants to overwrite.
     */
    public static void write(File file, Configuration config, Proxy proxy)
            throws IOException {

        // check, if file already exists
        if (file.exists()) {
            // ask user to confirm overwrite
            int option = JOptionPane.showConfirmDialog(EFrame.getInstance(),
                    file.getName() + Dictionary.CONFIRM_OVERWRITE_TEXT.getText(),
                    Dictionary.CONFIRM_OVERWRITE_TITLE.getText(),
                    JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.CANCEL_OPTION) {
                // if writing canceled, return
                return;
            }
        }

        // announce that we are writing the configuration
        Progressor.getInstance().setText(Dictionary.CONFIGURATION_WRITING.getText());
        int max = config.getGarten().getAllKinder().length;
        Progressor.getInstance().setProgress(0, max);

        // collect all tags in a list
        List<String> list = new LinkedList<>();
        list.add(XMLUtil.ENIAC_HEADER);
        list.add(XMLUtil.wrapOpenTag(EData.Tag.ENIAC.name().toLowerCase()));
        proxy.appendTags(list, 1);
        config.appendTags(list, 1);
        list.add(XMLUtil.wrapCloseTag(EData.Tag.ENIAC.name().toLowerCase()));

        // convert list to stringbuffer
        StringBuffer buf = new StringBuffer();
        for (String s : list) {
            buf.append(s);
            buf.append('\n');
        }

        // write stringbuffer to file.
        Writer writer = IOUtil.openWriter(file);
        writer.write(buf.toString());
        writer.flush();
        writer.close();
    }

    /**
     * Loads a configuration from the local file system
     * 
     * @param path
     *            a <code>string</code> containing the configuration's path
     */
    public static void loadConfiguration(String path) {
        InputStream in = IOUtil.openInputStream(path);
        loadConfiguration(in);
    }

    /**
     * Loads a basic configuration from the classLoader as described by a
     * <code>configurationProxy</code>
     * 
     * @param proxy
     *            the <code>configurationProxy</code> identifying the
     *            configuration to load.
     */
    public static void loadConfiguration(Proxy proxy) {
        String path = proxy.getPath();
        InputStream in = Manager.getInstance().getResourceAsStream(path);
        loadConfiguration(in);
    }

    public static List<Proxy> loadProxies() {
        String path = getPathWithoutIndex();
        int max = StringConverter.toInt(EProperties.getInstance().getProperty(
                "MAX_NUMBER_OF_CONFIGS"));
        String text = Dictionary.CONFIGURATION_SCANNING.getText();
        return IOUtil.loadProxies(path, max, text);
    }

    public static void loadDefaultConfiguration() {
        if (EProperties.getInstance().getProperty("INDEX_OF_DEFAULT_CONFIG")
                .startsWith("-")) {

            // if no default configuration is given, let user choose one
            OpenConfiguration oc = new OpenConfiguration();
            oc.run();
        } else {

            // otherwise load configuration as given by default index
            int index = StringConverter.toInt(EProperties.getInstance().getProperty(
                    "INDEX_OF_DEFAULT_CONFIG"));
            String path = getPathWithoutIndex();
            String fullPath = IOUtil.addIndex(path, index);
            Proxy proxy = IOUtil.loadProxy(fullPath);
            loadConfiguration(proxy);
        }
    }

    // ========================== private methods
    // ===============================

    private static String getPathWithoutIndex() {
        String folder = EProperties.getInstance().getProperty("CONFIG_FOLDER");
        String file = EProperties.getInstance().getProperty(
                "CONFIG_FILE_WITHOUT_INDEX");
        return folder + "/" + file; //$NON-NLS-1$
    }

    // this is called, when loading the configuration failed
    private static void loadingFailed(String reason) {
        Log.log(LogWords.COULD_NOT_LOAD_CONFIGURATION,
                JOptionPane.ERROR_MESSAGE, reason, true);
    }

    private static void loadingSucceded(Configuration newConfig) {

        // dispose old configuration object, if there is any
        // EFrame.getInstance().disposeConfigPanel();
        Object oldConfig = StatusMap.get(Status.CONFIGURATION);
        StatusMap.set(Status.CONFIGURATION, null);
        if (oldConfig != null) {
            ((Configuration) oldConfig).dispose();
        }

        // init new configuration object
        newConfig.init();

        // there were invalid ids. Display message to user.
        if (newConfig.getIDManager().hasInvalids()) {
            newConfig.getIDManager().integrateInvalids();
            Log.log(LogWords.INVALID_IDS_CONTAINED,
                    JOptionPane.INFORMATION_MESSAGE, true);
        }

        // set new configuration Object as current Configuration
        StatusMap.set(Status.CONFIGURATION, newConfig);
    }

    // loads a configuration from the given inputStream.
    private static void loadConfiguration(InputStream in) {

        // check for null
        if (in == null) {
            loadingFailed("Inputstream is null"); //$NON-NLS-1$
            return;
        }

        // create handler
        ConfigHandler handler = new ConfigHandler();

        // parse data tree from xml
        try {
            IOUtil.parse(in, handler);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check, if parsing was successful
        Configuration config = handler.getConfiguration();
        if (config != null) {
            loadingSucceded(config);
        } else {
            loadingFailed("new configuration is null"); //$NON-NLS-1$
        }
    }
}
