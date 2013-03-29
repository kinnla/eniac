/*
 * Created on 02.04.2004
 */
package eniac.data;

/**
 * @author zoppke
 */
public interface PulseInteractor {
    public void receiveProgram(long time, PulseInteractor source);

    public void sendProgram(long time, PulseInteractor source);

    public void receiveDigit(long time, long value, PulseInteractor source);

    public void sendDigit(long time, long value, PulseInteractor source);

    public boolean canReceiveDigit(long time, PulseInteractor source);

    public boolean canReceiveProgram(long time, PulseInteractor source);
}