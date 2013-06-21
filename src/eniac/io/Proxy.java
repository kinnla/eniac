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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @author zoppke
 */
public class Proxy extends EnumMap<Proxy.Tag, String>  {

	/**
	 * Enumeration of all tags that are understood by the proxy handler
	 * @author till
	 *
	 * TODO
	 */
	public enum Tag{
		
		/**
		 * the proxy tag. data outside this section will be ignored. Any tag 
		 * inside this section shall be registered as enum constant in Proxy.Tag
		 */
		PROXY, 
		
		/**
		 * the author of a skin
		 */
		AUTHOR, 
		
		/**
		 * the email address of the author of a skin
		 */
		EMAIL, 
		
		/**
		 * number of LODs in a skin (should be 2) TODO: refactor, we don't need it
		 */
		NUMBER_OF_LODS, 
		
		/**
		 * the number of descriptors in a skin TODO: do we need this any more?
		 */
		NUMBER_OF_DESCRIPTORS, 
		
		/**
		 * zoom steps for the user to zoom in & out
		 */
		ZOOM_STEPS, 
		
		/**
		 * path to a preview image of the skin
		 */
		PREVIEW, 
		
		/**
		 * name of a configuration or language
		 */
		NAME,
		
		/**
		 * description of the configuration or language
		 */
		DESCRIPTION,
		
		/**
		 * string-key of the language, as the 2-letter locale
		 */
		KEY,
	}
	
	private String _path;
	
    public Proxy() {
		super(Proxy.Tag.class);
	}

//	public String toString() {
//        return get(Tag.NAME);
//    }
    
    public void setPath(String path) {
    	_path=path;
    }

    public String getPath() {
    	return _path;
    }
    
    public void appendTags(List<String> l, int indent) {

        // append comment line and open tag
        XMLUtil.appendCommentLine(l, indent, Tag.PROXY.toString());
        l.add(XMLUtil.TABS[indent] + XMLUtil.wrapOpenTag(Tag.PROXY.toString()));

        // append child tags
        String tabs = XMLUtil.TABS[indent + 1];
        for (Map.Entry<Proxy.Tag, String> entry : entrySet()) {
            String open = XMLUtil.wrapOpenTag(entry.getKey().toString().toLowerCase());
            String close = XMLUtil.wrapCloseTag(entry.getKey().toString().toLowerCase());
            l.add(tabs + open + entry.getValue() + close);
        }

        // append close tags
        l.add(XMLUtil.TABS[indent] + XMLUtil.wrapCloseTag(Tag.PROXY.toString()));
    }
}
