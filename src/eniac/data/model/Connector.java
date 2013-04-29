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
 * Created on 28.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.model;

import org.xml.sax.Attributes;

import eniac.data.PulseInteractor;
import eniac.data.model.parent.Configuration;
import eniac.data.type.ProtoTypes;
import eniac.io.Tags;
import eniac.io.XMLUtil;
import eniac.simulation.EEvent;
import eniac.simulation.EEventListener;
import eniac.util.Status;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Connector extends EData implements PulseInteractor, EEventListener {

    // static key for observation messages
    public static final String CABLE_TRANSMITTION = "cable_transmittion"; //$NON-NLS-1$

    // static keys for pulse direction
    private static final short INPUT = 0;

    private static final short OUTPUT = 1;

    private static final short BOTH = 2;

    private static final String[] DIRECTION = { Tags.IN, Tags.OUT, Tags.BOTH };

    // pulse direction of this connector
    private short _direction;

    // id of the connector, that we are connected with by a cable.
    // if no cable, then this is -1.
    private int _partner;

    // flag indicating whether this connector has a cable.
    // note: This is another thing than having a partner. If a cable ist just
    // created by users click, the connector is plugged, but the cable ends
    // at the mouse-dragging-point. So no partner in this case.
    private boolean _plugged = false;

    // timeslot where we got the last pulse.
    // note: if choose -1 for this, the connector will be painted in highlight
    // mode at startup. I don't know why.
    private long _lastPulse = -11;

    // value of the pulse. If this is a program connector, it is 0 or 1.
    // in case of a digit connector, it is according to the pulse transmission.
    private long _pulseValue = 0;

    //============================ lifecycle
    // ===================================

    public Connector() {
        // empty constructor
    }

    public void setAttributes(Attributes attrs) {
        super.setAttributes(attrs);
        _direction = (short) XMLUtil.parseInt(attrs, Tags.IO, DIRECTION);
        _partner = XMLUtil.parseInt(attrs, Tags.PARTNER);
        //_plugged = _partner >= 0;
        // note: the connector is plugged, when both partners are set to
        // the cable
    }

    //================================ methods
    // =================================

    public String getAttributes() {
        return super.getAttributes()
                + XMLUtil.wrapAttribute(Tags.IO, DIRECTION[_direction])
                + XMLUtil.wrapAttribute(Tags.PARTNER, Integer
                        .toString(_partner));
    }

    public int getPartner() {
        return _partner;
    }

    public void setPartnerID(int id) {
        _partner = id;
    }

    public boolean isPlugged() {
        return _plugged;
    }

    public void setPlugged(boolean b) {
        if (_plugged != b) {
            _plugged = b;
            setChanged();
            notifyObservers(REPAINT);
        }
    }

    public short getDirection() {
        return _direction;
    }

    private boolean isSender() {
        return (_direction == OUTPUT || _direction == BOTH);
    }

    private boolean isReceiver() {
        return (_direction == INPUT || _direction == BOTH);
    }

    private boolean isDigit() {
        return _type == ProtoTypes.DIGIT_CONNECTOR
                || _type == ProtoTypes.DIGIT_CONNECTOR_CROSS
                || _type == ProtoTypes.INTER_CONNECTOR;
    }

    private boolean isProgram() {
        return _type == ProtoTypes.PROGRAM_CONNECTOR;
    }

    public void setLastPulse(long time) {
        if (_lastPulse < time) {
            // adjust pulse time and value
            _lastPulse = time;

            // if we are hightlighting, call for repaint
            if ((Boolean)Status.get("highlight_pulse")) {
                setChanged();
                notifyObservers(EData.PAINT_IMMEDIATELY);
            }
            // set alarm clock for changing our highlightning in the following
            // eevent time slot.
            // note: maybe the highlightning-flag is not set now.
            // But if we are in stepping mode it might switched on before our
            // call-back occures. So we have to set alarm in any case.
            Configuration config = (Configuration) Status
                    .get("configuration");
            config.getCyclingLights().setAlarmClock(time, this);
        }
    }

    public long getLastPulse() {
        return _lastPulse;
    }

    //========================= pulseInteractor methods
    // ========================

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#canReceiveDigit(long,
     *      eniac.data.PulseInteractor)
     */
    public boolean canReceiveDigit(long time, PulseInteractor source) {
        return isReceiver() && isDigit()
                && ((PulseInteractor) getParent()).canReceiveDigit(time, this);
    }

    private long checkDigit(long time, long value) {

        // check, if this is the first pulse within this time slot
        if (_lastPulse < time) {

            // adjust pulse time and value
            setLastPulse(time);
            _pulseValue = value;

        } else {

            // there has been a pulse before. Adjust values
            long oldPulseValue = _pulseValue;
            _pulseValue |= value;
            value = _pulseValue ^ oldPulseValue;
        }
        return value;
    }

    /**
     * @param time
     * @param value
     * @param source
     * @see eniac.data.PulseInteractor#receive(long, int,
     *      eniac.data.PulseInteractor)
     */
    public void receiveDigit(long time, long value, PulseInteractor source) {

        // if value contains no pulse, there is nothing to do
        if (value == 0) {
            return;
        }

        // check value
        value = checkDigit(time, value);

        // transmitt pulse to parent
        // note: we already asked parent if it can receive
        if (value > 0) {
            ((PulseInteractor) _parent).receiveDigit(time, value, this);
        }
    }

    /**
     * @param time
     * @param value
     * @param source
     * @see eniac.data.PulseInteractor#sendDigit(long, long, PulseInteractor)
     */
    public void sendDigit(long time, long value, PulseInteractor source) {

        //if (getType() == ProtoTypes.INTER_CONNECTOR) {
        //    System.out.println("jkjkj"); //$NON-NLS-1$
        //}

        // check that we potentially can send a digit pulse and haven't done
        // in current timeslice
        if (isDigit() && isSender() && value > 0) {

            // check value
            value = checkDigit(time, value);

            // if value contains no pulse, we don't need to send
            if (value == 0) {
                return;
            }

            // check, if we are plugged and have a valid partner
            if (_plugged && _partner >= 0) {

                // send pulse to partner if he can receive.
                Connector c = (Connector) getConfiguration().getIDManager()
                        .get(_partner);
                if (c.canReceiveDigit(time, this)) {
                    c.receiveDigit(time, value, this);
                }
                // notify cable for highlightning
                setChanged();
                notifyObservers(CABLE_TRANSMITTION);
            }
        }
    }

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#canReceiveProgram(long,
     *      eniac.data.PulseInteractor)
     */
    public boolean canReceiveProgram(long time, PulseInteractor source) {
        return isReceiver()
                && isProgram()
                && _lastPulse < time
                && ((PulseInteractor) getParent())
                        .canReceiveProgram(time, this);
    }

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#receiveProgram(long,
     *      eniac.data.PulseInteractor)
     */
    public void receiveProgram(long time, PulseInteractor source) {

        // adjust pulse time
        setLastPulse(time);

        // transmitt pulse to parent.
        // note: it has already been checked, that parent can receive.
        // so we don't ask again.
        ((PulseInteractor) _parent).receiveProgram(time, this);
    }

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#sendProgram(long,
     *      eniac.data.PulseInteractor)
     */
    public void sendProgram(long time, PulseInteractor source) {

        // check if pulse can go and haven't already gone this time
        if (isProgram() && isSender() && _lastPulse < time) {

            // adjust pulse time
            setLastPulse(time);

            // if we are plugged and have a valid partner, transmitt pulse.
            if (_plugged && _partner >= 0) {

                //TODO: synchronize this. maybe cable is removed.
                Connector c = (Connector) getConfiguration().getIDManager()
                        .get(_partner);
                if (c.canReceiveProgram(time, this)) {
                    c.receiveProgram(time, this);
                }
                // notify cable to paint
                setChanged();
                notifyObservers(CABLE_TRANSMITTION);
            }
        }
    }

    //============================= event listening
    // ============================

    /**
     * @param e
     * @see eniac.simulation.EEventListener#process(eniac.simulation.EEvent)
     */
    public void process(EEvent e) {
        if (e.type == EEvent.ALARM && (Boolean)Status.get("highlight_pulse")) {

            // we are called for downlightning
            setChanged();
            notifyObservers(EData.PAINT_IMMEDIATELY);
        }
    }
}
