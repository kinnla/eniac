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
 * SkinReader.java
 * 
 * Created on 05.02.2004
 */
package eniac.skin;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.data.type.EType;
import eniac.io.Progressor;
import eniac.io.Proxy;
import eniac.io.XMLUtil;
import eniac.lang.Dictionary;
import eniac.log.Log;

/**
 * @author zoppke
 */
public class SkinHandler extends DefaultHandler {

    // keys indicating states of parsing
    private static final short DEFAULT = 0;

    private static final short IN_DESCRIPTOR = 1;

    private static final short CREATING = 2;

    //============================ references
    // ==================================

    // reference to the skin-object that we are serving
    private Skin _skin;

    // hashtables for storing lod-related objects.
    private Hashtable<EType, Descriptor> _overviewDescriptors;
    private Hashtable<EType, Descriptor> _detailDescriptors;

    // reference to factory, where we get our creators from.
    private CreatorFactory _factory;

    //======================= temporary data while parsing
    // =====================

    // name of a single or an array
    private Descriptor.Key _name;

    // the current lod number
    private int _lod = -1;

    // current status of parsing
    private short _state = DEFAULT;

    // current creator
    private Creator _creator;

    // current descriptor
    private Descriptor _descriptor;

    // current shapeVector
    private List<Object> _list;

    // current hashtable for descriptors
    private Hashtable<EType, Descriptor> _descriptorsTable;
    
    //============================== lifecycle
    // =================================

    public SkinHandler(Skin skin) {

        // set reference to skin
        _skin = skin;

        // get imageBase for loading images
        Proxy proxy = _skin.getProxy();
        String imageBase = proxy.get(Proxy.Tag.NAME) + "/"; //$NON-NLS-1$

        // create new factory
        _factory = new CreatorFactory(imageBase);

        // create and init hashtables to store descriptors
        _overviewDescriptors = new Hashtable<>();
        _detailDescriptors = new Hashtable<>();
    }

    //========================== defaultHandler methods
    // ========================

