/*
 * Created on 24.02.2004
 */
package eniac.io;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

import eniac.LifecycleListener;
import eniac.Manager;
import eniac.lang.Dictionary;
import eniac.util.Status;
import eniac.window.EFrame;

/**
 * @author zoppke
 */
public class Progressor extends JDialog implements Runnable, LifecycleListener,
        PropertyChangeListener {

    // jpanel as contentpane
    private JPanel _panel = new JPanel(new BorderLayout());

    // a separate thread is startet in order to show the dialog whith blocking
    // all other gui but without blocking the initialization process.
    private Thread _thread = null;

    // cancel button
    private JButton _button = new JButton();

    // label to display the current task, its progress we are showing
    private JLabel _label = new JLabel(Dictionary.INITIALIZING);

    // progressbar to show progress of our
    private JProgressBar _progressBar = new JProgressBar();

    ////////////////////////////// singleton stuff
    // /////////////////////////////

    // singleton self reference
    private static Progressor instance;

    private Progressor() {

        // create progressor as modal dialog with eframe as owner
        super(EFrame.getInstance(), Dictionary.PLEASE_WAIT, true);

        // add as main listener
        Manager.getInstance().addMainListener(this);

        // add as status listener to be notified when language changes
        Status.getInstance().addListener(this);

        // init components
        //_progressBar.setIndeterminate(true);
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

    ///////////////////////////////// methods
    // //////////////////////////////////

    public void setAction(Action a) {

        // if no action wanted, disable button.
        if (a == null) {
            a = new AbstractAction(Dictionary.CANCEL) {
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
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                Dictionary.CANCEL);

        // adjust inputMaps of buttons
        _button.getActionMap().setParent(_panel.getActionMap());
        _button.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                        Dictionary.CANCEL);
    }

    public void setText(String text) {
        _label.setText(text);
    }

    public void clear() {
        setText(Dictionary.INITIALIZING);
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
        while (Manager.getInstance().getLifecycleState() < Manager.STATE_STOPPED) {

            // show progressor, if applet is busy
            short runlevel = Manager.getInstance().getLifecycleState();
            if (runlevel != Manager.STATE_RUNNING) {
                setVisible(true);
            }

            // check, if we are shuting down.
            if (Manager.getInstance().getLifecycleState() >= Manager.STATE_STOPPED) {
                break;
            }

            // wait until the runlevel changes
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // applet is shutting down. Hide and dispose progressor.
        setVisible(false);
        _thread = null;
        instance = null;
        dispose();
    }

    public synchronized void runLevelChanged(short oldVal, short newVal) {

        // main status changed. If applet is idling, hide progressor.
        // otherwise notify thread to show it.
        if (newVal == Manager.STATE_RUNNING) {
            setVisible(false);
        } else if (newVal == Manager.STATE_STOPPED) {
            setVisible(false);
            notifyAll();
        } else {
            notifyAll();
        }
    }

    /**
     * @param evt
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("language")) {
            _button.setText(Dictionary.CANCEL);
            setTitle(Dictionary.PLEASE_WAIT);
        }
    }
}