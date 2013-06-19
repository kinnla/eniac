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
 * Created on 12.04.2004
 */
package eniac.lang;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import eniac.Manager;
import eniac.io.IOUtil;
import eniac.io.Progressor;
import eniac.io.Proxy;
import eniac.io.Tag;
import eniac.log.Log;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StatusMap;
import eniac.util.StringConverter;

/**
 * @author zoppke
 */
public class DictionaryIO {

    public static final String USER_LANGUAGE_SYSTEM_PROPERTY = "user.language"; //$NON-NLS-1$

    private DictionaryIO() {
        // empty
    }

    public static Proxy[] loadProxies() {
        String path = getLanguagePathWithoutIndex();
        int max = StringConverter.toInt(EProperties.getInstance().getProperty(
                "MAX_LANGUAGE_INDEX"));
        String text = Dictionary.DICTIONARY_SCANNING.getText();
        return IOUtil.loadProxies(path, max, text);
    }

    public static void loadLanguage(Proxy proxy) {

        // announce that we are loading a language
        Progressor.getInstance().setText(Dictionary.DICTIONARY_LOADING.getText());

        String path = proxy.get(Tag.PATH_TO_THIS_FILE);
        InputStream in = Manager.getInstance().getResourceAsStream(path);
        DictionaryHandler handler = new DictionaryHandler();
        try {
            IOUtil.parse(in, handler);

            // set new language
            StatusMap.set(Status.LANGUAGE, proxy.get(Tag.KEY));
        } catch (IOException e) {
            Log
                    .log(
                            "Loading of language failed. Cannot change to new language", //$NON-NLS-1$
                            JOptionPane.ERROR_MESSAGE, true);
            e.printStackTrace();
        }
    }

    public static void loadDefaultLanguage() {
        if (Manager.getInstance().hasIOAccess()) {
            // if we are privileged, scan for proxies and call a method,
            // that looks for the users language in the system properties.
            Proxy[] proxies = loadProxies();
            // get user's default language.
            String locale = System.getProperty(USER_LANGUAGE_SYSTEM_PROPERTY);

            // recurse on proxies and find one that fits to the default language
            for (int i = 0; i < proxies.length; ++i) {
                String key = proxies[i].get(Tag.KEY);
                if (key.equals(locale)) {
                    // load language and return.
                    loadLanguage(proxies[i]);
                    return;
                }
            }
            // we have no language bundle for the user's default language.
            // so load default language as encoded at parameters.
        }
        // we are not privileged and so have no access to system properties.
        // so load default language as encoded by parameters.
        loadDefaultLanguageFromParameter();
    }

    private static void loadDefaultLanguageFromParameter() {
        String path = getLanguagePathWithoutIndex();
        int index = StringConverter.toInt(EProperties.getInstance()
                .getProperty("INDEX_OF_DEFAULT_LANGUAGE"));
        String languagePath = IOUtil.addIndex(path, index);
        Proxy proxy = IOUtil.loadProxy(languagePath);
        if (proxy == null) {
            // TODO: if proxy is null, log this and return
            Log.log("proxy is null");
            // System.out.println("proxy is null");
            return;
        }
        loadLanguage(proxy);
    }

    private static String getLanguagePathWithoutIndex() {
        String folder = EProperties.getInstance()
                .getProperty("LANGUAGE_FOLDER");
        String file = EProperties.getInstance().getProperty(
                "LANGUAGE_FILE_WITHOUT_INDEX");
        return folder + "/" + file; //$NON-NLS-1$
    }
}
