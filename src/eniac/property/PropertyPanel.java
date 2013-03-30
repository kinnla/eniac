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
 * Created on 21.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.property;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import eniac.lang.Dictionary;
import eniac.menu.action.gui.DialogPanel;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PropertyPanel extends DialogPanel {

    private List _properties;

    private boolean _commitChanges = false;

    public PropertyPanel(List properties) {
        super(new GridBagLayout());
        _properties = properties;
    }

    public void init() {

        //////////////////////////// actions
        // ///////////////////////////////////

        // create and add okAction
        final Action okAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performOkAction();
            }
        };
        okAction.putValue(Action.NAME, Dictionary.OK);
        getActionMap().put(okAction.getValue(Action.NAME), okAction);

        // create and add cancelAction
        final Action cancelAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performCancelAction();
            }
        };
        cancelAction.putValue(Action.NAME, Dictionary.CANCEL);
        getActionMap().put(cancelAction.getValue(Action.NAME), cancelAction);

        // add keystrokes
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Dictionary.OK);
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                Dictionary.CANCEL);

        /////////////////////////////// init panel
        // /////////////////////////////

        final JPanel panel = new JPanel(new GridBagLayout());
        Iterator it = _properties.iterator();

        // recurse over all properties and create subcomponents for the panel
        int y = 0;
        while (it.hasNext()) {
            Property p = (Property) it.next();

            // add nameLabel and valueComponent
            panel.add(p.getNameLabel(), new GridBagConstraints(0, y, 1, 1, 1.0,
                    0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            JComponent valueComponent = p.getValueComponent();
            panel.add(valueComponent, new GridBagConstraints(1, y, 1, 1, 1.0,
                    0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            y++;
        }

        //////////////////////////// init buttons
        // //////////////////////////////

        JButton okButton = new JButton(okAction);
        JButton cancelButton = new JButton(cancelAction);
        cancelButton.getActionMap().setParent(getActionMap());
        cancelButton.getInputMap(WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                        Dictionary.CANCEL);

        /////////////////////////// add components
        // /////////////////////////////

        add(panel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));

        add(okButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));
        add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 0, 0, 0), 0, 0));
    }

    public boolean isCommitChanges() {
        return _commitChanges;
    }

    ////////////////////////// event processing
    // ////////////////////////////////

    void performOkAction() {
        _commitChanges = true;
        // close dialog window
        SwingUtilities.windowForComponent(this).dispose();
    }

    public void performCancelAction() {
        _commitChanges = false;
        // close dialog window
        SwingUtilities.windowForComponent(PropertyPanel.this).dispose();
    }

}
