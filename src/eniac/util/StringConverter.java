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
 * StringConverter.java
 * 
 * Created on 08.02.2004
 */
package eniac.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.StringTokenizer;

/**
 * @author zoppke
 */
public class StringConverter {

    private StringConverter() {
        // empty
    }

    /*
     * ======================= converting string to others =====================
     */

    /**
     * Converts a <code>string</code> into an array of <code>int</code>.
     * The string should contain numbers divided by a komma (,).
     * 
     * @param s
     *            the <code>string</code> to be converted
     * @return an array of <code>int</code>, or <code>null</code>, if the
     *         string was <code>null</code> itself or contained bad data.
     */
    public static int[] toIntArray(String s) {
        s = s.trim();
        StringTokenizer tt = new StringTokenizer(s, ",\n\t ");
        int[] array = new int[tt.countTokens()];
        int i = 0;
        while (tt.hasMoreElements()) {
            array[i++] = Integer.parseInt((String) tt.nextElement());
        }
        return array;
    }

    /**
     * Converts a <code>string</code> into a <code>dimension</code>.
     * 
     * @param s
     *            the <code>string</code> to be converted
     * @return a <code>dimension</code> or <code>null</code> if no
     *         conversion was possible.
     */
    public static Dimension toDimension(String s) {
        int[] array = toIntArray(s);
        if (array.length < 2) {
            throw new RuntimeException("cannot convert to dimension: [" + s
                    + "]");
        }
        return new Dimension(array[0], array[1]);
    }

    /**
     * Converts a <code>string</code> into a <code>point</code>.
     * 
     * @param s
     *            the <code>string</code> to be converted
     * @return a <code>point</code> or <code>null</code> if no conversion
     *         was possible.
     */
    public static Point toPoint(String s) {
        int[] array = toIntArray(s);
        if (array.length < 2) {
            throw new RuntimeException("cannot convert to point: [" + s + "]");
        }
        return new Point(array[0], array[1]);
    }

    /**
     * Converts a <code>string</code> into a <code>rectangle</code>.
     * 
     * @param s
     *            the <code>string</code> to be converted
     * @return a <code>rectangle</code> or <code>null</code> if no
     *         conversion was possible.
     */
    public static Rectangle toRectangle(String s) {
        int[] array = toIntArray(s);
        if (array.length < 4) {
            throw new RuntimeException("cannot convert to rectangle: [" + s
                    + "]");
        }
        return new Rectangle(array[0], array[1], array[2], array[3]);
    }

    /**
     * Converts a <code>string</code> into a <code>boolean</code>. If s is
     * <code>null</code>, then <code>false</code> will be returned.
     * 
     * @param s
     *            the <code>string</code> to be converted
     * @return a <code>boolean</code> as result of conversion.
     */
    public static boolean toBoolean(String s) {
        return Boolean.valueOf(s).booleanValue();
    }

    public static int toInt(String s) {
        return Integer.parseInt(s);
    }

    public static Color toColor(String s) {
        return new Color(Integer.parseInt(s, 16));
    }

    public static float toFloat(String s) {
        return Float.parseFloat(s);
    }

    /**
     * @param string
     * @return
     */
    public static long toLong(String s) {
        return Long.parseLong(s);
    }

    /*
     * ======================= converting others to string =====================
     */

    public static String toString(int[] array) {
        String s = "";
        if (array.length > 0) {
            s += array[0];
            for (int i = 1; i < array.length; ++i) {
                s += ",";
                s += array[i];
            }
        }
        return s;
    }
}
