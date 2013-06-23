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
 * Created on 02.04.2004
 */
package eniac.data.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import eniac.LifecycleListener;
import eniac.Manager;
import eniac.simulation.EEvent;
import eniac.simulation.EEventListener;
import eniac.simulation.EventQueue;
import eniac.simulation.Frequency;
import eniac.util.Status;

/**
 * @author zoppke
 */
public class CyclingLights extends EData implements Runnable, EEventListener, LifecycleListener, Observer {

	// ===================== eeventmanager fields //=======================

	// length of a single and an addition-cycle
	public static final int SINGLE_CYCLE = 10;

	public static final int ADDITION_CYCLE = 200;

	public static final int OFFSET_10P = 3;

	public static final int CPP_TIME = 170;

	// initial length and grow rate of event listeners
	private static final int ARRAY_LENGTH = 3;

	// prototype events for one addition cycle to be reused
	private EEvent[] _events;

	// 2-dim array of listeners
	private EEventListener[][] _listeners;

	private int[] _counters;

	// ================= simulator fields //==============

	private static final int ITERATION_0 = 0;

	private static final int ITERATION_1 = 1;

	private static final int ITERATION_2 = 2;

	private static final int ITERATION_INFINITY = 3;

	// flag indicating whether we should go on simulating.
	private Thread _thread;

	// maximum time until that events should be processed.
	// if there is continuous simulation (iteration-switch==infinity),
	// this will be Long.MAX_VALUE
	private long _stopTime = 0;

	// event queue to store our events
	private EventQueue _queue;

	// frequency we try to maintain during simulation
	private Frequency _frequency;

	// timestamps for computing the appropriate sleeping time between processing
	// two events
	private long _realTimestamp;

	private long _simTimestamp;

	// carry clear gate
	private boolean _ccg;

	// =============================== lifecycle
	// ================================

	/**
	 * @param type
	 */
	public CyclingLights() {
		// empty constructor
	}

	public void init() {
		super.init();

		// add as listener to heaters of cycling unit
		// EData unit =
		// getConfiguration().getGarten().getKind(EType.CYCLING_UNIT, 0);
		// Switch heaters = ((Unit) unit).getHeaters();
		// heaters.addObserver(this);

		// add to starter as busyListener
		Manager.getInstance().addMainListener(this);

		// init eventqueue
		_queue = new EventQueue();

		// create and start simulation thread
		_thread = new Thread(this);
		_thread.start();

		// add ourself as listener to be notified, when new events should be
		// inserted.
		addEEventListener(this, EEvent.GENERATE_NEW);
		addEEventListener(this, EEvent.CCG_UP);
		addEEventListener(this, EEvent.CCG_DOWN);
	}

	public void dispose() {
		super.dispose();

		// if there are threads waiting, stop them.
		synchronized (this) {
			_thread = null;
			notifyAll();
		}
	}

	// =========================== private methods
	// ==============================

	private synchronized void simulate() {

		// check if there is work to do
		while (_queue.isEmpty() || Manager.getInstance().getLifecycleState() != Manager.STATE_RUNNING
				|| _queue.getFirst().time > _stopTime) {

			// if nothing to do, wait for new work
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// check, if we should should stop simulation.
			// this will occure, when lifecycle is set to stopping
			if (_thread == null) {
				return;
			}
		}
		// remove the first event from the queue
		EEvent ev = _queue.removeFirst();

		// update time
		Status.SIMULATION_TIME.setValue(ev.time);

		// process event
		// check, if we have a listener specified
		// (this is the alarm clock case)
		if (ev.listener != null) {
			ev.listener.process(ev);
		}
		else {

			// otherwise look for registered listeners in our arrays.
			EEventListener[] array = _listeners[ev.type];
			for (int i = 0; i < _counters[ev.type]; ++i) {
				array[i].process(ev);
			}
		}

		// maybe sleep in order to maintain frequency.
		// don't sleep, if the following event has the same timestamp.
		if (_queue.getFirst().time > ev.time) {

			// compute time differences
			long realDiff = System.currentTimeMillis() - _realTimestamp;
			long simDiff = ev.time - _simTimestamp;

			// compute sleeping time
			long sleepTime = (long) (simToReal(simDiff) / _frequency.linear() - realDiff);

			// only sleep, if millis are positive
			if (sleepTime > 0) {
				try {
					wait(sleepTime);
				} catch (InterruptedException e) {
					// we should not be interrupted. In case of program exit,
					// we should be notified by propertyChange.
					e.printStackTrace();
				}
			}

			// adjust timestamps
			long realTime = System.currentTimeMillis();
			if (realTime - _realTimestamp > 100) {
				_realTimestamp = realTime;
				_simTimestamp = ev.time;
			}
		}
	}

