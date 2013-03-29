/*
 * Created on 29.05.2004
 */
package eniac.data.model.parent;

import java.util.Observable;
import java.util.Observer;

import eniac.data.model.EData;
import eniac.data.model.sw.Switch;
import eniac.data.model.unit.ConstantTransmitter2;
import eniac.data.type.EType;
import eniac.data.type.ProtoTypes;

/**
 * @author zoppke
 */
public class Constant2Lights extends ParentData implements Observer {

    public void init() {
        super.init();
        ConstantTransmitter2 unit = getTransmitter();

        // observe sign switchs
        unit.getGarten().getKind(ProtoTypes.CONSTANT_SIGN_TOGGLE_JL, 0)
                .addObserver(this);
        unit.getGarten().getKind(ProtoTypes.CONSTANT_SIGN_TOGGLE_JR, 0)
                .addObserver(this);
        unit.getGarten().getKind(ProtoTypes.CONSTANT_SIGN_TOGGLE_KL, 0)
                .addObserver(this);
        unit.getGarten().getKind(ProtoTypes.CONSTANT_SIGN_TOGGLE_KR, 0)
                .addObserver(this);

        // observe constant switchs
        EData[] switchs = unit.getGarten()
                .getKinder(ProtoTypes.CONSTANT_SWITCH);
        for (int i = 0; i < switchs.length; ++i) {
            switchs[i].addObserver(this);
        }
        EData heaters = unit.getGarten().getKind(ProtoTypes.HEATERS, 0);
        heaters.addObserver(this);
    }

    public boolean hasPower() {
        return getTransmitter().hasPower();
    }

    private ConstantTransmitter2 getTransmitter() {
        return (ConstantTransmitter2) getConfiguration().getGarten().getKind(
                ProtoTypes.CONSTANT_TRANSMITTER_2_UNIT, 0);
    }

    /**
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        EData d = (EData) o;
        EType t = d.getType();
        if (t == ProtoTypes.CONSTANT_SIGN_TOGGLE_JL) {
            Switch sign = (Switch) getGarten().getKind(
                    ProtoTypes.CONSTANT_BLINKEN_SIGN, 0);
            sign.setValue(((Switch) d).getValue());
        } else if (t == ProtoTypes.CONSTANT_SIGN_TOGGLE_JR) {
            Switch sign = (Switch) getGarten().getKind(
                    ProtoTypes.CONSTANT_BLINKEN_SIGN, 1);
            sign.setValue(((Switch) d).getValue());
        } else if (t == ProtoTypes.CONSTANT_SIGN_TOGGLE_KL) {
            Switch sign = (Switch) getGarten().getKind(
                    ProtoTypes.CONSTANT_BLINKEN_SIGN, 2);
            sign.setValue(((Switch) d).getValue());
        } else if (t == ProtoTypes.CONSTANT_SIGN_TOGGLE_KR) {
            Switch sign = (Switch) getGarten().getKind(
                    ProtoTypes.CONSTANT_BLINKEN_SIGN, 3);
            sign.setValue(((Switch) d).getValue());
        } else if (t == ProtoTypes.CONSTANT_SWITCH) {
            Switch sw = (Switch) getGarten().getKind(
                    ProtoTypes.CONSTANT_BLINKEN_CIPHER, d.getIndex());
            sw.setValue(((Switch) d).getValue());
        } else if (t == ProtoTypes.HEATERS) {
            setChanged();
            notifyObservers(EData.REPAINT);
        }
        setChanged();
        notifyObservers(ConstantTransmittionLights.PAINT_LIGHTS);
    }
}