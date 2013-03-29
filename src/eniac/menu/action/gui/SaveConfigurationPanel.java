/*
 * Created on 23.02.2004
 */
package eniac.menu.action.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import eniac.lang.Dictionary;

/**
 * @author zoppke
 */
public class SaveConfigurationPanel extends DialogPanel {

    // actions
    private Action _cancelAction;

    private Action _nextAction;

    // jpanel
    private JPanel _panel;

    // labels and textfields for name and description
    private JLabel _nameLabel;

    private JLabel _descriptionLabel;

    private JTextField _nameField;

    private JTextArea _descriptionArea;

    private JScrollPane _scrollPane;

    // exit status
    private boolean _nextPressed = false;

    public SaveConfigurationPanel() {
        super(new GridBagLayout());
    }

    /**
     * Initializes this openConfigurationPanel
     */
    public void init() {

        //////////////////////////// actions
        // ///////////////////////////////////

        // create and add okAction
        _nextAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performOkAction();
            }
        };
        _nextAction.putValue(Action.NAME, Dictionary.NEXT);
        getActionMap().put(_nextAction.getValue(Action.NAME), _nextAction);

        // create and add cancelAction
        _cancelAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performCancelAction();
            }
        };
        _cancelAction.putValue(Action.NAME, Dictionary.CANCEL);
        getActionMap().put(_cancelAction.getValue(Action.NAME), _cancelAction);

        /////////////////////////////// jpanel
        // /////////////////////////////////

        // create components
        _panel = new JPanel(new GridBagLayout());
        _panel.setBorder(BorderFactory
                .createTitledBorder(Dictionary.ENTER_DETAILS));
        _nameLabel = new JLabel(Dictionary.NAME);
        _descriptionLabel = new JLabel(Dictionary.DESCRIPTION);
        _nameField = new JTextField(20);
        _descriptionArea = new JTextArea(3, 20);
        _scrollPane = new JScrollPane(_descriptionArea);

        // add components to panel
        _panel.add(_nameLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
                        0, 0, 0), 0, 0));
        _panel.add(_nameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        _panel.add(_descriptionLabel, new GridBagConstraints(0, 1, 1, 1, 1.0,
                0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        _panel.add(_scrollPane, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        // add panel to saveConfigPanel
        add(_panel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        ///////////////////////////// buttons
        // //////////////////////////////////

        // create and init buttons
        JButton okButton = new JButton(_nextAction);
        JButton cancelButton = new JButton(_cancelAction);

        // layout components
        add(okButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        /////////////////////////////// add keystrokes
        // /////////////////////////

        // fill actionMap
        getActionMap().put(Dictionary.NEXT, _nextAction);
        getActionMap().put(Dictionary.CANCEL, _cancelAction);

        // fill inputMap
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), Dictionary.NEXT);
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                Dictionary.CANCEL);

        // adjust inputMaps of buttons
        cancelButton.getActionMap().setParent(getActionMap());
        cancelButton.getInputMap(WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                        Dictionary.CANCEL);
    }

    /////////////////////////////////// getter
    // /////////////////////////////////

    public boolean isNextPressed() {
        return _nextPressed;
    }

    public String getName() {
        return _nameField.getText();
    }

    public String getDescription() {
        return _descriptionArea.getText();
    }

    //////////////////////////// action methods
    // ////////////////////////////////

    public void performCancelAction() {
        _nextPressed = false;
        SwingUtilities.windowForComponent(SaveConfigurationPanel.this)
                .dispose();
    }

    void performOkAction() {
        _nextPressed = true;
        SwingUtilities.windowForComponent(SaveConfigurationPanel.this)
                .dispose();
    }

}