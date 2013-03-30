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
 * Created on 24.02.2004
 */
package eniac.io;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;

import eniac.lang.Dictionary;

/**
 * @author zoppke
 */
public class ProxyScanner extends AbstractAction {

    private String _path;

    private int _maxIndex;

    private String _text;

    private boolean _running = true;

    public ProxyScanner(String path, int maxIndex, String text) {
        super(Dictionary.CANCEL);
        _path = path;
        _maxIndex = maxIndex;
        _text = text;
    }

    /**
     * 
     * @see java.lang.Runnable#run()
     */
    public Proxy[] getProxies() {

        // register cancel action at progressor
        Progressor.getInstance().setText(_text);
        Progressor.getInstance().setProgress(0, _maxIndex);
        Progressor.getInstance().setAction(this);

        // get ready to scan
        Vector v = new Vector();
        ProxyHandler handler = new ProxyHandler();
        String[] proxyFiles = IOUtil.addIndices(_path, 0, _maxIndex);

        // parse previews and collect them in a list
        for (int i = 0; i < proxyFiles.length && _running; ++i) {

            // update progressor to current search index
            Progressor.getInstance().incrementValue();

            // try to load proxy with given proxy
            Proxy proxy = IOUtil.loadProxy(proxyFiles[i], handler);
            if (proxy != null) {
                v.add(proxy);
            }

            // reset handler
            handler.reset();
        }
        // unregister cancel action
        Progressor.getInstance().setAction(this);

        // convert to array and return
        Proxy[] proxies = new Proxy[v.size()];
        v.toArray(proxies);
        return proxies;
    }

    /**
     * @param e
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        _running = false;
    }
}
