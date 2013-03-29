/*
 * Created on 11.04.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;

import eniac.Manager;
import eniac.io.Proxy;
import eniac.lang.Dictionary;
import eniac.lang.DictionaryIO;
import eniac.menu.action.gui.ChangeLanguagePanel;

/**
 * @author zoppke
 */
public class ChangeLanguage extends EAction implements Runnable {

    public void actionPerformed(ActionEvent evt) {
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {

        // announce that work is started
        Manager.getInstance().block();

        // scan for proxies
        Proxy[] proxies = DictionaryIO.loadProxies();

        // create dialog that user can choose a configDescriptor
        ChangeLanguagePanel panel = new ChangeLanguagePanel(proxies);
        panel.init();
        Manager.getInstance().makeDialog(panel, Dictionary.CHANGE_LANGUAGE_NAME);

        // get selected proxy and load language.
        Proxy proxy = panel.getSelectedProxy();
        if (proxy != null) {
            DictionaryIO.loadLanguage(proxy);
        }

        // announce that work is done
        Manager.getInstance().unblock();
    }
}