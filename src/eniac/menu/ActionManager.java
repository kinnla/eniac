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
 * Created on 25.03.2004
 */
package eniac.menu;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.Manager;
import eniac.io.IOUtil;
import eniac.io.Progressor;
import eniac.io.XMLUtil;
import eniac.log.Log;
import eniac.log.LogWords;
import eniac.menu.action.EAction;
import eniac.skin.Skin;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StringConverter;
import eniac.window.EFrame;

/**
 * @author zoppke
 */
public class ActionManager extends DefaultHandler {

	// constant tag and attribute names for parsing the menu xml file
	private enum Tag {
		PROPERTY, CLASS, ACTION, ACTIONS, NAME, VALUE, KEY;
	}

	// =============================== fields //================================

	private EAction _currentAction = null;

	private Hashtable<String, Action> _actionsTable = null;

	// ========================== singleton stuff //============================

	private static ActionManager instance = null;

	private ActionManager() {
	}

	public synchronized static ActionManager getInstance() {
		if (instance == null) {
			instance = new ActionManager();
			instance.init();
		}
		return instance;
	}

	private void init() {

		// parse actions from xml
		String path = EProperties.getInstance().getProperty("ACTIONS_FILE");
		InputStream in = Manager.class.getClassLoader().getResourceAsStream(path);
		try {
			IOUtil.parse(in, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ========================== defaultHandler methods //=====================

	public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
		// System.out.println(qName);
		try {
			if (qName.equalsIgnoreCase(Tag.ACTIONS.toString())) {
				// create hashtable to store actions
				_actionsTable = new Hashtable<>();

			}
			else if (qName.equalsIgnoreCase(Tag.ACTION.toString())) {
				// parse action class and name
				String key = XMLUtil.parseString(attrs, Tag.KEY);
				String className = XMLUtil.parseString(attrs, Tag.CLASS);
				// create action and put its key as property
				_currentAction = (EAction) Class.forName(className).newInstance();
				_currentAction.putValue(EAction.KEY, key);
				// add to action hashtable
				_actionsTable.put(key, _currentAction);

			}
			else if (qName.equalsIgnoreCase(Tag.PROPERTY.toString())) {
				// parse property name and value
				String name = XMLUtil.parseString(attrs, Tag.NAME);
				String value = XMLUtil.parseString(attrs, Tag.VALUE);
				// set property at current action
				Object convertedValue = convertProperty(name, value);
				_currentAction.putValue(name, convertedValue);

			}
		} catch (Exception e) {
			// important: catch any exception and print its tree.
			// otherwise you won't get the error's source.
			e.printStackTrace();
			throw new SAXException(e);
		}
	}

	// =========================== other methods //=============================

	public EAction getAction(String key) {
		return (EAction) _actionsTable.get(key);
	}

	private Object convertProperty(String name, String value) {
		if (name.equals(Action.SMALL_ICON)) {

			// tell progressor, that new image loaded
			Progressor.getInstance().incrementValue();

			// load image. If image cannot be loaded, load default image.
			Image img = EFrame.getInstance().getResourceAsImage(value);
			if (img == null) {
				Log.log(LogWords.IMAGE_NOT_FOUND, JOptionPane.ERROR_MESSAGE, value);
				img = Skin.DEFAULT_IMAGE;
			}

			// scale image in case it does not fit to the icon size
			Dimension d = StringConverter.toDimension(EProperties.getInstance().getProperty("ICON_SIZE"));
			ImageObserver o = EFrame.getInstance();
			if (d.width != img.getWidth(o) && d.height != img.getHeight(o)) {
				img = img.getScaledInstance(d.width, d.height, Image.SCALE_DEFAULT);
			}

			// return icon
			return new ImageIcon(img);
		}
		else if (name.equals(EAction.STATUS_PROPERTY)) {
			try {
				return Enum.valueOf(Status.class, value);
			} catch (IllegalArgumentException exc) {
				System.out.println("unknown status property: " + value);
				return null; // TODO: this will cause a nullpointer exception
// later. any better handling?
			}
		}
		// property doesn't need to be converted
		return value;
	}
}
