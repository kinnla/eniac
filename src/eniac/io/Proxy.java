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
 * Created on 11.02.2004
 */
package eniac.io;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * @author zoppke
 */
public class Proxy extends Hashtable {

    public Proxy() {
        // empty
    }

    public String toString() {
        return (String) get(Tags.NAME);
    }

    public void appendTags(List l, int indent) {

        // append comment line and open tag
        XMLUtil.appendCommentLine(l, indent, Tags.PROXY);
        l.add(XMLUtil.TABS[indent] + XMLUtil.wrapOpenTag(Tags.PROXY));

        // append child tags
        Enumeration en = keys();
        String tabs = XMLUtil.TABS[indent + 1];
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            String open = XMLUtil.wrapOpenTag(key);
            String value = (String) get(key);
            String close = XMLUtil.wrapCloseTag(key);
            l.add(tabs + open + value + close);
        }

        // append close tags
        l.add(XMLUtil.TABS[indent] + XMLUtil.wrapCloseTag(Tags.PROXY));
    }
}
