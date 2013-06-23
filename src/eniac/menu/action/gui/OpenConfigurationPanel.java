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
 * Created on 10.09.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.menu.action.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eniac.Manager;
import eniac.io.IOUtil;
import eniac.io.Proxy;
import eniac.lang.Dictionary;
import eniac.util.EProperties;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OpenConfigurationPanel extends DialogPanel {

	/**
	 * no configuration selected by the user
	 */
	public static final short NO_CONFIGURATION = 0;

	/**
	 * basic configuration type, so available as resource from the classloader
	 */
	public static final short BASIC = 1;

	/**
	 * local configuration type, so available from local file system
	 */
	public static final short LOCAL = 2;

	// configurationProxies the user can choose from
	private List<Proxy> _proxies;

	// data for the users input-result.
	private short _configurationType = NO_CONFIGURATION;

	private Proxy _selectedProxy = null;

	private String _canonicalPath = null;

	// components
	private ButtonGroup _buttonGroup;

	private JRadioButton _radioButton1;

	private JRadioButton _radioButton2;

	private JPanel _jpanel1;

	private JPanel _jpanel2;

	// jpanel1 subcomponents
	private JList<Proxy> _jlist;

	private JScrollPane _listPane;

	private JTextArea _textArea;

	private JScrollPane _textPane;

	// jpanel2 subcomponents
	private JTextField _jtextField;

	// Actions
	private Action _cancelAction;

	private Action _okAction;

	private Action _fileChooserAction;

	public OpenConfigurationPanel(List<Proxy> proxies) {
		super(new GridBagLayout());
		_proxies = proxies;
	}

	/**
	 * Initializes this openConfigurationPanel
	 */
	public void init() {

		// ========================= actions //=================================

		// create and add okAction
		_okAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				performOkAction();
			}
		};
		_okAction.putValue(Action.NAME, Dictionary.OK.getText());
		getActionMap().put(_okAction.getValue(Action.NAME), _okAction);

		// create and add cancelAction
		_cancelAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				performCancelAction();
			}
		};
		_cancelAction.putValue(Action.NAME, Dictionary.CANCEL.getText());
		getActionMap().put(_cancelAction.getValue(Action.NAME), _cancelAction);

		// create and add filechooserAction
		_fileChooserAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				performFilechooserAction();
			}
		};
		_fileChooserAction.putValue(Action.NAME, "..."); //$NON-NLS-1$
		_fileChooserAction.putValue(Action.SHORT_DESCRIPTION, Dictionary.CHOOSE_FILE.getText());
		getActionMap().put(_fileChooserAction.getValue(Action.NAME), _fileChooserAction);

		// =========================== jpanel1 //===============================

		// create and init _jlist and _listPane
		_jlist = new JList<>(new Vector<>(_proxies));
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

		// add itemListener to _jlist for to display description-text
		_jlist.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				performUpdate();
			}
		});

		// create and init _textArea and _textPane
		_textArea = new JTextArea(StringConverter.toInt(EProperties.getInstance().getProperty("TEXT_AREA_ROWS")),
				StringConverter.toInt(EProperties.getInstance().getProperty("TEXT_AREA_COLUMNS")));
		_textArea.setEditable(false);
		_textPane = new JScrollPane(_textArea);

		// create and init radioButtons and buttonGroup
		_radioButton1 = new JRadioButton();
		_radioButton2 = new JRadioButton();
		_buttonGroup = new ButtonGroup();
		_buttonGroup.add(_radioButton1);
		_buttonGroup.add(_radioButton2);
		_radioButton1.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				performUpdate();
			}
		});
		_radioButton2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				performUpdate();
			}
		});

		// init jpanel1
		_jpanel1 = new JPanel(new GridBagLayout());
		_jpanel1.setBorder(BorderFactory.createTitledBorder(Dictionary.CHOOSE_WEB_LOCATION.getText()));

		// add components
		_jpanel1.add(_listPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.5, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		_jpanel1.add(_textPane, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.5, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		// add jradiobutton1 and jpanel1
		add(_radioButton1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(_jpanel1, new GridBagConstraints(1, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		// ============================= jpanel2 //=============================

		// init jpanel1
		_jpanel2 = new JPanel(new GridBagLayout());
		_jpanel2.setBorder(BorderFactory.createTitledBorder(Dictionary.LOAD_FROM_FILE.getText()));

		// create and init jtextfield
		_jtextField = new JTextField(20);

		// create and init fileChooserButton
		JButton fileChooserButton = new JButton(_fileChooserAction);

		// add components
		_jpanel2.add(_jtextField, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		_jpanel2.add(fileChooserButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		// add jradiobutton2 and jpanel1
		add(_radioButton2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		add(_jpanel2, new GridBagConstraints(1, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		// ============================ buttons //==============================

		// create and init buttons
		JButton okButton = new JButton(_okAction);
		JButton cancelButton = new JButton(_cancelAction);

		// layout components
		add(okButton, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(cancelButton, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		// init selection state
		_radioButton1.setSelected(true);

		// enable radiobutton2 according to our privileges
		_radioButton2.setEnabled(Manager.getInstance().hasIOAccess());

		// ============================ add keystrokes //=======================

		// fill actionMap
		getActionMap().put(_okAction.getValue(Action.NAME), _okAction);
		getActionMap().put(_cancelAction.getValue(Action.NAME), _cancelAction);
		getActionMap().put(_fileChooserAction.getValue(Action.NAME), _fileChooserAction);

		// fill inputMap
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				_okAction.getValue(Action.NAME));
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				_cancelAction.getValue(Action.NAME));
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke('.'),
				_fileChooserAction.getValue(Action.NAME));

		// adjust inputMaps of buttons
		cancelButton.getActionMap().setParent(getActionMap());
		fileChooserButton.getActionMap().setParent(getActionMap());
		cancelButton.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				_cancelAction.getValue(Action.NAME));
		fileChooserButton.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				_fileChooserAction.getValue(Action.NAME));
	}

	// this is called, when the panel already was added to the dialog.
	// so we can set our selection here.
	// note: if set selection without having a window as ancestor,
	// there will be a NuPoExc
	public void setWindow(Window window) {
		super.setWindow(window);

		// preselect first configuration
		if (_proxies.size() > 0) {
			_jlist.setSelectedIndex(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	public Dimension getPreferredSize() {
		return StringConverter.toDimension(EProperties.getInstance().getProperty("OPEN_CONFIGURATION_PANEL_SIZE"));
	}

	// ================ methods for getting the input result //=================

	public short getConfiguraionType() {
		return _configurationType;
	}

	public Proxy getProxy() {
		return _selectedProxy;
	}

	public String getCanonicalPath() {
		return _canonicalPath;
	}

	// ======================== event processing //=============================

	void performUpdate() {

		// enable or disable components
		_jlist.setEnabled(_radioButton1.isSelected());
		_textArea.setEnabled(_radioButton1.isSelected());
		_jtextField.setEnabled(_radioButton2.isSelected());
		_fileChooserAction.setEnabled(_radioButton2.isSelected());

		// set description text to textarea
		Proxy proxy = _jlist.getSelectedValue();
		if (proxy == null) {
			_textArea.setText(""); //$NON-NLS-1$
		}
		else {
			_textArea.setText(proxy.get(Proxy.Tag.DESCRIPTION));
		}

		// set data according to user's selection
		String s = _jtextField.getText();
		if (_radioButton1.isSelected() && proxy != null) {
			_configurationType = BASIC;
			_selectedProxy = proxy;
		}
		else if (_radioButton2.isSelected() && !s.equals("")) { //$NON-NLS-1$
			_configurationType = LOCAL;
			_canonicalPath = s;
		}
		else {
			_configurationType = NO_CONFIGURATION;
		}

		// enable or disable okButton
		_okAction.setEnabled(_configurationType != NO_CONFIGURATION);
	}

	public void performCancelAction() {
		_configurationType = NO_CONFIGURATION;
		SwingUtilities.windowForComponent(OpenConfigurationPanel.this).dispose();
	}

	void performOkAction() {
		if (_configurationType != NO_CONFIGURATION) {
			SwingUtilities.windowForComponent(OpenConfigurationPanel.this).dispose();
		}
	}

	void performFilechooserAction() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(IOUtil.getFileFilter());
		int returnVal = chooser.showOpenDialog(OpenConfigurationPanel.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try {
				_jtextField.setText(chooser.getSelectedFile().getCanonicalPath());
				performUpdate();
			} catch (IOException exc) {
				exc.printStackTrace();
			}
		}
	}
}
