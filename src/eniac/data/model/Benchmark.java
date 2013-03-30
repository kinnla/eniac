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
 * Created on 30.03.2004
 */
package eniac.data.model;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import org.xml.sax.Attributes;

import eniac.Manager;
import eniac.data.model.unit.Unit;
import eniac.io.Tags;
import eniac.io.XMLUtil;
import eniac.simulation.Frequency;
import eniac.util.Status;

/**
 * @author zoppke
 */
public class Benchmark extends EData implements Observer {

    // circular array of frequencies. One frequency per second.
    private Frequency[] _freqs;

    // points to the next frequency to be overwritten
    private int _pointer = 0;

    // timer
    private Timer _timer = new Timer();

    // task for updating frequencies. To be scheduled at the timer.
    private UpdateTask _task = new UpdateTask();

    // timestamps from when we computed a frequency last time.
    private long _lastRealTime = 0L;

    private long _lastSimTime = 0L;

    ///////////////////////////////// lifecycle ///////////////////////////////

    /**
     * @param type
     */
    public Benchmark() {
        // empty constructor
    }

    public void init() {
        super.init();
        ((Unit) getParent()).getHeaters().addObserver(this);

        // initialize frequencies and schedule task,
        // in case power is switched on.
        update(null, null);
    }

    public void setAttributes(Attributes attrs) {
        super.setAttributes(attrs);
        int size = XMLUtil.parseInt(attrs, Tags.SIZE);
        _freqs = new Frequency[size];
    }

    public void dispose() {
        _task.cancel();
        _timer.cancel();
        super.dispose();
    }

    ///////////////////////////////// methods /////////////////////////////////

    public void update(Observable o, Object args) {
        if (hasPower()) {

            // power switch on. Init frequency array with zero line.
            Frequency freq = Frequency.getNew();
            freq.setLinear(0.01);
            Arrays.fill(_freqs, freq);

            // schedule task.
            _task = new UpdateTask();
            _timer.schedule(_task, 0, 1000);
        } else {

            // power switched off. Cancel task.
            _task.cancel();
        }
    }

    void updateFrequencies() {

        // init variables
        long simTime = Status.getLong("simulation_time");
        long realTime = System.currentTimeMillis();
        long simTimeDiff = simTime - _lastSimTime;
        long realTimeDiff = realTime - _lastRealTime;

        // compute frequency and its logarithmic scale.
        // the result will be within [0..1] on a logarithmic scale
        double freq = CyclingLights.simToReal(simTimeDiff)
                / (double) realTimeDiff;
        _freqs[_pointer] = Frequency.getNew();
        _freqs[_pointer].setLinear(freq);

        // adjust pointer and timestamps
        _pointer = ++_pointer % _freqs.length;
        _lastSimTime = simTime;
        _lastRealTime = realTime;

        // call for repaint
        setChanged();
        notifyObservers(EData.REPAINT);
    }

    public Frequency[] getFrequencies() {
        return _freqs;
    }

    public int getPointer() {
        return _pointer;
    }

    public String getAttributes() {
        return super.getAttributes()
                + XMLUtil.wrapAttribute(Tags.SIZE, Integer
                        .toString(_freqs.length));
    }

    ///////////////////////// private class UpdateTask ////////////////////////

    private class UpdateTask extends TimerTask {
        public void run() {
            if (Manager.getInstance().getLifecycleState() == Manager.STATE_RUNNING) {
                updateFrequencies();
            }
        }
    }
}
