/*
 * SkinIO.java
 * 
 * Created on 06.02.2004
 */
package eniac.skin;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JOptionPane;

import eniac.Manager;
import eniac.data.type.EType;
import eniac.data.type.ProtoTypes;
import eniac.io.IOUtil;
import eniac.io.Proxy;
import eniac.io.Tags;
import eniac.lang.Dictionary;
import eniac.log.Log;
import eniac.log.LogWords;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StringConverter;

/**
 * @author zoppke
 */
public final class SkinIO {

    private SkinIO() {
        // empty
    }

    public static Proxy[] loadProxies() {
        String path = getSkinPathWithoutIndex();
        int max = StringConverter.toInt(EProperties.getInstance().getProperty(
                "MAX_NUMBER_OF_SKINS"));
        String text = Dictionary.SKIN_SCANNING;
        return IOUtil.loadProxies(path, max, text);
    }

    public static void loadSkin(Proxy proxy) {

        String path = (String) proxy.get(Tags.PATH_TO_THIS_FILE);
        InputStream in = Manager.getInstance().getResourceAsStream(path);
        Skin skin = new Skin(proxy);
        SkinHandler handler = new SkinHandler(skin);
        try {
            IOUtil.parse(in, handler);

            // check, if all images could be loaded.
            // if not, announce this to the user
            if (handler.hasMissingImages()) {
                Log.log(LogWords.MISSING_IMAGES,
                        JOptionPane.INFORMATION_MESSAGE, true);
            }

            // TODO: this should be done at another place
            // iterate on types
            EType[] types = ProtoTypes.getTypes();
            for (int i = 0; i < types.length; ++i) {
                // set descriptors to etypes
                handler.setDescriptorsToType(types[i]);
            }
            // set new skin
            Status.set("skin", skin);
        } catch (IOException e) {
            Log.log(LogWords.LOADING_OF_SKIN_FAILED, JOptionPane.ERROR_MESSAGE,
                    true);
            e.printStackTrace();
        }
    }

    public static void loadDefaultSkin() {
        String path = getSkinPathWithoutIndex();
        int index = StringConverter.toInt(EProperties.getInstance().getProperty(
                "INDEX_OF_DEFAULT_SKIN"));
        String skinPath = IOUtil.addIndex(path, index);
        Proxy proxy = IOUtil.loadProxy(skinPath);
        if (proxy == null) {
            Log.log("skin loading: proxy is null");
            // System.out.println("skin loading: proxy is null");
            return;
        }
        loadSkin(proxy);
    }

    private static String getSkinPathWithoutIndex() {
        String folder = EProperties.getInstance().getProperty("SKIN_FOLDER");
        String file = EProperties.getInstance().getProperty("SKIN_FILE_WITHOUT_INDEX");
        return folder + "/" + file; //$NON-NLS-1$
    }
}