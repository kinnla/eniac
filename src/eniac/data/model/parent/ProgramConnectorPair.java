/*
 * Created on 02.04.2004
 */
package eniac.data.model.parent;

import eniac.data.PulseInteractor;
import eniac.data.model.Connector;
import eniac.data.model.EData;

/**
 * @author zoppke
 */
public class ProgramConnectorPair extends ParentData implements PulseInteractor {

    private long _lastPulse;

    public ProgramConnectorPair() {
        // empty
    }

    /**
     * @param time
     * @return @see eniac.data.PulseInteractor#canReceiveProgram(long)
     */
    public boolean canReceiveProgram(long time, PulseInteractor source) {
        return _lastPulse < time
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
        // note: we already asked parent whether it can receive
        ((PulseInteractor) _parent).receiveProgram(time, this);
        _lastPulse = time;
    }

    /**
     * @param time
     * @param source
     * @see eniac.data.PulseInteractor#sendProgram(long,
     *      eniac.data.PulseInteractor)
     */
    public void sendProgram(long time, PulseInteractor source) {
        if (_lastPulse < time) {
            EData[] children = getGarten().getAllKinder();
            for (int i = 0; i < children.length; ++i) {
                Connector c = (Connector) children[i];
                c.sendProgram(time, this);
            }
            _lastPulse = time;
        }
    }

    /**
     * @param time
     * @param source
     * @return @see eniac.data.PulseInteractor#canReceiveDigit(long,
     *         eniac.data.PulseInteractor)
     */
    public boolean canReceiveDigit(long time, PulseInteractor source) {
        return false;
    }

    /**
     * @param time
     * @param value
     * @param source
     * @see eniac.data.PulseInteractor#receiveDigit(long, int,
     *      eniac.data.PulseInteractor)
     */
    public void receiveDigit(long time, long value, PulseInteractor source) {
        // nop
    }

    /**
     * @param time
     * @param value
     * @param source
     * @see eniac.data.PulseInteractor#sendDigit(long, int,
     *      eniac.data.PulseInteractor)
     */
    public void sendDigit(long time, long value, PulseInteractor source) {
        // nop
    }
}