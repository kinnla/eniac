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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import eniac.lang.Dictionary;

/**
 * @author zoppke
 */
public class TextPanel extends DialogPanel {

	private JTextArea _textArea;

	private JScrollPane _scrollPane;

	private JButton _okButton;

	private Action _okAction;

	private String _text;

	/**
	 * @param lm
	 */
	public TextPanel(String text) {
		super(new BorderLayout());
		_text = text;
	}

	public void init() {

		// create and add okAction
		// we perform cancelAction, because ok means the same as cancel here.
		_okAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				performCancelAction();
			}
		};
		_okAction.putValue(Action.NAME, Dictionary.OK.getText());
		getActionMap().put(_okAction.getValue(Action.NAME), _okAction);

		// init components
		_textArea = new JTextArea(_text);
		_textArea.setEditable(false);
		_textArea.setTabSize(3);
		_scrollPane = new JScrollPane(_textArea);
		_okButton = new JButton(_okAction);

		// add components
		add(_scrollPane, BorderLayout.CENTER);
		add(_okButton, BorderLayout.SOUTH);

		// fill action- and inputMap
		getActionMap().put(Dictionary.OK, _okAction);
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				_okAction.getValue(Action.NAME));
		getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				_okAction.getValue(Action.NAME));
	}

	/**
	 * 
	 * @see eniac.menu.actions.gui.DialogPanel#performCancelAction()
	 */
	public void performCancelAction() {
		SwingUtilities.windowForComponent(TextPanel.this).dispose();
	}
}
