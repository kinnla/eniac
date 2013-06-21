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
 * IOUtil.java
 * 
 * Created on 11.02.2004
 */
package eniac.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;

import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.Manager;
import eniac.lang.Dictionary;
import eniac.util.EProperties;

/**
 * @author zoppke
 */
public class IOUtil {

    // private constructor to avoid someone creates an instance of this class
    private IOUtil() {
        // empty
    }

    public static Proxy loadProxy(String path, ProxyHandler handler) {
        InputStream in = Manager.getInstance().getResourceAsStream(path);
        if (in == null) {
            return null;
        }
        try {
            IOUtil.parse(in, handler);
            Proxy proxy = handler.getProxy();
            proxy.setPath(path);
            return proxy;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Proxy loadProxy(String path) {
        ProxyHandler handler = new ProxyHandler();
        return loadProxy(path, handler);
    }

    public static List<Proxy> 
    		loadProxies(String path, int maxIndex, String text) {
        ProxyScanner scanner = new ProxyScanner(path, maxIndex, text);
        return scanner.getProxies();
    }

    public static String addIndex(String fileName, int index) {
        return addIndices(fileName, index, index)[0];
    }

    public static String[] addIndices(String fileName, int min, int max) {

        // divide fileName
        int dot = fileName.lastIndexOf('.');
        String path1 = fileName.substring(0, dot);
        String path2 = fileName.substring(dot);

        // add name and indices to return array
        int length = max - min + 1;
        String[] retour = new String[length];
        for (int i = 0; i < retour.length; ++i) {
            retour[i] = path1 + (i + min) + path2;
        }

        // return array;
        return retour;
    }

    public static void parse(InputStream in, DefaultHandler handler)
            throws IOException {
        try {
            // parse from the stream with the given handler
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(in, handler);
        } catch (SAXException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }

    // ======================== file access methods
    // =============================

    /**
     * Opens an <code>inputStream</code> to the specified file.
     * 
     * @param path
     *            a <code>string</code> as the name of the file to load
     * @return an <code>inputStream</code> if the file could be found, <br>
     *         or <code>null</code> otherwise
     */
    public static InputStream openInputStream(String path) {
        try {
            return new FileInputStream(path);
        } catch (IOException e) {
            return null;
        }
    }

    public static Writer openWriter(File file) throws IOException {
        return new FileWriter(file, false);
    }

    public static File getSettingsFile() {
        return new File(System.getProperty("user.home") //$NON-NLS-1$
                + File.separator
                + EProperties.getInstance().getProperty("SETTINGS_FILE"));
    }

    public static FileFilter getFileFilter() {
        return new FileFilter() {
            public String getDescription() {
                return Dictionary.FILE_FILTER_DESCRIPTION.getText();
            }

            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".xml") //$NON-NLS-1$
                        || file.getName().endsWith(".eniac"); //$NON-NLS-1$
            }
        };
    }

}
