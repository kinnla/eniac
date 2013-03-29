/*
 * Created on 29.05.2004
 */
package eniac.data.model.unit;

import java.util.Observable;

import eniac.data.model.sw.Switch;
import eniac.data.type.ProtoTypes;

/**
 * @author zoppke
 */
public class ConstantTransmitter2 extends Unit {

    /**
     * @return @see eniac.data.model.unit.Unit#getHeaters()
     */
    public Switch getHeaters() {
        return (Switch) getGarten().getKind(ProtoTypes.HEATERS, 0);
    }

    /**
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub

    }

}