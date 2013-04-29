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
 * Created on 23.02.2004
 */
package eniac.menu.action.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eniac.io.Proxy;
import eniac.io.Tags;
import eniac.lang.Dictionary;
import eniac.util.EProperties;
import eniac.util.StringConverter;
import eniac.window.EFrame;

/**
 * @author zoppke
 */
public class OpenSkinPanel extends DialogPanel {

    // configurationProxies the user can choose from
    private Proxy[] _proxies;

    private Proxy _selectedProxy = null;

    private JPanel _jpanel;

    JList _jlist;

    private JScrollPane _listPane;

    private ImagePanel _imagePanel;

    // Actions
    private Action _cancelAction;

    private Action _okAction;

    public OpenSkinPanel(Proxy[] proxies) {
        super(new GridBagLayout());
        _proxies = proxies;
    }

    /**
     * Initializes this openConfigurationPanel
     */
    public void init() {

        // ========================== actions
        // ===================================

        // create and add okAction
        _okAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performOkAction();
            }
        };
        _okAction.setEnabled(false);
        _okAction.putValue(Action.NAME, Dictionary.OK);
        getActionMap().put(_okAction.getValue(Action.NAME), _okAction);

        // create and add cancelAction
        _cancelAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performCancelAction();
            }
        };
        _cancelAction.putValue(Action.NAME, Dictionary.CANCEL);
        getActionMap().put(_cancelAction.getValue(Action.NAME), _cancelAction);

        // ============================ jpanel
        // ==================================

        _jpanel = new JPanel(new GridBagLayout());
        _imagePanel = new ImagePanel();

        // create and init _jlist and _listPane
        _jlist = new JList(_proxies);
        _jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _listPane = new JScrollPane(_jlist);

        // add mouseListener to _jlist for receiving double-clicks
        _jlist.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    performOkAction();
                }
            }
        });

        // add itemListener to _jlist for display preview image
        _jlist.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                performUpdate();
            }
        });

        // ========================= layout components
        // ==========================

        // add components
        _jpanel.add(_listPane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));
        _jpanel.add(_imagePanel, new GridBagConstraints(1, 0, 1, 1, 0.5, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));

        // add jpanel
        add(_jpanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        // ============================= buttons
        // ================================

        // create and init buttons
        JButton okButton = new JButton(_okAction);
        JButton cancelButton = new JButton(_cancelAction);

        // layout components
        add(okButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        // ============================= add keystrokes
        // =========================

        // fill actionMap
        getActionMap().put(Dictionary.OK, _okAction);
        getActionMap().put(Dictionary.CANCEL, _cancelAction);

        // fill inputMap
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Dictionary.OK);
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                Dictionary.CANCEL);

        // adjust inputMaps of buttons
        cancelButton.getActionMap().setParent(getActionMap());
        cancelButton.getInputMap(WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                        Dictionary.CANCEL);
    }

    // this is called, when the panel already was added to the dialog.
    // so we can set our selection here.
    // note: if set selection without having a window as ancestor,
    // there will be a NuPoExc
    public void setWindow(Window window) {
        super.setWindow(window);
        if (_jlist.getModel().getSize() > 0) {
            _jlist.setSelectedIndex(0);
        }
    }

    // ========================= event processing
    // ===============================

    void performUpdate() {

        // set image to imagePanel
        _selectedProxy = (Proxy) _jlist.getSelectedValue();
        if (_selectedProxy == null) {
            _imagePanel.setImage(null);
        } else {
            String path = (String) _selectedProxy.get(Tags.PREVIEW);
            Image img = EFrame.getInstance().getResourceAsImage(path);
            _imagePanel.setImage(img);
        }

        // resize window
        Window window = SwingUtilities.windowForComponent(this);
        window.pack();

        // enable or disable okButton
        _okAction.setEnabled(_selectedProxy != null);
    }

    public void performCancelAction() {
        _selectedProxy = null;
        SwingUtilities.windowForComponent(OpenSkinPanel.this).dispose();
    }

    void performOkAction() {
        if (_selectedProxy != null) {
            SwingUtilities.windowForComponent(OpenSkinPanel.this).dispose();
        }
    }

    public Proxy getSelectedProxy() {
        return _selectedProxy;
    }

    // ======================= inner class ImagePanel
    // ===========================

    class ImagePanel extends JPanel {

        private Image _preview;

        public void setImage(Image preview) {
            _preview = preview;
            repaint();
        }

        public Dimension getPreferredSize() {
            return StringConverter.toDimension(EProperties.getInstance().getProperty(
                    "PREVIEW_SIZE"));
            // if (_preview == null) {
            // return new Dimension(0, 0);
            // }
            // int width = _preview.getWidth(this);
            // int height = _preview.getHeight(this);
            // return new Dimension(width, height);
        }

        public void paint(Graphics g) {
            if (_preview == null) {
                g.clearRect(0, 0, getWidth(), getHeight());
            } else {
                g.drawImage(_preview, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

}
