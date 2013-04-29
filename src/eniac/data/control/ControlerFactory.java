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
 * Created on 27.03.2004
 */
package eniac.data.control;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;

import eniac.data.view.sw.SwitchPanel;
import eniac.io.Tags;
import eniac.skin.Descriptor;

/**
 * @author zoppke
 */
public class ControlerFactory {

    private Controler[] _actionators;

    private String[] _keys;

    //============================== lifecycle
    // =================================

    public ControlerFactory() {

        // init actionators
        _actionators = new Controler[] { new Toggle(), new PushButton(),
                new Switch(), new OperationSwitch() };

        // init keys
        _keys = new String[_actionators.length];
        for (int i = 0; i < _keys.length; ++i) {
            String s = _actionators[i].getClass().getName();
            _keys[i] = s.substring(s.lastIndexOf('$') + 1);
        }
    }

    //============================= methods
    // ====================================

    public Controler get(String cls) {
        for (int i = 0; i < _actionators.length; ++i) {
            if (_keys[i].equals(cls)) {
                return _actionators[i];
            }
        }
        return null;
    }

    //=============================== Toggle
    // ===================================

    private class Toggle extends BasicControler {
        public void mpressed(MouseEvent e) {
            toggleValue(e);
        }
    }

    //============================ PushButton
    // ==================================

    private class PushButton extends BasicControler {
        public void mpressed(MouseEvent e) {
            setValue(e, 1);
        }

        public void mreleased(MouseEvent e) {
            setValue(e, 0);
        }

        public void mdragged(MouseEvent e) {
            // if dragged outside the component, take it as released.
            if (!isInside(e)) {
                setValue(e, 0);
            }
        }
    }

    //=============================== Switch
    // ===================================

    private class Switch extends BasicControler {

        private int _oldValue;

        public void mpressed(MouseEvent e) {

            // save old value and set new one
            _oldValue = getValue(e);
            setValueByPoint(e);
        }

        public void mreleased(MouseEvent e) {
            setValueByPoint(e);
        }

        public void mdragged(MouseEvent e) {
            setValueByPoint(e);
        }

        private void setValueByPoint(MouseEvent e) {

            // compute clickpoint in descriptor's bounds
            SwitchPanel sp = getSwitchPanel(e);
            Descriptor d = sp.getDescriptor(sp.getLod());
            double x = ((double) e.getX()) * d.getWidth() / sp.getWidth();
            double y = ((double) e.getY()) * d.getHeight() / sp.getHeight();

            // get shapes from descriptor
            Object[] shapes = (Object[]) d.get(Tags.AREAS);
            if (shapes == null) {
                shapes = (Object[]) d.get(Tags.RECTANGLE_ARRAY);
            }

            // search for a shape that containes the point.
            for (int i = 0; i < shapes.length; ++i) {
                if (((Shape) shapes[i]).contains(x, y)) {
                    // shape is found. set value and return.
                    setValue(e, i);
                    return;
                }
            }
            // cannot find value. Set old value.
            setValue(e, _oldValue);
        }
    }

    //=========================== OperationSwitch
    // ==============================

    private class OperationSwitch extends BasicControler {

        private Controler _controler = null;

        public void mpressed(MouseEvent e) {

            // compute clickpoint in descriptor's bounds
            SwitchPanel sp = getSwitchPanel(e);
            Descriptor d = sp.getDescriptor(sp.getLod());
            double x = ((double) e.getX()) * d.getWidth() / sp.getWidth();
            double y = ((double) e.getY()) * d.getHeight() / sp.getHeight();

            // get clearCorrect rectangle from descriptor
            Rectangle r = (Rectangle) d.get(Tags.RECTANGLE);

            // select controler according to inside or outside the rectangle
            if (r.contains(x, y)) {
                ((eniac.data.model.sw.SwitchAndFlag) sp.getData()).toggleFlag();
            } else {
                _controler = new Switch();
                _controler.mpressed(e);
            }
        }

        public void mreleased(MouseEvent e) {
            if (_controler != null) {
                _controler.mreleased(e);
                _controler = null;
            }
        }

        public void mdragged(MouseEvent e) {
            if (_controler != null) {
                _controler.mdragged(e);
            }
        }
    }
}
