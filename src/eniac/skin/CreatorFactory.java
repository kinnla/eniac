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
 * Created on 26.03.2004
 */
package eniac.skin;

import javax.swing.JOptionPane;

import org.xml.sax.Attributes;

import eniac.data.control.ControlerFactory;
import eniac.io.XMLUtil;
import eniac.log.Log;
import eniac.log.LogWords;
import eniac.util.StringConverter;
import eniac.window.EFrame;

/**
 * @author zoppke
 */
public class CreatorFactory {

	// array containing creators
	private Creator[] _creators;

	// class keys of creators
	private String[] _keys;

	// base folder for creating images
	String _imageBase;

	// lod name. Subfolder for creating images
	String _lodName;

	// flag indicating whether there are any images missing.
	boolean _missingImages;

	// actionatorFactory where we get actionators from
	ControlerFactory _actionatorFactory;

	// ================================ lifecycle
	// ===============================

	public CreatorFactory(String imageBase) {

		// set image base
		_imageBase = imageBase;

		// create actionator factory
		_actionatorFactory = new ControlerFactory();

		// collect Creators in an array
		_creators = new Creator[]{new Color(), new Image(), new IntArray(), new Integer(), new Rectangle(),
				new Polygon(), new Actionator(), new Boolean()};

		// init keys
		_keys = new String[_creators.length];
		for (int i = 0; i < _keys.length; ++i) {
			String s = _creators[i].getClass().getName();
			_keys[i] = s.substring(s.lastIndexOf('$') + 1);
		}
	}

	// =============================== methods
	// ==================================

	public Creator get(String cls) {
		for (int i = 0; i < _creators.length; ++i) {
			if (_keys[i].equals(cls)) {
				return _creators[i];
			}
		}
		return null;
	}

	public boolean hasMissingImages() {
		return _missingImages;
	}

	public void setLodName(String lodName) {
		_lodName = lodName;
	}

	// ========================= inner class ColorCreator
	// =======================

	private class Color extends Creator {

		public Color() {
			// empty constructor
		}

		public void endElement(String name) {
			_object = StringConverter.toColor(_cdata);
			_cdata = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see eniac.skin.Creator#startElement(java.lang.String,
		 * org.xml.sax.Attributes)
		 */
		public void startElement(String name, Attributes attrs) {
			// TODO Auto-generated method stub

		}
	}

	private class IntArray extends Creator {

		public IntArray() {
			// empty constructor
		}

		public void endElement(String name) {
			_object = StringConverter.toIntArray(_cdata);
			_cdata = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see eniac.skin.Creator#startElement(java.lang.String,
		 * org.xml.sax.Attributes)
		 */
		public void startElement(String name, Attributes attrs) {
			// TODO Auto-generated method stub

		}
	}

	private class Image extends Creator {

		public Image() {
			// empty constructor
		}

		public void endElement(String name) {
			// load image.
			String path = _imageBase + _lodName + "/" + _cdata; //$NON-NLS-1$
			_object = EFrame.getInstance().getResourceAsImage(path);
			if (_object == null) {

				// If image cannot be loaded, try to load without subfolder
				path = _imageBase + _cdata;
				_object = EFrame.getInstance().getResourceAsImage(path);
				if (_object == null) {

					// cannot find at all. Load default image.
					Log.log(LogWords.IMAGE_NOT_FOUND, JOptionPane.ERROR_MESSAGE, _cdata);
					_object = Skin.DEFAULT_IMAGE;
					_missingImages = true;
				}
			}
			_cdata = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see eniac.skin.Creator#startElement(java.lang.String,
		 * org.xml.sax.Attributes)
		 */
		public void startElement(String name, Attributes attrs) {
			// TODO Auto-generated method stub

		}
	}

	private class Rectangle extends Creator {

		public Rectangle() {
			// empty constructor
		}

		public void endElement(String name) {
			_object = StringConverter.toRectangle(_cdata);
			_cdata = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see eniac.skin.Creator#startElement(java.lang.String,
		 * org.xml.sax.Attributes)
		 */
		public void startElement(String name, Attributes attrs) {
			// TODO Auto-generated method stub

		}
	}

	private class Integer extends Creator {

		public Integer() {
			// empty constructor
		}

		public void endElement(String name) {
			_object = new java.lang.Integer(StringConverter.toInt(_cdata));
			_cdata = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see eniac.skin.Creator#startElement(java.lang.String,
		 * org.xml.sax.Attributes)
		 */
		public void startElement(String name, Attributes attrs) {
			// TODO Auto-generated method stub

		}
	}

	private class Polygon extends Creator {

		public Polygon() {
			// empty constructor
		}

		private java.awt.Polygon _p = null;

		public void startElement(String name, Attributes attrs) {
			if (name.equals(Skin.Tag.POINT.name().toLowerCase())) {
				if (_p == null) {
					_p = new java.awt.Polygon();
				}
				int x = XMLUtil.parseInt(attrs, Skin.Attribute.X);
				int y = XMLUtil.parseInt(attrs, Skin.Attribute.Y);
				_p.addPoint(x, y);
			}
		}

		public void endElement(String name) {
			_object = _p;
			_p = null;
		}
	}

	private class Actionator extends Creator {

		public Actionator() {
			// empty constructor
		}

		public void endElement(String name) {
			_object = _actionatorFactory.get(_cdata);
			_cdata = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see eniac.skin.Creator#startElement(java.lang.String,
		 * org.xml.sax.Attributes)
		 */
		public void startElement(String name, Attributes attrs) {
			// TODO Auto-generated method stub

		}
	}

	private class Boolean extends Creator {

		public Boolean() {
			// empty constructor
		}

		public void endElement(String name) {
			_object = new java.lang.Boolean(_cdata);
			_cdata = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see eniac.skin.Creator#startElement(java.lang.String,
		 * org.xml.sax.Attributes)
		 */
		public void startElement(String name, Attributes attrs) {
			// TODO Auto-generated method stub

		}
	}
}
