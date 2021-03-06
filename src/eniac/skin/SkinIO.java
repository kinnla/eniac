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
 * SkinIO.java
 * 
 * Created on 06.02.2004
 */
package eniac.skin;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.swing.JOptionPane;

import eniac.Manager;
import eniac.data.type.EType;
import eniac.io.IOUtil;
import eniac.io.Proxy;
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

	public static List<Proxy> loadProxies() {
		String path = getSkinPathWithoutIndex();
		int max = StringConverter.toInt(EProperties.getInstance().getProperty("MAX_NUMBER_OF_SKINS"));
		String text = Dictionary.SKIN_SCANNING.getText();
		return IOUtil.loadProxies(path, max, text);
	}

	public static void loadSkin(Proxy proxy) {

		String path = proxy.getPath();
		InputStream in = Manager.getInstance().getResourceAsStream(path);
		Skin skin = new Skin(proxy);
		SkinHandler handler = new SkinHandler(skin);
		try {
			IOUtil.parse(in, handler);

			// check, if all images could be loaded.
			// if not, announce this to the user
			if (handler.hasMissingImages()) {
				Log.log(LogWords.MISSING_IMAGES, JOptionPane.INFORMATION_MESSAGE, true);
			}

			// TODO: this should be done at another place
			// iterate on types
			for (EType type : EType.values()) {
				// set descriptors to etypes
				handler.setDescriptorsToType(type);
			}
			// set new skin
			Status.SKIN.setValue(skin);
		} catch (IOException e) {
			Log.log(LogWords.LOADING_OF_SKIN_FAILED, JOptionPane.ERROR_MESSAGE, true);
			e.printStackTrace();
		}
	}

	public static void loadDefaultSkin() {
		String path = getSkinPathWithoutIndex();
		int index = StringConverter.toInt(EProperties.getInstance().getProperty("INDEX_OF_DEFAULT_SKIN"));
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
