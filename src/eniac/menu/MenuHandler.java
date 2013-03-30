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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import eniac.Manager;
import eniac.io.IOUtil;
import eniac.io.XMLUtil;
import eniac.menu.action.EAction;
import eniac.util.EProperties;

/**
 * @author zoppke
 */
public class MenuHandler extends DefaultHandler {

	// constant tag and attribute names for parsing the menu xml file
	private static final String MENU = "menu", GROUP = "group",
			ACTION = "action", NAME = "name", MENUBAR = "menubar",
			TOOLBAR = "toolbar", SEPARATOR = "separator", KEY = "key",
			SID = "sid";

	// constant values for parsing state
	private static final int STATE_DEFAULT = 0, STATE_TOOLBAR = 1,
			STATE_MENUBAR = 2;

	///////////////////////////////// fields //////////////////////////////////

	private Hashtable _actionDefaults = null;

	private JMenu _currentMenu = null;

	private JToolBar _toolBar = null;

	private JMenuBar _menuBar = null;

	private int _parsingState = STATE_DEFAULT;

	private Hashtable _actions = new Hashtable();

	////////////////////////////// lifecycle //////////////////////////////////

	public MenuHandler(Hashtable actionDefaults) {
		_actionDefaults = actionDefaults;
	}

	public void init() {

		// parse menu and toolbar from xml
		String path = EProperties.getInstance().getProperty("MENU_FILE");
		InputStream in = Manager.class.getClassLoader().getResourceAsStream(path);
		try {
			IOUtil.parse(in, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//////////////////////////// defaultHandler methods ///////////////////////

	public void startElement(String uri, String localName, String qName,
			Attributes attrs) throws SAXException {
		//System.out.println(qName);
		try {
			if (qName.equals(TOOLBAR)) {
				// create jtoolbar
				_toolBar = new JToolBar();
				// adjust parsing state
				_parsingState = STATE_TOOLBAR;

			} else if (qName.equals(ACTION) && _parsingState == STATE_TOOLBAR) {
				// add action to toolbar
				String key = XMLUtil.parseString(attrs, KEY);
				EAction action = getAction(key);
				_toolBar.add((AbstractButton) action.getValue(EAction.BUTTON));

			} else if (qName.equals(SEPARATOR)
					&& _parsingState == STATE_TOOLBAR) {
				// add separator to toolbar
				_toolBar.addSeparator();

			} else if (qName.equals(MENUBAR)) {
				// create jmenubar
				_menuBar = new JMenuBar();
				// adjust parsing state
				_parsingState = STATE_MENUBAR;

			} else if (qName.equals(GROUP)) {
				// create jmenu and add it to menubar
				String sid = XMLUtil.parseString(attrs, SID);
				_currentMenu = new EMenu(sid);
				_menuBar.add(_currentMenu);

			} else if (qName.equals(ACTION) && _parsingState == STATE_MENUBAR) {
				// add action to jmenu
				String key = XMLUtil.parseString(attrs, KEY);
				EAction action = getAction(key);
				_currentMenu.add((JMenuItem) action.getValue(EAction.ITEM));

			} else if (qName.equals(SEPARATOR)
					&& _parsingState == STATE_MENUBAR) {
				// add separator to menu
				_currentMenu.addSeparator();
			}
		} catch (Exception e) {
			// important: catch any exception and print its tree.
			// otherwise you won't get the error's source.
			e.printStackTrace();
			throw new SAXException(e);
		}
	}

	///////////////////////////// other methods ///////////////////////////////

	public JToolBar getToolBar() {
		return _toolBar;
	}

	public JMenuBar getMenuBar() {
		return _menuBar;
	}

	private EAction getAction(String key) {
		EAction action = (EAction) _actions.get(key);

		// if action is not present in our table, get it from the action manager
		if (action == null) {
			action = ActionManager.getInstance().getAction(key);

			// add action defaults
			Enumeration keys = _actionDefaults.keys();
			while (keys.hasMoreElements()) {
				String s = (String) keys.nextElement();
				action.putValue(s, _actionDefaults.get(s));
			}
			// init action and store it at our table
			action.init();
			_actions.put(key, action);
		}
		// return action
		return action;
	}
}
