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
 * Created on 20.04.2004
 */
package eniac.lang;

import java.util.EnumMap;

/**
 * The <code>Dictionary</code> class contains all words that will be presented
 * to the user. The words are given as public static strings and can be
 * identified by their names. Initially, these fields have the values of their
 * lowercase names. On program startup, a dictionary file is loaded by the help
 * of class {@link eniac.lang.DictionaryIO}. The file is choosen according to
 * the user's language settings. Default language is -- once more -- English.
 * This dictionary file is parsed by a {@link eniac.lang.DictionaryHandler}
 * which will fill the matching fields with the words specified in the file. <br>
 * <br>
 * Class <code>Dictionary</code> is a static class that won't be instantiated.
 * The words are either accessed directly by referencing one of the class
 * variables or indirectly by calling {@link #get(String)}with the field's name.
 * 
 * @author zoppke
 */
public enum Dictionary {

	/**
	 * Name of the "Open-Skin" command.
	 */
	OPEN_SKIN_NAME,

	/**
	 * Short description of the "Open-Skin" command.
	 */
	OPEN_SKIN_SHORT,

	/**
	 * Name of the "Open Configuration" command.
	 */
	OPEN_CONFIGURATION_NAME,

	/**
	 * Short description of the "Open configuration" command.
	 */
	OPEN_CONFIGURATION_SHORT,

	/**
	 * Name of the "Save Configuration" command.
	 */
	SAVE_CONFIGURATION_NAME,

	/**
	 * Short description of the "Save configuration" command.
	 */
	SAVE_CONFIGURATION_SHORT,

	/**
	 * Name of the "Zoom in" command.
	 */
	ZOOM_IN_NAME,

	/**
	 * Short description of the "Zoom in" command.
	 */
	ZOOM_IN_SHORT,

	/**
	 * Name of the "Zoom out" command.
	 */
	ZOOM_OUT_NAME,

	/**
	 * Short description of the "Zoom out" command.
	 */
	ZOOM_OUT_SHORT,

	/**
	 * Title of the "Confirm overwrite" dialog.
	 */
	CONFIRM_OVERWRITE_TITLE,

	/**
	 * Text at the "Confirm overwrite" dialog.
	 */
	CONFIRM_OVERWRITE_TEXT,

	/**
	 * Name of the "Change Language" command.
	 */
	CHANGE_LANGUAGE_NAME,

	/**
	 * Short description of the "Change Language" command.
	 */
	CHANGE_LANGUAGE_SHORT,

	/**
	 * Name of the "Show log-window" dialog.
	 */
	SHOW_LOG_NAME,

	/**
	 * Short description of the "Show log-window" command.
	 */
	SHOW_LOG_SHORT,

	/**
	 * Name of the "Show Overview-window" command.
	 */
	SHOW_OVERVIEW_NAME,

	/**
	 * Short description of the "Show Overview-window" command.
	 */
	SHOW_OVERVIEW_SHORT,

	/**
	 * Name of the "Fit zoom to height" command.
	 */
	ZOOM_FIT_HEIGHT_NAME,

	/**
	 * Short description of the "Fit zoom to height" command.
	 */
	ZOOM_FIT_HEIGHT_SHORT,

	/**
	 * Name of the "Fit zoom to width" command.
	 */
	ZOOM_FIT_WIDTH_NAME,

	/**
	 * Short description of the "Fit zoom to width" command.
	 */
	ZOOM_FIT_WIDTH_SHORT,

	/**
	 * Text at the "Open configuration" dialog.
	 */
	CHOOSE_WEB_LOCATION,

	/**
	 * Text at the "Open configuration" dialog.
	 */
	CHOOSE_FILE,

	/**
	 * Text at the "Open configuration" dialog.
	 */
	LOAD_FROM_FILE,

	/**
	 * The word for "value".
	 */
	VALUE,

	/**
	 * Title of the Main-frame.
	 */
	MAIN_FRAME_TITLE,

	/**
	 * Title of the Overview-window.
	 */
	OVERVIEW_WINDOW_TITLE,

	/**
	 * Title of the "Log window".
	 */
	LOG_WINDOW_TITLE,

	/**
	 * The word for "properties".
	 */
	PROPERTIES,

	/**
	 * The word for "ok".
	 */
	OK,

	/**
	 * The word for "cancel".
	 */
	CANCEL,

	/**
	 * Message in the blocking-dialog that we are scanning for configurations.
	 */
	CONFIGURATION_SCANNING,

	/**
	 * Description of the file filter at File-Chooser-Dialogs.
	 */
	FILE_FILTER_DESCRIPTION,

