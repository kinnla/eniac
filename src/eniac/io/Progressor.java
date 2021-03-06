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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import eniac.Manager;
import eniac.lang.Dictionary;
import eniac.util.Status;
import eniac.util.StatusListener;
import eniac.window.EFrame;

/**
 * @author zoppke
 */
public class Progressor extends JDialog implements Runnable, StatusListener {

	// jpanel as contentpane
	private JPanel _panel = new JPanel(new BorderLayout());

	// a separate thread is startet in order to show the dialog whith blocking
	// all other gui but without blocking the initialization process.
	private Thread _thread = null;

	// cancel button
	private JButton _button = new JButton();

	// label to display the current task, its progress we are showing
	private JLabel _label = new JLabel(Dictionary.INITIALIZING.getText());

	// progressbar to show progress of our
	private JProgressBar _progressBar = new JProgressBar();

	// ============================ singleton stuff
	// =============================

	// singleton self reference
	private static Progressor instance;

	private Progressor() {

		// create progressor as modal dialog with eframe as owner
		super(EFrame.getInstance(), Dictionary.PLEASE_WAIT.getText(), true);

		// add as status listener to be notified when language or runlevel
// changes
		Status.LANGUAGE.addListener(this);
		Status.LIFECYCLE.addListener(this);

		// init components
		// _progressBar.setIndeterminate(true);
		setAction(null);
		_panel.add(_label, BorderLayout.NORTH);
		_panel.add(_progressBar, BorderLayout.CENTER);
		_panel.add(_button, BorderLayout.SOUTH);
		setContentPane(_panel);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// bring it to the screen.
		pack();
		setLocationRelativeTo(EFrame.getInstance());

		// start thread that controlles visibility
		_thread = new Thread(this);
		_thread.start();
	}

	public static Progressor getInstance() {
		if (instance == null) {
			instance = new Progressor();
		}
		return instance;
	}

	// =============================== methods
	// ==================================

	public void setAction(Action a) {

		// if no action wanted, disable button.
		if (a == null) {
			a = new AbstractAction(Dictionary.CANCEL.getText()) {
				public void actionPerformed(ActionEvent e) {
					// empty
				}

				public boolean isEnabled() {
					return false;
				}
			};
		}

		// create and add action
		_panel.getActionMap().put(a.getValue(Action.NAME), a);

		// map action to button
		_button.setAction(a);
		_button.setEnabled(true);

		// fill actionMap
		_panel.getActionMap().put(Dictionary.CANCEL, a);

		// fill inputMap
		_panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Dictionary.CANCEL);

		// adjust inputMaps of buttons
		_button.getActionMap().setParent(_panel.getActionMap());
		_button.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				Dictionary.CANCEL);
	}

	public void setText(String text) {
		_label.setText(text);
	}

	public void clear() {
		setText(Dictionary.INITIALIZING.getText());
		_progressBar.setIndeterminate(true);
	}

	public void incrementValue() {
		_progressBar.setValue(_progressBar.getValue() + 1);
	}

	public void setProgress(int value, int max) {
		_progressBar.setIndeterminate(false);
		_progressBar.setValue(value);
		_progressBar.setMaximum(max);
	}

	public void run() {

		// run this thread until applet is shutting down.
		while (Status.LIFECYCLE.getValue() != Manager.LifeCycle.STOPPED
				|| Status.LIFECYCLE.getValue() != Manager.LifeCycle.DESTROYED) {

			// show progressor, if applet is busy
			if (Status.LIFECYCLE.getValue() != Manager.LifeCycle.RUNNING) {
				setVisible(true);
			}

			// wait until the runlevel changes
			synchronized (this) {
				if (Status.LIFECYCLE.getValue() == Manager.LifeCycle.BLOCKED) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// applet is shutting down. Hide and dispose progressor.
		setVisible(false);
		_thread = null;
		instance = null;
		dispose();
	}

	public synchronized void statusChanged(Status status, Object newValue) {
		assert status == Status.LANGUAGE || status == Status.LIFECYCLE;

		switch (status) {
			case LANGUAGE :
				_button.setText(Dictionary.CANCEL.getText());
				setTitle(Dictionary.PLEASE_WAIT.getText());
				break;

			case LIFECYCLE :
				// If applet is idling, hide progressor.
				// otherwise notify thread to show it.
				if (newValue == Manager.LifeCycle.RUNNING) {
					setVisible(false);
				}
				else if (newValue == Manager.LifeCycle.STOPPED) {
					setVisible(false);
					notifyAll();
				}
				else {
					notifyAll();
				}
				break;

			default :
				break;
		}
	}
}
