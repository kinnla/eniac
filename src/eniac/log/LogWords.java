/*
 * Created on 20.02.2004
 */
package eniac.log;

/**
 * @author zoppke
 */
public class LogWords {

    public static final String

    // messages
            IMAGE_NOT_FOUND = "Cannot find image", //$NON-NLS-1$
            MISSING_IMAGES = "Some images are missing." //$NON-NLS-1$
                    + " Maybe this skin cannot be displayed correctly.", //$NON-NLS-1$
            LOADING_OF_SKIN_FAILED = "Loading of Skin Failed.", //$NON-NLS-1$
            CANNOT_SAVE_FILE = "Cannot save file. No privileges to access local file system. " //$NON-NLS-1$
                    + "If you want to write file, " //$NON-NLS-1$
                    + "please restart and accept certificate.", //$NON-NLS-1$
            NO_PRIVILEGES_GRANTED = "No privileges granted. No file access.", //$NON-NLS-1$
            COULD_NOT_LOAD_CONFIGURATION = "Could not load new configuration.", //$NON-NLS-1$
            INVALID_IDS_CONTAINED = "Configuration contained invalid IDs. " //$NON-NLS-1$
                    + "Maybe it is not working properly."; //$NON-NLS-1$

    private LogWords() {
        // empty
    }
}