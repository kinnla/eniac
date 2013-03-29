/*
 * Created on 11.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.model.unit;

import java.util.Observable;
import java.util.Observer;

import eniac.data.model.EData;
import eniac.data.model.Slider;
import eniac.data.model.parent.CycleCounter;
import eniac.data.model.sw.Switch;
import eniac.data.type.EType;
import eniac.data.type.ProtoTypes;
import eniac.simulation.EEvent;
import eniac.simulation.EEventListener;
import eniac.simulation.Frequency;
import eniac.util.Status;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Cycling extends Unit implements Observer, EEventListener {

    ///////////////////////////// constants ///////////////////////////////////

    // iteration switch values
    //private static final int ITERATION_SINGLE = 0;
    //private static final int ITERATION_ADDITION = 1;
    private static final int ITERATION_INFINITY = 3;

    /////////////////////////////// lifecycle /////////////////////////////////

    public Cycling() {
        // empty
    }

    public void init() {
        // super initializes children
        super.init();

        // add this as dataListener to components
        getGarten().getKind(ProtoTypes.STEP_BUTTON, 0).addObserver(this);
        getGarten().getKind(ProtoTypes.ITERATION_SWITCH, 0).addObserver(this);
        getGarten().getKind(ProtoTypes.FREQUENCY_SLIDER, 0).addObserver(this);

        // add this as eevent listener to eevent manager
        getConfiguration().getCyclingLights().addEEventListener(this,
                EEvent.GENERATE_NEW);

        // init simulator's wanted frequency, power and simulator's stoptime
        updateFrequency();
        updatePower();
    }

    ///////////////////////////////// methods /////////////////////////////////

    private void updatePower() {

        // adjust simulator and event processing to the new state of power
        if (hasPower()) {
            // if power on, then init simulator by inserting first set of events
            getConfiguration().getCyclingLights().initEvents();
            // update iteration. This will take place in case of infinity.
            updateIteration();
        } else {
            // if power off, then reset simulator and array of events for reuse
            getConfiguration().getCyclingLights().reset();
        }
    }

    private void updateIteration() {
        if (hasPower()) {
            // get value of iteration switch
            Switch iteration = (Switch) getGarten().getKind(
                    ProtoTypes.ITERATION_SWITCH, 0);
            int value = iteration.getValue();

            // update simulator's stoptime according to iteration switch
            long stopTime;
            if (value == ITERATION_INFINITY) {
                stopTime = Long.MAX_VALUE;
            } else {
                stopTime = Status.getLong("simulation_time");
            }
            getConfiguration().getCyclingLights().setStopTime(stopTime);
        }
    }

    private void updateFrequency() {
        Slider s = (Slider) getGarten().getKind(ProtoTypes.FREQUENCY_SLIDER, 0);
        Frequency freq = Frequency.getNew();
        freq.setLogarithmic(s.getValue());
        getConfiguration().getCyclingLights().setWantedFrequency(freq);
    }

    /**
     * @return @see eniac.data.unit.Unit#getHeaters()
     */
    public Switch getHeaters() {
        return (Switch) getGarten().getKind(ProtoTypes.HEATERS, 0);
    }

    //////////////////////////// event listening //////////////////////////////

    /**
     * @param e
     * @see eniac.simulation.EEventListener#process(eniac.simulation.EEvent)
     */
    public void process(EEvent e) {

        // we are listening to GENERATE_NEW.
        // increment cyclecounter
        EData data = getGarten().getKind(ProtoTypes.CYCLE_COUNTER, 0);
        ((CycleCounter) data).incrementValue();
    }

    public void update(Observable o, Object args) {
        EType type = ((EData) o).getType();
        if (type == ProtoTypes.HEATERS) {
            updatePower();
        } else if (type == ProtoTypes.ITERATION_SWITCH) {
            updateIteration();
        } else if (type == ProtoTypes.FREQUENCY_SLIDER) {
            updateFrequency();
        } else if (type == ProtoTypes.STEP_BUTTON) {
            // button has been changed.
            // Check that button was pressed (not released)
            // and that cyclingunit has power
            if (((Switch) o).isValue() && hasPower()) {

                // get mode of iteration, as set by iteration switch.
                Switch iterationSwitch = (Switch) getGarten().getKind(
                        ProtoTypes.ITERATION_SWITCH, 0);
                int mode = iterationSwitch.getValue();

                // increase simulators stop-time according to mode.
                getConfiguration().getCyclingLights().updateStopTime(mode);
            }
        }
    }
}