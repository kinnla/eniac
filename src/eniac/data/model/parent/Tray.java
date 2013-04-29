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
 * Created on 17.01.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package eniac.data.model.parent;

import eniac.data.PulseInteractor;
import eniac.data.model.Connector;
import eniac.data.model.EData;
import eniac.data.type.ProtoTypes;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Tray extends ParentData implements PulseInteractor {

    // number of channels in a tray
    private static final int NUMBER_OF_CHANNELS = 11;

    // if we are transmitting a program pulse, this is the index of the
    // active channel
    private int _activeIndex;

    // last time we got a pulse
    private long _lastPulse = -11;

    // value of the pulse
    private long _pulseValue = 0;

    public Tray() {
        // empty
    }

    //========================= PulseInteractor methods
    // ========================

    /**
     * @param time
     * @param source
     * @return @see eniac.data.PulseInteractor#canReceiveDigit(long,
     *         eniac.data.PulseInteractor)
     */
    public boolean canReceiveDigit(long time, PulseInteractor source) {
        // we always can receive a digit, because we won't send any wire twice
        // receiving is intelligent, that means any wire is checked against
        // double pulse.
        return true;
    }

    private long checkDigit(long time, long value) {

        // check, if this is the first pulse within this time slot
        if (_lastPulse < time) {

            // adjust pulse time and value
            _lastPulse = time;
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
     * @see eniac.data.PulseInteractor#receiveDigit(long, int,
     *      eniac.data.PulseInteractor)
     */
    public void receiveDigit(long time, long value, PulseInteractor source) {

        // if value contains no pulse, return immediately
        if (value == 0) {
            return;
        }
        // check value
        value = checkDigit(time, value);
        // send as digit pulse
        sendDigit(time, value, this);

        // send as program pulses
        for (int i = NUMBER_OF_CHANNELS - 1; i >= 0; --i) {
            if (value % 10 > 0) {
                _activeIndex = i;
                sendProgram(time, this);
            }
            value /= 10;
        }
    }

    /**
     * @param time
     * @param value
     * @param source
     * @see eniac.data.PulseInteractor#sendDigit(long, int,
     *      eniac.data.PulseInteractor)
     */
    public void sendDigit(long time, long value, PulseInteractor source) {
        EData[] children = getGarten().getKinder(
                ProtoTypes.DIGIT_CONNECTOR_CROSS);
        for (int i = 0; i < children.length; ++i) {
            Connector c = (Connector) children[i];
            c.sendDigit(time, value, this);
        }
    }

    /**
     * @param time
     * @return @see eniac.data.PulseInteractor#canReceiveProgram(long)
     */
    public boolean canReceiveProgram(long time, PulseInteractor source) {
        return true;
    }

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#receiveProgram(long,
     *      eniac.data.PulseInteractor)
     */
    public void receiveProgram(long time, PulseInteractor source) {
        _activeIndex = ((EData) source).getIndex() % NUMBER_OF_CHANNELS;
        long value = (long) Math.pow(10, 10 - _activeIndex);
        checkDigit(time, value);
        if (value == 0) {
            return;
        }
        sendProgram(time, this);
        sendDigit(time, value, this);
    }

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#sendProgram(long,
     *      eniac.data.PulseInteractor)
     */
    public void sendProgram(long time, PulseInteractor source) {
        int number = getGarten().getNumber(ProtoTypes.PROGRAM_CONNECTOR);
        for (int i = _activeIndex; i < number; i += NUMBER_OF_CHANNELS) {
            Connector c = (Connector) getGarten().getKind(
                    ProtoTypes.PROGRAM_CONNECTOR, i);
            c.sendProgram(time, this);
        }
    }
}
