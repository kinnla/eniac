/*
 * Created on 20.04.2004
 */
package eniac.lang;

import java.lang.reflect.Field;

import eniac.io.XMLUtil;

/**
 * The <code>Dictionary</code> class contains all words that will be presented
 * to the user. The words are given as public static strings and can be
 * identified by their names. Initially, these fields have the values of their
 * lowercase names. On program startup, a dictionary file is loaded by the help
 * of class {@link eniac.lang.DictionaryIO}. The file is choosen according to
 * the user's language settings. Default language is -- once more -- English.
 * This dictionary file is parsed by a {@link eniac.lang.DictionaryHandler}
 * which will fill the matching fields with the words specified in the file.
 * <br>
 * <br>
 * Class <code>Dictionary</code> is a static class that won't be instantiated.
 * The words are either accessed directly by referencing one of the class
 * variables or indirectly by calling {@link #get(String)}with the field's
 * name.
 * 
 * @author zoppke
 */
public class Dictionary {

    /*
     * Static initialization of the fields. This is done, because a dictionary
     * might not be complete in the sense of it doesn't contain words for every
     * field. So in this case, a NuPoExc is prevented.
     */
    static {
        // init keys by their lowercase names for default
        XMLUtil.initByLowerCase(Dictionary.class.getFields());

        // Init special fields with predefined values. These are those words
        // displayed before the language data is loaded.
        INITIALIZING = "Eniac simulation is starting up. Please wait."; //$NON-NLS-1$
        DICTIONARY_LOADING = "loading language file"; //$NON-NLS-1$
        DICTIONARY_SCANNING = "scanning for language files"; //$NON-NLS-1$
        PLEASE_WAIT = "Please wait"; //$NON-NLS-1$
    }

    /*
     * Private constructor to avoid that this class is getting instantiated.
     */
    private Dictionary() {
        // empty
    }

    /**
     * Name of the "Open-Skin" command.
     */
    public static String OPEN_SKIN_NAME;

    /**
     * Short description of the "Open-Skin" command.
     */
    public static String OPEN_SKIN_SHORT;

    /**
     * Name of the "Open Configuration" command.
     */
    public static String OPEN_CONFIGURATION_NAME;

    /**
     * Short description of the "Open configuration" command.
     */
    public static String OPEN_CONFIGURATION_SHORT;

    /**
     * Name of the "Save Configuration" command.
     */
    public static String SAVE_CONFIGURATION_NAME;

    /**
     * Short description of the "Save configuration" command.
     */
    public static String SAVE_CONFIGURATION_SHORT;

    /**
     * Name of the "Zoom in" command.
     */
    public static String ZOOM_IN_NAME;

    /**
     * Short description of the "Zoom in" command.
     */
    public static String ZOOM_IN_SHORT;

    /**
     * Name of the "Zoom out" command.
     */
    public static String ZOOM_OUT_NAME;

    /**
     * Short description of the "Zoom out" command.
     */
    public static String ZOOM_OUT_SHORT;

    /**
     * Title of the "Confirm overwrite" dialog.
     */
    public static String CONFIRM_OVERWRITE_TITLE;

    /**
     * Text at the "Confirm overwrite" dialog.
     */
    public static String CONFIRM_OVERWRITE_TEXT;

    /**
     * Name of the "Change Language" command.
     */
    public static String CHANGE_LANGUAGE_NAME;

    /**
     * Short description of the "Change Language" command.
     */
    public static String CHANGE_LANGUAGE_SHORT;

    /**
     * Name of the "Show log-window" dialog.
     */
    public static String SHOW_LOG_NAME;

    /**
     * Short description of the "Show log-window" command.
     */
    public static String SHOW_LOG_SHORT;

    /**
     * Name of the "Show Overview-window" command.
     */
    public static String SHOW_OVERVIEW_NAME;

    /**
     * Short description of the "Show Overview-window" command.
     */
    public static String SHOW_OVERVIEW_SHORT;

    /**
     * Name of the "Fit zoom to height" command.
     */
    public static String ZOOM_FIT_HEIGHT_NAME;

    /**
     * Short description of the "Fit zoom to height" command.
     */
    public static String ZOOM_FIT_HEIGHT_SHORT;

    /**
     * Name of the "Fit zoom to width" command.
     */
    public static String ZOOM_FIT_WIDTH_NAME;

    /**
     * Short description of the "Fit zoom to width" command.
     */
    public static String ZOOM_FIT_WIDTH_SHORT;

    /**
     * Text at the "Open configuration" dialog.
     */
    public static String CHOOSE_WEB_LOCATION;