    public void startDocument() {
        // init progressor
        Proxy proxy = _skin.getProxy();
        int max = Integer.parseInt(proxy.get(Proxy.Tag.NUMBER_OF_DESCRIPTORS));
        Progressor.getInstance().setText(Dictionary.SKIN_LOADING.getText());
        Progressor.getInstance().setProgress(0, max);
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attrs) throws SAXException {
    	
    	// try to read TAG
    	Skin.Tag tag = convertToTag(qName);
    	if (tag == null) {
    		return;
    	}
        
        try {
        	// switch on current parsing state
            switch (_state) {

            //============================ default case //==================
            case DEFAULT:

                if (tag == Skin.Tag.LOD) {
                    // lod tag. Parse attributes
                    int min = XMLUtil.parseInt(attrs, Skin.Attribute.MIN_HEIGHT);
                    int max = XMLUtil.parseInt(attrs, Skin.Attribute.MAX_HEIGHT);
                    String name = attrs.getValue(Proxy.Tag.NAME.name().toLowerCase());

                    // add lod to skin
                    _skin.setLod(++_lod, min, max);

                    // set lod name to creatorFactory
                    _factory.setLodName(name);
                    
                    // set current descriptors hashtable
                    _descriptorsTable = _lod == 0 ? _overviewDescriptors : _detailDescriptors;

                } else if (tag == Skin.Tag.DESCRIPTOR) {
                    // create descriptor
                    _descriptor = new Descriptor();

                    // set type
                    EType type = getType(XMLUtil.parseString(attrs, Skin.Attribute.TYPE));

                    // set width, height and fill
                    _descriptor.setWidth(XMLUtil.parseInt(attrs, Skin.Attribute.WIDTH));
                    _descriptor.setHeight(XMLUtil.parseInt(attrs, Skin.Attribute.HEIGHT));
                    _descriptor.setFill(XMLUtil.parseEnum(attrs, Skin.Attribute.FILL,
                    		Descriptor.Fill.class));

                    // add descriptor to hashtable array
                    _descriptorsTable.put(type, _descriptor);

                    // Set flag and tell progressor
                    _state = IN_DESCRIPTOR;
                    Progressor.getInstance().incrementValue();
                    //						try {
                    //							Thread.sleep(100);
                    //
                    //						} catch (Exception e) {
                    //							e.printStackTrace();
                    //						}
                }
                break;

            //====================== in_descriptor case //==============
            case IN_DESCRIPTOR:
                if (tag == Skin.Tag.ARRAY) {

                    // get Creator, init list and adjust flag
                    String cls = XMLUtil.parseString(attrs, Skin.Attribute.CLASS);
                    _creator = _factory.get(cls);
                    _list = new Vector<>();
                    _name = XMLUtil.parseEnum(attrs, Skin.Attribute.NAME, Descriptor.Key.class);
                } else if (tag == Skin.Tag.ENTRY) {

                    // pass call to creator
                    _creator.startElement(qName, attrs);
                    _state = CREATING;
                } else if (tag == Skin.Tag.SINGLE) {

                    // get creator and pass call to him
                    _name = XMLUtil.parseEnum(attrs, Skin.Attribute.NAME, Descriptor.Key.class);
                    String cls = XMLUtil.parseString(attrs, Skin.Attribute.CLASS);
                    _creator = _factory.get(cls);
                    _creator.startElement(qName, attrs);
                    _state = CREATING;
                }
                break;

            //====================== creating case //===================

            case CREATING:
                // pass call to creator
                _creator.startElement(qName, attrs);
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    public void endElement(String uri, String localName, String qName)
            throws SAXException {

    	// try to read TAG
    	Skin.Tag tag = convertToTag(qName);
    	if (tag == null) {
    		return;
    	}

        try {
        	
            // switch on current parsing state
            switch (_state) {

            //============================ default case //==================
            case DEFAULT:
                break;

            //====================== in_descriptor case //==============
            case IN_DESCRIPTOR:

                if (tag == Skin.Tag.DESCRIPTOR) {

                    // end of descriptor. Set descriptor to

                    //reset flag
                    _state = DEFAULT;
                } else if (tag == Skin.Tag.ARRAY) {

                    // convert list to array and put to descriptor
                    Class<?> cls = _list.get(0).getClass();

                    // if bufferedImage, there are some images missing.
                    // create array of Images in this case.
                    if (cls.equals(BufferedImage.class)) {
                        cls = Image.class;
                    }
                    //System.out.println(cls);
                    //System.out.println(_list.get(0));
                    Object[] array = (Object[]) Array.newInstance(cls, _list
                            .size());
                    _descriptor.put(_name, _list.toArray(array));
                } else {

                    // pass call to creator
                    _creator.endElement(qName);
                }
                break;

            //===================== creating case //====================

            case CREATING:
                if (tag == Skin.Tag.ENTRY) {

                    // end of entry. add created object to list
                    _creator.endElement(qName);
                    _list.add(_creator.getObject());
                    _state = IN_DESCRIPTOR;
                } else if (tag == Skin.Tag.SINGLE) {

                    // put created object to descriptor
                    _creator.endElement(qName);
                    _descriptor.put(_name, _creator.getObject());
                    _state = IN_DESCRIPTOR;
                }
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SAXException(e);
        }
    }

    public void warning(SAXParseException e) throws SAXException {
        Log.log(e.toString());
    }

    public void error(SAXParseException e) throws SAXException {
        Log.log(e.toString());
    }

    public void characters(char[] ch, int start, int length) {
        if (_state == CREATING) {
            _creator.characters(new String(ch, start, length));
        }
    }

    //=========================== other methods
    // ================================

    public void setDescriptorsToType(EType type) {
		type.setDescriptors(new Descriptor[]{_overviewDescriptors.get(type), _detailDescriptors.get(type)});
    }

    public boolean hasMissingImages() {
        return _factory.hasMissingImages();
    }
    
    private Skin.Tag convertToTag(String name){
        try {
        	return Enum.valueOf(Skin.Tag.class, name.toUpperCase());
        }catch (IllegalArgumentException exc) {
        	
        	// check, if this is a proxy tag
            try {
            	Enum.valueOf(Proxy.Tag.class, name.toUpperCase());
            }
        	catch(IllegalArgumentException exc2) {
        		
        		// completely unknown tag.
        		System.out.println("Ignoring unknown tag: "+name);
        	}
        	return null;
        }
    }
    
    private EType getType(String name) {
    		return Enum.valueOf(EType.class, name.toUpperCase());
    }
}
