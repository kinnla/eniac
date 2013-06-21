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
 * Created on 17.01.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package eniac.io;

import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.Field;
import java.util.List;

import org.xml.sax.Attributes;

import eniac.data.io.DataParsingException;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLUtil {

    // private default constructor.
    // Just to avoid that this class is getting instantiated
    private XMLUtil() {
        // empty
    }

    //============================ constants
    // ===================================

    // tabulators indentation levels by writing to xml
    public static final String[] TABS = {
            "", "\t", "\t\t", "\t\t\t", "\t\t\t\t", "\t\t\t\t\t" }; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    // the header of the eniac xml file
    public static final String ENIAC_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>" //$NON-NLS-1$
            + "\n" //$NON-NLS-1$
            + "<!DOCTYPE eniac>" //$NON-NLS-1$
            + "\n"; //$NON-NLS-1$

    // comment lines to increase readability
    public static final String COMMENT_1 = "<!--###################### "; //$NON-NLS-1$

    public static final String COMMENT_2 = " ######################-->"; //$NON-NLS-1$

    //========================= tag initialization
    // =============================

    // initializes a number of Strings, each string with its lowecase value.
    // Strings are given in an array of Fields representing public static
    // Strings
    public static void initByLowerCase(Field[] fields) {
        // recurse on all fields
        for (int i = 0; i < fields.length; ++i) {
            try {
                // init field by its lowercased name
                String value = fields[i].getName().toLowerCase();
                fields[i].set(null, value);
            } catch (Exception e) {
                // exception should not occure, because only strings over here.
                e.printStackTrace();
            }
        }
    }

    //============================ writing xml
    // =================================

    public static void appendCommentLine(List<String> l, int indent, String name) {
        l.add(""); //$NON-NLS-1$
        l.add(TABS[indent] + COMMENT_1 + name + COMMENT_2);
        l.add(""); //$NON-NLS-1$
    }

    public static String wrapAttribute(Enum<?> tag, String value) {
        return " " + tag + "=\"" + value + "\""; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }

    public static String wrapOpenCloseTag(String tag) {
        return "<" + tag + "/>"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static String wrapOpenTag(String tag) {
        return "<" + tag + ">"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static String wrapCloseTag(String tag) {
        return "</" + tag + ">"; //$NON-NLS-1$//$NON-NLS-2$
    }

    //========================== reading xml
    // ===================================

    public static int parseInt(String s, Enum<?>[] tags) {
        for (int i = 0; i < tags.length; ++i) {
            if (s.equals(tags[i].toString())) {
                return i;
            }
        }
        return -1;
    }

    public static String parseString(Attributes attrs, Enum<?> tag) {
        String s = attrs.getValue(tag.name().toLowerCase());
        if (s == null) {
            throw new DataParsingException(tag,
                    DataParsingException.MISSING_ATTRIBUTE);
        }
        return s;
    }

    public static int parseInt(Attributes attrs, Enum<?> tag) {
        String s = parseString(attrs, tag);
        return StringConverter.toInt(s);
    }

    /**
     * @param attributes
     * @param string
     * @return
     */
    public static Color parseColor(Attributes attrs, Enum<?> tag) {
        String s = parseString(attrs, tag);
        return StringConverter.toColor(s);
    }

    public static int parseInt(Attributes attrs, Enum<?> tag, Enum<?>[] tags) {

        String s = parseString(attrs, tag);
        int i = parseInt(s, tags);
        if (i == -1) {
            throw new DataParsingException(s, tag);
        }
        return i;
    }

    public static long parseLong(Attributes attrs, Enum<?> tag) {
        String s = parseString(attrs, tag);
        return StringConverter.toLong(s);
    }

    public static Dimension parseDimension(Attributes attrs, Enum<?> tag) {
        String s = parseString(attrs, tag);
        return StringConverter.toDimension(s);
    }

    public static boolean parseBoolean(Attributes attrs, Enum<?> tag) {
        String s = parseString(attrs, tag);
        return StringConverter.toBoolean(s);
    }

    public static float parseFloat(Attributes attrs, Enum<?> tag) {
        String s = parseString(attrs, tag);
        return StringConverter.toFloat(s);
    }

    public static int[] parseIntArray(Attributes attrs, Enum<?> tag) {
        String s = parseString(attrs, tag);
        return StringConverter.toIntArray(s);
    }
}