    /**
     * Text at the "Open configuration" dialog.
     */
    public static String CHOOSE_FILE;

    /**
     * Text at the "Open configuration" dialog.
     */
    public static String LOAD_FROM_FILE;

    /**
     * The word for "value".
     */
    public static String VALUE;

    /**
     * Title of the Main-frame.
     */
    public static String MAIN_FRAME_TITLE;

    /**
     * Title of the Overview-window.
     */
    public static String OVERVIEW_WINDOW_TITLE;

    /**
     * Title of the "Log window".
     */
    public static String LOG_WINDOW_TITLE;

    /**
     * The word for "properties".
     */
    public static String PROPERTIES;

    /**
     * The word for "ok".
     */
    public static String OK;

    /**
     * The word for "cancel".
     */
    public static String CANCEL;

    /**
     * Message in the blocking-dialog that we are scanning for configurations.
     */
    public static String CONFIGURATION_SCANNING;

    /**
     * Description of the file filter at File-Chooser-Dialogs.
     */
    public static String FILE_FILTER_DESCRIPTION;

    /**
     * Message in the blocking-dialog that we are loading a dictionary.
     */
    public static String DICTIONARY_LOADING;

    /**
     * Message in the blocking-dialog that we are scanning for Dictionaries.
     */
    public static String DICTIONARY_SCANNING;

    /**
     * Message in the blocking-dialog that we are loading the menu.
     */
    public static String MENU_LOADING;

    /**
     * Message in the blocking-dialog that we are scanning for skin files.
     */
    public static String SKIN_SCANNING;

    /**
     * Message in the blocking-dialog that we are loading a skin file.
     */
    public static String SKIN_LOADING;

    /**
     * Polite busy message.
     */
    public static String PLEASE_WAIT;

    /**
     * Title of the "Save Configuration" dialog.
     */
    public static String SAVE_CONFIGURATION_TITLE;

    /**
     * The word for "write".
     */
    public static String WRITE;

    /**
     * The word for "next".
     */
    public static String NEXT;

    /**
     * Enter details before saving a configuration.
     */
    public static String ENTER_DETAILS;

    /**
     * The word for "name".
     */
    public static String NAME;

    /**
     * The word for "description".
     */
    public static String DESCRIPTION;

    /**
     * The title of the (floating) toolbar.
     */
    public static String TOOLBAR_TITLE;

    /**
     * The word for "file".
     */
    public static String FILE;

    /**
     * The word for "window" (not: windows).
     */
    public static String WINDOW;

    /**
     * The word for "view".
     */
    public static String VIEW;

    /**
     * The word for "zoom".
     */
    public static String ZOOM;

    /**
     * The word for "help".
     */
    public static String HELP;

    /**
     * The name of the "Hightlight pule" command.
     */
    public static String HIGHLIGHT_PULSE_NAME;

    /**
     * Short description of the "Highlight pulse" command.
     */
    public static String HIGHLIGHT_PULSE_SHORT;

    /**
     * Name of the "About this program" command.
     */
    public static String ABOUT_NAME;

    /**
     * Short description of the "About this program" command.
     */
    public static String ABOUT_SHORT;

    /**
     * Text of the "About this program" dialog.
     */
    public static String ABOUT_TEXT;

    /**
     * Name of the "faq" command.
     */
    public static String FAQ_NAME;

    /**
     * Short description of the "faq" command.
     */
    public static String FAQ_SHORT;

    /**
     * Text of the "faq" dialog.
     */
    public static String FAQ_TEXT;

    /**
     * Name of the "settings" command.
     */
    public static String SETTINGS_NAME;

    /**
     * Short description of the "settings" command.
     */
    public static String SETTINGS_SHORT;

    /**
     * Text at the "save settings" dialog.
     */
    public static String SAVE_SETTINGS_TEXT;

    /**
     * Title of the "Save settings" dialog.
     */
    public static String SAVE_SETTINGS_TITLE;

    /**
     * Message in the blocking-dialog that we are initializing.
     */
    public static String INITIALIZING;

    /**
     * Message in the blocking-dialog that we are loading a configuration.
     */
    public static String CONFIGURATION_LOADING;

    /**
     * Message in the blocking-dialog that we are writing a configuration.
     */
    public static String CONFIGURATION_WRITING;

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
    public static String get(String sid) {
        try {
            Field field = Dictionary.class.getField(sid.toUpperCase());
            return (String) field.get(null);
        } catch (Exception e) {
            // unknown key. return key as value.
            // TODO: log this
            return sid;
        }
    }
}