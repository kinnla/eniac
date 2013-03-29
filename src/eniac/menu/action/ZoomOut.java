/*
 * Created on 11.04.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import eniac.lang.Dictionary;
import eniac.skin.Skin;
import eniac.util.Status;

/**
 * @author zoppke
 */
public class ZoomOut extends EAction implements PropertyChangeListener {

    public ZoomOut() {
        Status.getInstance().addListener(this);
    }

    private int getNewHeight() {

        // get current height and zoomSteps
        int height = Status.getInt("zoomed_height");
        Skin skin = (Skin) Status.get("skin");
        int[] steps = skin.getZoomSteps();

        // search index of current height in zoomSteps
        int index = Arrays.binarySearch(steps, height);
        if (index < 0) {
            // we are between 2 steps. Choose the upper one.
            index = -index - 1;
        }
        // decrease step, but check that we stay inside array bounds
        return steps[Math.max(index - 1, 0)];
    }

    public void actionPerformed(ActionEvent e) {
        Status.set("zoomed_height", getNewHeight());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("zoomed_height")) {
            int height = Status.getInt("zoomed_height");
            setEnabled(height != getNewHeight());
        } else {
            super.propertyChange(evt);
        }
    }
}