	// ========================== public methods //=============================

	public void initEvents() {
		// init events.
		// create list for easy initialization
		List<EEvent> list = new LinkedList<>();

		// CPP
		list.add(new EEvent(CPP_TIME, EEvent.CPP));

		// 10P
		for (int i = 0; i < 10; ++i) {
			long time = i * SINGLE_CYCLE + OFFSET_10P;
			EEvent e = new EEvent(time, EEvent.PULSE_10P);
			list.add(e);
		}

		// 9P
		for (int i = 1; i < 10; ++i) {
			long time = i * SINGLE_CYCLE;
			EEvent e = new EEvent(time, EEvent.PULSE_9P);
			list.add(e);
		}

		// 1P
		list.add(new EEvent(10, EEvent.PULSE_1P));

		// 2P
		list.add(new EEvent(20, EEvent.PULSE_2P));
		list.add(new EEvent(30, EEvent.PULSE_2P));

		// 2'P
		list.add(new EEvent(40, EEvent.PULSE_2AP));
		list.add(new EEvent(50, EEvent.PULSE_2AP));

		// 4P
		list.add(new EEvent(60, EEvent.PULSE_4P));
		list.add(new EEvent(70, EEvent.PULSE_4P));
		list.add(new EEvent(80, EEvent.PULSE_4P));
		list.add(new EEvent(90, EEvent.PULSE_4P));

		// 1*P
		list.add(new EEvent(100, EEvent.PULSE_1AP));

		// CCG
		list.add(new EEvent(110, EEvent.CCG_UP));
		list.add(new EEvent(180, EEvent.CCG_DOWN));

		// RP
		list.add(new EEvent(130, EEvent.RP));
		list.add(new EEvent(190, EEvent.RP));

		// GENERATE_NEW
		list.add(new EEvent(ADDITION_CYCLE, EEvent.GENERATE_NEW));

		// NOP
		for (int i = 0; i < 20; ++i) {
			list.add(new EEvent(i * 10 + 6, EEvent.NOP));
		}
		for (int i = 10; i < 20; ++i) {
			list.add(new EEvent(i * 10 + 3, EEvent.NOP));
			list.add(new EEvent(i * 10 + 6, EEvent.NOP));
		}
		list.add(new EEvent(120, EEvent.NOP));
		list.add(new EEvent(140, EEvent.NOP));
		list.add(new EEvent(150, EEvent.NOP));
		list.add(new EEvent(160, EEvent.NOP));

		// init events array from list
		_events = new EEvent[list.size()];
		Collections.shuffle(list);
		list.toArray(_events);

		// insert events to simulator
		insertEvents(_events);
	}

	public void run() {
		while (_thread != null) {
			simulate();
		}
	}

	public synchronized void updateStopTime(int iterationMode) {
		switch (iterationMode) {
			case ITERATION_0 :
				// debug mode. Set stoptime to time of next event
				_stopTime = _queue.getFirst().time;
				break;

			case ITERATION_1 :
				// single single step mode. Icrease stoptime by single cycle.
				_stopTime += SINGLE_CYCLE;
				break;

			case ITERATION_2 :
				// addition cycle step mode. Increase stoptime by addition
// cycle.
				_stopTime += ADDITION_CYCLE;
				break;

			case ITERATION_INFINITY :
				// infinity. Nothing to do.
				break;
		}
		notifyAll();
	}