	/**
	 * Message in the blocking-dialog that we are loading a dictionary.
	 */
	DICTIONARY_LOADING,

	/**
	 * Message in the blocking-dialog that we are scanning for Dictionaries.
	 */
	DICTIONARY_SCANNING,

	/**
	 * Message in the blocking-dialog that we are loading the menu.
	 */
	MENU_LOADING,

	/**
	 * Message in the blocking-dialog that we are scanning for skin files.
	 */
	SKIN_SCANNING,

	/**
	 * Message in the blocking-dialog that we are loading a skin file.
	 */
	SKIN_LOADING,

	/**
	 * Polite busy message.
	 */
	PLEASE_WAIT,

	/**
	 * Title of the "Save Configuration" dialog.
	 */
	SAVE_CONFIGURATION_TITLE,

	/**
	 * The word for "write".
	 */
	WRITE,

	/**
	 * The word for "next".
	 */
	NEXT,

	/**
	 * Enter details before saving a configuration.
	 */
	ENTER_DETAILS,

	/**
	 * The word for "name".
	 */
	NAME,

	/**
	 * The word for "description".
	 */
	DESCRIPTION,

	/**
	 * The title of the (floating) toolbar.
	 */
	TOOLBAR_TITLE,

	/**
	 * The word for "file".
	 */
	FILE,

	/**
	 * The word for "window" (not: windows).
	 */
	WINDOW,

	/**
	 * The word for "view".
	 */
	VIEW,

	/**
	 * The word for "zoom".
	 */
	ZOOM,

	/**
	 * The word for "help".
	 */
	HELP,

	/**
	 * The name of the "Hightlight pule" command.
	 */
	HIGHLIGHT_PULSE_NAME,

	/**
	 * Short description of the "Highlight pulse" command.
	 */
	HIGHLIGHT_PULSE_SHORT,

	/**
	 * Name of the "About this program" command.
	 */
	ABOUT_NAME,

	/**
	 * Short description of the "About this program" command.
	 */
	ABOUT_SHORT,

	/**
	 * Text of the "About this program" dialog.
	 */
	ABOUT_TEXT,

	/**
	 * Name of the "faq" command.
	 */
	FAQ_NAME,

	/**
	 * Short description of the "faq" command.
	 */
	FAQ_SHORT,

	/**
	 * Text of the "faq" dialog.
	 */
	FAQ_TEXT,

	/**
	 * Name of the "settings" command.
	 */
	SETTINGS_NAME,

	/**
	 * Short description of the "settings" command.
	 */
	SETTINGS_SHORT,

	/**
	 * Text at the "save settings" dialog.
	 */
	SAVE_SETTINGS_TEXT,

	/**
	 * Title of the "Save settings" dialog.
	 */
	SAVE_SETTINGS_TITLE,

	/**
	 * Message in the blocking-dialog that we are initializing.
	 */
	INITIALIZING,

	/**
	 * Message in the blocking-dialog that we are loading a configuration.
	 */
	CONFIGURATION_LOADING,

	/**
	 * Message in the blocking-dialog that we are writing a configuration.
	 */
	CONFIGURATION_WRITING;

	/**
	 * Looks for the words associated with the given key. If the key is invalid,
	 * the key itself is returned.
	 * 
	 * @param sid
	 *            a <code>string</code> to identify the words we are looking
	 *            for.
	 * @return the value of the static field with the uppercased name of 'key'
	 *         or the value of key itself, if there is no field.
	 */

	private EnumMap<Dictionary, String> _map = null;

	public void init() {
		_map = new EnumMap<>(Dictionary.class);

		// Init special fields with predefined values. These are those words
		// displayed before the language data is loaded.
		_map.put(INITIALIZING, "Eniac simulation is starting up. Please wait."); //$NON-NLS-1$
		_map.put(DICTIONARY_LOADING, "loading language file"); //$NON-NLS-1$
		_map.put(DICTIONARY_SCANNING, "scanning for language files"); //$NON-NLS-1$
		_map.put(PLEASE_WAIT, "Please wait"); //$NON-NLS-1$
	}

	public String getText() {

		// check, if already initialized
		if (_map == null) {
			init();
		}
		return _map.get(this);
	}
	
	public void setText(String text) {
		_map.put(this, text);
	}

//
// try {
// Field field = Dictionary.class.getField(sid.toUpperCase());
// return (String) field.get(null);
// } catch (Exception e) {
// // unknown key. return key as value.
// // TODO: log this
// return sid;
// }
// }

}
