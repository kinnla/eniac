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
 * Created on 11.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.model.unit;

import java.util.Observable;

import eniac.data.IDManager;
import eniac.data.PulseInteractor;
import eniac.data.model.Connector;
import eniac.data.model.CyclingLights;
import eniac.data.model.EData;
import eniac.data.model.parent.BlinkenLights;
import eniac.data.model.sw.Switch;
import eniac.data.model.sw.SwitchAndFlag;
import eniac.data.type.ProtoTypes;
import eniac.simulation.EEvent;
import eniac.simulation.EEventListener;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Accumulator extends Unit implements EEventListener {

    // static keys for left or right interconnection partner detection
    private static final int PARTNER_LEFT = 0;

    private static final int PARTNER_RIGHT = 2;

    // static keys definig the accumulator's interconnection status
    private static final short NONE = 0;

    private static final short SINGLE = 1;

    private static final short LEFT_HAND = 2;

    private static final short RIGHT_HAND = 3;

    // static keys for operation switch setting
    private static final int EPSILON = 4, A = 6, AS = 7, S = 8;

    // current program we are working on
    private SwitchAndFlag _currentOperation = null;

    private long _programStartTime = 1;

    // stop time of the current program
    private int _repeatCounter = 0;

    // current transmission cycle if we are transmitting
    private int _transmissionCycle = 0;

    ///////////////////////////// lifecycle ///////////////////////////////////

    public Accumulator() {
        // empty
    }

    public void init() {

        // super inits children and data-listening to heaters
        super.init();

        // add this as eventlistener
        CyclingLights lights = getConfiguration().getCyclingLights();
        lights.addEEventListener(this, EEvent.PULSE_10P);
        lights.addEEventListener(this, EEvent.PULSE_9P);
        lights.addEEventListener(this, EEvent.CPP);
        lights.addEEventListener(this, EEvent.PULSE_1AP);
        lights.addEEventListener(this, EEvent.RP);
    }

    ///////////////////////////// unit methods ////////////////////////////////

    public Switch getHeaters() {
        return (Switch) getGarten().getKind(ProtoTypes.ACCU_HEATERS, 0);
    }

    /////////////////////////////// methods ///////////////////////////////////

    private boolean hasActiveProgram(long time) {
        return _programStartTime < time && _repeatCounter > 0;
    }

    private void maybeSendProgram(long time, PulseInteractor source) {

        // if we have been performing a repeating program that is finished now,
        // reset current program and send a program pulse.
        if (_currentOperation != null && _currentOperation.getIndex() >= 4
                && _repeatCounter == 1 && time % 200 == CyclingLights.CPP_TIME) {

            sendProgram(time, source);
        }
    }

    private boolean isTransmitting(long time) {

        // if no program, then we are not transmitting
        if (!hasActiveProgram(time)) {
            return false;
        }
        // if cycle is > 0, then we are transmitting
        if (_transmissionCycle > 0) {
            return true;
        }
        // if operation switch is set to sending, then we are transmitting
        return (_currentOperation.getValue() >= A);
    }

    private boolean isReceiving(long time) {

        // if no program, then we are not transmitting
        if (!hasActiveProgram(time)) {
            return false;
        }
        // if operation switch is set to receiving, then we are.
        return (_currentOperation.getValue() < A);
    }

    public void clear() {
        if (hasPower()) {
            _currentOperation = null;
            _programStartTime = 0;
            _repeatCounter = 0;
            _transmissionCycle = 0;
            clearSignificiant();
        }
    }

    private void clearSignificiant() {
        // reset blinkenlights according to significiant figures switch
        EData sw = getGarten().getKind(ProtoTypes.SIGNIFICIANT_FIGURES_SWITCH,
                0);
        int significiant = ((Switch) sw).getValue();
        long l = 5 * (long) Math.pow(10, 9 - significiant);
        BlinkenLights blinkens = (BlinkenLights) getBlinkens();
        blinkens.setNumber(l);
        blinkens.clearCarry();
    }

    private boolean hasLoadBox(long time) {
        Connector con = (Connector) getGarten().getKind(
                ProtoTypes.INTER_CONNECTOR, 2);
        boolean loadBox = con.getPartner() == con.getID();
        if (loadBox) {
            con.setLastPulse(time);
        }
        return loadBox;
    }

    private boolean isLeftEnd(long time) {

        // determine partners of our left interconnectors
        Connector c1 = (Connector) getGarten().getKind(
                ProtoTypes.INTER_CONNECTOR, 0);
        Connector c2 = (Connector) getGarten().getKind(
                ProtoTypes.INTER_CONNECTOR, 1);
        boolean leftEnd = c1.getPartner() == c2.getID()
                && c2.getPartner() == c1.getID();

        if (leftEnd) {
            c1.setLastPulse(time);
            c2.setLastPulse(time);
        }
        return leftEnd;
    }

    private Accumulator getInterconnectionPartner(int partner, long time) {

        // determine partners of the specified interconnectors
        Connector c1 = (Connector) getGarten().getKind(
                ProtoTypes.INTER_CONNECTOR, partner);
        Connector c2 = (Connector) getGarten().getKind(
                ProtoTypes.INTER_CONNECTOR, partner + 1);
        int id1 = c1.getPartner();
        int id2 = c2.getPartner();

        // check, if ids point to real components
        if (id1 < 0 || id2 < 0) {
            return null;
        }

        IDManager idman = getConfiguration().getIDManager();
        EData d1 = idman.get(id1);
        EData d2 = idman.get(id2);

        // determine parents. Only Accumulators have Interconnectors,
        // so no ClasscastException can occure
        Accumulator p1 = (Accumulator) d1.getParent();
        Accumulator p2 = (Accumulator) d2.getParent();

        // if both connector-partners have different parents,
        // or if we are the parent, then no parner.
        if (p1 != p2 || p1 == this) {
            return null;
        }

        // if connectors are at the correct indices at its parent,
        // then we have a partner
        int i1 = (partner + 2) % 4;
        int i2 = (partner + 3) % 4;
        if (d1.getIndex() == i1 && d2.getIndex() == i2) {
            c1.setLastPulse(time);
            c2.setLastPulse(time);
            return p1;
        }
        return null;
    }

    private short getInterconnectionState(long time) {
        if (isLeftEnd(time) && hasLoadBox(time)) {
            return SINGLE;
        }
        Accumulator partner = getInterconnectionPartner(PARTNER_LEFT, time);
        if (partner != null && partner.isLeftEnd(time) && hasLoadBox(time)) {
            return RIGHT_HAND;
        }
        partner = getInterconnectionPartner(PARTNER_RIGHT, time);
        if (partner != null && isLeftEnd(time) && partner.hasLoadBox(time)) {
            return LEFT_HAND;
        }
        return NONE;
    }

    public void setOperation(SwitchAndFlag operationSwitch, int repeat) {
        _currentOperation = operationSwitch;
        _repeatCounter = repeat;
    }

    ////////////////////////////// observer methods
    // ////////////////////////////

    /**
     * @param data
     * @see eniac.data.DataListener#dataChanged(eniac.data.EData)
     */
    public void update(Observable o, Object args) {
        //TODO: map blinkenlights directly to heaters.
        // unit should not be an observer any more.
        if (((EData) o).getType() == ProtoTypes.ACCU_HEATERS) {

            // power changed. clear accumulator and en- or disable blinkenlights
            clear();
            ((BlinkenLights) getBlinkens()).setEnabled(hasPower());
        }
    }

    //////////////////////////// pulseInteractor methods
    // ///////////////////////

    /**
     * @param time
     * @return @see eniac.data.PulseInteractor#canReceiveProgram(long)
     */
    public boolean canReceiveProgram(long time, PulseInteractor source) {
        return hasPower() && getInterconnectionState(time) > NONE;
    }

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#receiveProgram(long,
     *      eniac.data.PulseInteractor)
     */
    public void receiveProgram(long time, PulseInteractor source) {
        // before starting a new program, check whether a program just ended
        // now. The appropriate process()-call maybe will occure later.
        // We don't want to overwrite any information.
        if (_programStartTime < time) {
            maybeSendProgram(time, this);
        }

        // adjust program start time
        _programStartTime = time;

        // determine index of program
        EData d = (EData) source;
        int index = d.getIndex();

        // assume that program was triggered by a single program connector.
        // so program will be executed only once.
        _repeatCounter = 1;
        _currentOperation = (SwitchAndFlag) getGarten().getKind(
                ProtoTypes.OPERATION_SWITCH, index);
        //TODO: rewrite determination of currentOperation

        // check assumption.
        if (d.getType() == ProtoTypes.PROGRAM_CONNECTOR_PAIR) {

            // so assumption was wrong and we have to correct the settings.
            // program might be repeated multiple times.
            EData sw = getGarten().getKind(ProtoTypes.REPEAT_SWITCH, index);
            int repeat = ((Switch) sw).getValue();
            _repeatCounter += repeat;
            _currentOperation = (SwitchAndFlag) getGarten().getKind(
                    ProtoTypes.OPERATION_SWITCH, index + 4);
        }
        // if we have a partner, we must set its operation
        Accumulator partner = getInterconnectionPartner(PARTNER_LEFT, time);
        if (partner == null) {
            partner = getInterconnectionPartner(PARTNER_RIGHT, time);
            if (partner == null) {
                return;
            }
            //			System.out.println(
            //				"Accu"
            //					+ getIndex()
            //					+ " has right partner "
            //					+ partner.getIndex());
        } else {
            //			System.out.println(
            //				"Accu"
            //					+ getIndex()
            //					+ " has left partner "
            //					+ partner.getIndex());
        }
        partner.setOperation(_currentOperation, _repeatCounter);
        partner.setProgramStartTime(time);
    }

    public void setProgramStartTime(long time) {
        _programStartTime = time;
    }

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#sendProgram(long,
     *      eniac.data.PulseInteractor)
     */
    public void sendProgram(long time, PulseInteractor source) {

        // if operationSwitch is not our child, return.
        // note: it could be the child of our partner, too
        if (_currentOperation.getParent() != this) {
            return;
        }
        // sending program pulse through connector pairs
        int index = _currentOperation.getIndex() - 4;
        EData d = getGarten().getKind(ProtoTypes.PROGRAM_CONNECTOR_PAIR, index);
        ((PulseInteractor) d).sendProgram(time, this);
    }

    /**
     * @param time
     * @return @see eniac.data.PulseInteractor#canReceiveDigit(long)
     */
    public boolean canReceiveDigit(long time, PulseInteractor source) {

        // check simple conditions:
        // - having power
        // - program is active
        if (!hasPower() || !hasActiveProgram(time)) {
            return false;
        }

        // if this is a pulse coming through a interconnector,
        // then we can receive.
        if (((EData) source).getType() == ProtoTypes.INTER_CONNECTOR) {
            return true;
        }

        // check if we are currently listening to this digitConnector
        return ((EData) source).getIndex() == _currentOperation.getValue();
    }

    /**
     * @param time
     * @param value
     * @param source
     * @see eniac.data.PulseInteractor#receiveDigit(long, int,
     *      eniac.data.PulseInteractor)
     */
    public void receiveDigit(long time, long value, PulseInteractor source) {

        // if common digit received, modify blinkenlights number by the value
        ((BlinkenLights) getBlinkens()).rotateNumber(value);
    }

    private void carryAsLeftPartner(boolean rotate) {
        BlinkenLights blinkens = (BlinkenLights) getBlinkens();
        if (rotate) {
            //System.out.println("receiving code 11");
            EData number = blinkens.getGarten().getKind(
                    ProtoTypes.BLINKEN_NUMBER_SWITCH, 9);
            ((SwitchAndFlag) number).rotateValue();
        }

        boolean carry = blinkens.carryOver();
        //System.out.println("partner-carryover performed");
        // check, if further carry adjustment is needed.
        if (carry) {
            // toggle the sign
            EData sign = blinkens.getGarten().getKind(
                    ProtoTypes.BLINKEN_SIGN_SWITCH, 0);
            ((Switch) sign).toggleValue();
        }
        // clear carryover flags
        blinkens.clearCarry();
    }

    /**
     * @param time
     * @param value
     * @param source
     * @see eniac.data.PulseInteractor#sendDigit(long, int,
     *      eniac.data.PulseInteractor)
     */
    public void sendDigit(long time, long value, PulseInteractor source) {
        int operation = _currentOperation.getValue();
        if (operation == A || operation == AS) {
            // send positive
            EData con = getGarten().getKind(ProtoTypes.DIGIT_CONNECTOR, 5);
            ((PulseInteractor) con).sendDigit(time, value, this);
        }
        if (operation == AS || operation == S) {
            // send negative
            EData con = getGarten().getKind(ProtoTypes.DIGIT_CONNECTOR, 6);
            ((PulseInteractor) con).sendDigit(time, 11111111111L - value, this);
        }
    }

    ///////////////////////////// eeventListener methods
    // ///////////////////////

    /**
     * @param e
     * @see eniac.simulation.EEventListener#process(eniac.simulation.EEvent)
     */
    public void process(EEvent e) {
        switch (e.type) {

        // CPP
        case EEvent.CPP:

            // maybe send program, if a program finished just now
            if (_programStartTime < e.time) {
                maybeSendProgram(e.time, this);
            }

            // if program didn't started right now, decrease repeatCounter.
            // note: programStartTime could be changed by "maybeSendProgram"
            // above, so it must be checked again.
            if (_programStartTime < e.time) {
                _repeatCounter--;
            }
            break;

        // PULSE_10P
        case EEvent.PULSE_10P:
            // rotate number, if operation switch is set to transmission
            if (isTransmitting(e.time)) {
                ((BlinkenLights) getBlinkens()).rotateNumber(1111111111L);
                _transmissionCycle++;
                _transmissionCycle %= 10;
            }
            break;

        //PULSE_9P
        case EEvent.PULSE_9P:
            // transmitt pulse, if operation switch is set to transmission
            if (isTransmitting(e.time)) {
                BlinkenLights blinkens = (BlinkenLights) getBlinkens();
                long pulse = blinkens.computePulse(_transmissionCycle);
                sendDigit(e.time, pulse, this);
            }
            break;

        // PULSE_1AP:
        // if complementary transmittion, send correcting pulse
        // if receiving/O and clear correct is set and we have loadbox,
        // pick up as correcting pulse
        case EEvent.PULSE_1AP:
            perform1AP(e);
            break;

        // RP:
        // clear accus if carryClear is set and we are O,A,AS,S
        // perform carryOver computation according to interconnection
        // state
        case EEvent.RP:
            performRP(e);
            break;
        }
    }

    private void perform1AP(EEvent e) {

        // check, if transmitting complementary
        if (isTransmitting(e.time)
                && (_currentOperation.getValue() == AS || _currentOperation
                        .getValue() == S)) {

            // determine setting of significiant figures switch
            EData d = getGarten().getKind(
                    ProtoTypes.SIGNIFICIANT_FIGURES_SWITCH, 0);
            int figure = ((Switch) d).getValue();

            // if fugure is 0, then we don't need to send
            if (figure == 0) {
                return;
            }

            // otherwise compute pulse to send
            long pulse = (long) Math.pow(10, 10 - figure);

            // send correcting pulse through S output
            EData con = getGarten().getKind(ProtoTypes.DIGIT_CONNECTOR, 6);
            ((Connector) con).sendDigit(e.time, pulse, this);

            // check, if we are receiving, if clear-correct-switch is set
            // and if we have a load box
        } else if (isReceiving(e.time) && _currentOperation.isFlag()
                && hasLoadBox(e.time)) {
            // send correcting pulse throug our input connector
            // note: this asserts, that just one pulse is received
            // in this timeslot. We might receive a complementary
            // number and get a pulse at PULSE_1AP anyway.
            // but this would be blocked by the commector.
            int v = _currentOperation.getValue();
            EData con = getGarten().getKind(ProtoTypes.DIGIT_CONNECTOR, v);
            ((Connector) con).receiveDigit(e.time, 1, this);
        }
    }

    private void performRP(EEvent e) {

        // if carry clear gate is low, there is nothing to do.
        if (!getConfiguration().getCyclingLights().isCCG()) {
            return;
        }

        // if carryClear is set, clear deacde counters after sending/zero
        if (hasActiveProgram(e.time) && _currentOperation.isFlag()) {

            // check, that we are sending or performing zero
            if (_currentOperation.getValue() > EPSILON) {
                // clear decade counters
                clearSignificiant();
            }
        }

        // carryover
        switch (getInterconnectionState(e.time)) {

        case NONE:
            // not wired, so nothing to do
            break;

        case SINGLE:
            BlinkenLights blinkens = (BlinkenLights) getBlinkens();
            if (isReceiving(e.time)) {

                // perform carryover
                boolean carry = blinkens.carryOver();

                // check, if further carry adjustment is needed.
                if (isLeftEnd(e.time) && carry) {
                    // if we are single, toggle the sign
                    EData sign = blinkens.getGarten().getKind(
                            ProtoTypes.BLINKEN_SIGN_SWITCH, 0);
                    ((Switch) sign).toggleValue();
                }
            }
            // clear carryover flags
            blinkens.clearCarry();
            break;

        case LEFT_HAND:
            // wait for the carryover-call of our right hand partner
            break;

        case RIGHT_HAND:
            blinkens = (BlinkenLights) getBlinkens();
            Accumulator partner = getInterconnectionPartner(PARTNER_LEFT,
                    e.time);
            if (isReceiving(e.time)) {
                // perform carryover
                boolean carry = blinkens.carryOver();
                // send program pulse through interconnector
                // note: If we have 1 carryover, we send it as 2.
                // because this also will trigger the carry-over
                // at our partner and we send 1 to encode no carry.
                partner.carryAsLeftPartner(carry);
                //					long value = carry ? 11 : 1;
                //					System.out.println("sending carryover, code: " + value);
                //					if (value == 11) {
                //						System.out.println("poupou"); //$NON-NLS-1$
                //					}
                //					EData con =
                //						getGarten().getKind(ProtoTypes.INTER_CONNECTOR, 0);
                //					((Connector) con).sendDigit(e.time, value, this);
            }
            blinkens.clearCarry();
            ((BlinkenLights) partner.getBlinkens()).clearCarry();
            break;
        }
    }
}