	public synchronized void setStopTime(long time) {
		_stopTime = time;
		notifyAll();
	}

	public synchronized void insertEvent(EEvent e) {
		_queue.insert(e);
		notifyAll();
	}

	public synchronized void insertEvents(EEvent[] events) {
		_queue.insert(events);
		notifyAll();
	}

	public synchronized void reset() {
		_queue.empty();
		_stopTime = 0L;
		// TODO: this is a hack, because there was a NuPoExc when changing
		// configuration. Find a proper way to init and dispose.
		if (Manager.getInstance().getLifecycleState() == Manager.STATE_RUNNING) {
			Status.SIMULATION_TIME.setValue(0L);
		}
		notifyAll();
	}

	public synchronized void setWantedFrequency(Frequency f) {
		_frequency = f;
		// notify because maybe our thread is sleeping for a long time
		// and in case of speed up we want to process events now.
		notifyAll();
	}

	public static long simToReal(long simTime) {
		return simTime * 5;
	}

	public void setAlarmClock(long time, EEventListener listener) {

		// compute next time slot for alarm
		if (time % 10 == 6) {
			time += 4;
		}
		else {
			time += 3;
		}
		// create and insert event
		EEvent e = new EEvent(time, EEvent.ALARM, listener);
		insertEvent(e);
	}

	public void addEEventListener(EEventListener listener, short eventType) {

		// check if initialized
		if (_listeners == null) {
			// init eeventListener array.
			// recurse on all event types
			Field[] fields = EEvent.class.getFields();
			int max = 0;
			for (int i = 0; i < fields.length; ++i) {
				if (Modifier.isFinal(fields[i].getModifiers())) {
					try {
						max = Math.max(max, fields[i].getInt(null));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// length of arrays is maximum key + 1
			max += 1;
			_counters = new int[max];
			_listeners = new EEventListener[max][];
			for (int i = 0; i < _listeners.length; ++i) {
				_listeners[i] = new EEventListener[ARRAY_LENGTH];
			}
		}

		// check size
		int oldLength = _listeners[eventType].length;
		if (oldLength <= _counters[eventType]) {
			EEventListener[] temp = new EEventListener[oldLength + ARRAY_LENGTH];
			System.arraycopy(_listeners[eventType], 0, temp, 0, oldLength);
			_listeners[eventType] = temp;
		}
		// add listener
		_listeners[eventType][_counters[eventType]++] = listener;
	}

	public boolean isCCG() {
		return _ccg;
	}

	// ========================== eevent processing //==========================

	/**
	 * @param e
	 * @see eniac.simulation.EEventListener#process(eniac.simulation.EEvent)
	 */
	public void process(EEvent e) {

		// switch on the event type
		switch (e.type) {

			case EEvent.GENERATE_NEW :
				// adjust events for the new addition cycle
				for (int i = 0; i < _events.length; ++i) {
					_events[i].time += ADDITION_CYCLE;
				}
				// insert new events to the simulator.
				insertEvents(_events);
				break;

			case EEvent.CCG_UP :
				_ccg = true;
				break;

			case EEvent.CCG_DOWN :
				_ccg = false;
		}
	}

	/**
	 * @param busy
	 * @see eniac.BusyListener#busyChanged(boolean)
	 */
	public synchronized void runLevelChanged(short oldVal, short newVal) {

		// just notify simulation thread. This is useful, when the thread
		// is waiting for the runlevel to idle and go back to work.
		notifyAll();
	}

	/**
	 * @param o
	 * @param arg
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		// adjust simulator and event processing to the new state of power
		// if (hasPower()) {
		// // if power on, then init simulator by inserting first set of events
		// getConfiguration().getCyclingLights().initEvents();
		// // update iteration. This will take place in case of infinity.
		// updateIteration();
		// } else {
		// // if power off, then reset simulator and array of events for reuse
		// getConfiguration().getCyclingLights().reset();
		// }
	}
}
