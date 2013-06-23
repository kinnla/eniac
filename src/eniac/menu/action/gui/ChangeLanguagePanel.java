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
 * Created on 12.04.2004
 */
package eniac.menu.action.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

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
import eniac.lang.Dictionary;
import eniac.util.Status;

/**
 * @author zoppke
 */
public class ChangeLanguagePanel extends DialogPanel {

	// configurationProxies the user can choose from
	private List<Proxy> _proxies;

	private Proxy _selectedProxy = null;

	private JPanel _jpanel;

	JList<Proxy> _jlist;

	private JScrollPane _listPane;

	// Actions
	private Action _cancelAction;

	private Action _okAction;

	public ChangeLanguagePanel(List<Proxy> proxies) {
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

		// ============================ jpanel
		// ==================================

		_jpanel = new JPanel(new GridBagLayout());

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

		// add itemListener to _jlist for display preview image
		_jlist.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				performUpdate();
			}
		});

		// ========================= layout components
		// ==========================

		// add components
		_jpanel.add(_listPane, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.5, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		// add jpanel
		add(_jpanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		// ============================= buttons
		// ================================

		// create and init buttons
		JButton okButton = new JButton(_okAction);
		JButton cancelButton = new JButton(_cancelAction);

		// layout components
		add(okButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

		// ============================= add keystrokes
		// =========================

		// fill actionMap
		getActionMap().put(_okAction.getValue(Action.NAME), _okAction);
		getActionMap().put(_cancelAction.getValue(Action.NAME), _cancelAction);

		// fill inputMap
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), _okAction.getValue(Action.NAME));
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				_cancelAction.getValue(Action.NAME));

		// adjust inputMaps of buttons
		cancelButton.getActionMap().setParent(getActionMap());
		cancelButton.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), _cancelAction.getValue(Action.NAME));
	}

	// this is called, when the panel already was added to the dialog.
	// so we can set our selection here.
	// note: if set selection without having a window as ancestor,
	// there will be a NuPoExc
	public void setWindow(Window window) {
		super.setWindow(window);

		// preselect current language.
		String key = (String) Status.LANGUAGE.getValue();
		int i = -1;
		for (Proxy p : _proxies) {
			++i;
			if (key.equals(p.get(Proxy.Tag.KEY))) {
				_jlist.setSelectedIndex(i);
				return;
			}
		}
	}

	// ========================= event processing
	// ===============================

	void performUpdate() {

		// enable or disable okButton
		_selectedProxy = _jlist.getSelectedValue();
		_okAction.setEnabled(_selectedProxy != null);
	}

	public void performCancelAction() {
		_selectedProxy = null;
		SwingUtilities.windowForComponent(ChangeLanguagePanel.this).dispose();
	}

	void performOkAction() {
		if (_selectedProxy != null) {
			SwingUtilities.windowForComponent(ChangeLanguagePanel.this).dispose();
		}
	}

	public Proxy getSelectedProxy() {
		return _selectedProxy;
	}
}
