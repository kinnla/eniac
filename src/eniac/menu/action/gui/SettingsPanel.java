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
 * Created on 02.05.2004
 */
package eniac.menu.action.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import eniac.lang.Dictionary;
import eniac.util.EProperties;

/**
 * @author zoppke
 */
public class SettingsPanel extends DialogPanel {

    private boolean _okPressed = false;

    private JScrollPane _scrollPane;

    private JTable _table;

    private Action _okAction;

    private Action _cancelAction;

    public SettingsPanel() {
        super(new GridBagLayout());
    }

    public void init() {

        /*
         * ============================ actions ================================
         */

        // create and add okAction
        _okAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performOkAction();
            }
        };
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

        /*
         * ============================= layout ================================
         */

        // create components
        JButton okButton = new JButton(_okAction);
        JButton cancelButton = new JButton(_cancelAction);
        _table = new JTable(new MyTableModel());
        _scrollPane = new JScrollPane(_table);

        // add components
        add(_scrollPane, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(okButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        /*
         * ============================ add keystrokes =========================
         */

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

    /*
     * ============================== methods ==================================
     */

    public void performCancelAction() {

        // set cancel pressed and close window
        _okPressed = false;
        SwingUtilities.windowForComponent(SettingsPanel.this).dispose();
    }

    void performOkAction() {

        // set ok pressed
        _okPressed = true;

        // if currently a cell is editing, stop editing to get the new value.
        if (_table.isEditing()) {
            _table.getCellEditor().stopCellEditing();
        }
        // close window
        SwingUtilities.windowForComponent(SettingsPanel.this).dispose();
    }

    public boolean isOkPressed() {
        return _okPressed;
    }

    public Vector<Vector<String>> getDataVector() {
        return ((MyTableModel) _table.getModel()).getDataVector();
    }

    /*
     * ====================== inner class MyTableModel =========================
     */

    // table model that prevents the first column from being edited
    private class MyTableModel extends DefaultTableModel {

        public MyTableModel() {

            // create data vector
            Vector<Vector<String>> newDataVector = new Vector<>();
            for (String key : EProperties.getInstance().stringPropertyNames()) {
                String value = EProperties.getInstance().getProperty(key);
                Vector<String> row = new Vector<>();
                row.add(key);
                row.add(value);
                newDataVector.add(row);
            }

            // create column identifiers
            Vector<String> newColumnIdentifiers = new Vector<>();
            newColumnIdentifiers.add(Dictionary.NAME.getText());
            newColumnIdentifiers.add(Dictionary.VALUE.getText());

            // set data vector
            setDataVector(newDataVector, newColumnIdentifiers);
        }

        public boolean isCellEditable(int row, int column) {
            return column == 1;
        }
    }

}
