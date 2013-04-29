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
 * Created on 16.03.2004
 */
package eniac.data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Observable;
import java.util.Observer;

import eniac.data.model.Connector;
import eniac.data.model.EData;
import eniac.data.model.parent.Configuration;
import eniac.data.type.EType;
import eniac.data.type.ProtoTypes;
import eniac.data.view.ConnectorPanel;
import eniac.data.view.parent.ConfigPanel;
import eniac.io.Tags;
import eniac.simulation.EEvent;
import eniac.simulation.EEventListener;
import eniac.skin.Descriptor;
import eniac.util.Status;
import eniac.window.EFrame;
import eniac.window.OVWindow;

/**
 * @author zoppke
 */
public class Cable extends Observable implements Observer, EEventListener {

    // connectorPanels
    private ConnectorPanel _cop1;

    private ConnectorPanel _cop2;

    private Point _dragPoint = null;

    private boolean _pulseTransmittion = false;

    //============================= lifecycle //===============================

    public Cable(ConnectorPanel cop) {
        _cop1 = cop;
        _cop1.getData().addObserver(this);
        ((Connector) _cop1.getData()).setPlugged(true);
    }

    //=============================== methods //===============================

    public boolean isComplete() {
        return _cop1 != null && _cop2 != null;
    }

    public boolean isDragging() {
        return _dragPoint != null;
    }

    public void setDragPoint(Point p) {

        // adjust dragpoint
        _dragPoint = p;

        // notify observing configPanel
        setChanged();
        notifyObservers(EData.REPAINT);
        //TODO: find finer way of repainting.
        // Like this the whole configpanel will be updated.
    }

    public void paintOnBufferedImage(Graphics g, float zoom, int lod) {

        // check, if we can paint this cable
        if (!isComplete() && !isDragging()) {
            return;
        }
        // get points and paint
        Point p1 = getBufferedPaintPoint(_cop1);
        Point p2 = getBufferedPaintPoint(_cop2);
        paint(g, zoom, lod, p1, p2);
    }

    private Point getBufferedPaintPoint(ConnectorPanel cop) {
        if (cop == null) {
            Dimension ovSize = OVWindow.getInstance().getOVPanel().getSize();
            ConfigPanel cp = EFrame.getInstance().getConfigPanel();
            int x = _dragPoint.x * ovSize.width / cp.getWidth();
            int y = _dragPoint.y * ovSize.height / cp.getHeight();
            return new Point(x, y);
        }
        return cop.getBufferedPaintPoint();
    }

    public void paintOnConfigPanel(Graphics g, float zoom, int lod) {

        // check, if we can paint this cable
        if (!isComplete() && !isDragging()) {
            return;
        }
        // get points and paint
        Point p1 = getConfigPaintPoint(_cop1);
        Point p2 = getConfigPaintPoint(_cop2);
        paint(g, zoom, lod, p1, p2);
    }

    private Point getConfigPaintPoint(ConnectorPanel cop) {
        if (cop == null) {
            return _dragPoint;
        }
        Point p = cop.getLocationOnConfigPanel();
        p.x += cop.getWidth() >> 1;
        p.y += cop.getHeight() >> 1;
        return p;
    }

    private void paintImmediately() {

        // paint on configpanel
        ConfigPanel cp = EFrame.getInstance().getConfigPanel();
        float zoom = ConfigPanel.heightToPercentage();
        paintOnConfigPanel(cp.getGraphics(), zoom, cp.getLod());

        // notify observers to cause a repaint of configpanel and ovPanel.
        setChanged();
        notifyObservers(EData.REPAINT);
    }

    private void paint(Graphics g, float zoom, int lod, Point p1, Point p2) {

        // get descriptor
        EType type = _cop1.getData().getType();
        Descriptor descriptor = type.getDescriptor(lod);

        // if descriptor is null, then return.
        if (descriptor == null) {
            return;
        }

        // set color for drawing
        if (_pulseTransmittion && (Boolean)Status.get("highlight_pulse")) {
            g.setColor((Color) descriptor.get(Tags.CABLE_COLOR_HIGHLIGHT));
        } else {
            g.setColor((Color) descriptor.get(Tags.CABLE_COLOR));
        }

        // compute helper variables
        float pixels = ((Integer) descriptor.get(Tags.CABLE_PIXELS))
                .floatValue()
                * zoom;
        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float factor1 = pixels / distance;
        float factor2 = pixels / distance;
        int xx1 = (int) (dy * factor1) >> 1;
        int yy1 = (int) (dx * factor1) >> 1;
        int xx2 = (int) (dy * factor2) >> 1;
        int yy2 = (int) (dx * factor2) >> 1;

        // if polygon is too small, draw a line
        if (xx1 == 0 && yy1 == 0) {
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        } else {

            // init polygon points and draw polygon
            int[] px = new int[4];
            int[] py = new int[4];
            px[0] = p1.x + xx1;
            py[0] = p1.y - yy1;
            px[1] = p1.x - xx2;
            py[1] = p1.y + yy2;
            px[2] = p2.x - xx1;
            py[2] = p2.y + yy1;
            px[3] = p2.x + xx2;
            py[3] = p2.y - yy2;
            g.fillPolygon(px, py, 4);
        }
    }

    public boolean containsCop(ConnectorPanel cop) {
        return _cop1 == cop || _cop2 == cop;
    }

    public boolean containsCon(Connector con) {
        return (_cop1 != null && _cop1.getData() == con)
                || (_cop2 != null && _cop2.getData() == con);
    }

    public void removeCop(ConnectorPanel cop) {
        disposeCop(cop);
        if (_cop1 == cop) {
            _cop1 = _cop2;
            _cop2 = null;
        } else if (_cop2 == cop) {
            _cop2 = null;
        } else {
            return;
            //TODO: log, that cable doesn't contain cop
        }
        if (_cop1 == null) {
            //maybe dispose cable
        } else {
            ((Connector) _cop1.getData()).setPartnerID(-1);
        }
    }

    public void dispose() {
        if (_cop1 != null) {
            disposeCop(_cop1);
            _cop1 = null;
        }
        if (_cop2 != null) {
            disposeCop(_cop2);
            _cop2 = null;
        }
        _dragPoint = null;
        deleteObservers();
    }

    private void disposeCop(ConnectorPanel cop) {
        Connector con = (Connector) cop.getData();
        con.setPlugged(false);
        con.setPartnerID(-1);
        con.deleteObserver(this);
    }

    public void addCop(ConnectorPanel cop) {

        // set cop and add observer
        if (_cop1 == null) {
            _cop1 = cop;
        } else if (_cop2 == null) {
            _cop2 = cop;
        } else {
            //TODO: log that cable already was complete
            return;
        }
        cop.getData().addObserver(this);

        // if cable is completed just now, make its cops plugged partners
        if (isComplete()) {
            Connector con1 = (Connector) _cop1.getData();
            Connector con2 = (Connector) _cop2.getData();
            con1.setPartnerID(con2.getID());
            con2.setPartnerID(con1.getID());
            con1.setPlugged(true);
            con2.setPlugged(true);

            // set dragpoint to null
            _dragPoint = null;

            // notify observers
            //TODO: call observers from CableManager
            setChanged();
            notifyObservers(EData.REPAINT);
        }
    }

    public static boolean canConnect(ConnectorPanel cop1, ConnectorPanel cop2) {

        // get data types
        EType type1 = cop1.getData().getType();
        EType type2 = cop2.getData().getType();

        // check for program connectors
        if (type1 == ProtoTypes.PROGRAM_CONNECTOR
                && type2 == ProtoTypes.PROGRAM_CONNECTOR) {
            return true;
        }
        // check for digitConnectors
        if ((type1 == ProtoTypes.DIGIT_CONNECTOR || type1 == ProtoTypes.DIGIT_CONNECTOR_CROSS)
                && (type2 == ProtoTypes.DIGIT_CONNECTOR || type2 == ProtoTypes.DIGIT_CONNECTOR_CROSS)) {
            return true;
        }
        // check for (unit-)interconnectors
        if (type1 == ProtoTypes.INTER_CONNECTOR
                && type2 == ProtoTypes.INTER_CONNECTOR) {
            return true;
        }
        // otherwise return false.
        return false;
    }

    /**
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        if (arg == Connector.CABLE_TRANSMITTION) {

            // set pulse transmittion flag for painting
            _pulseTransmittion = true;

            // set alarm clock for downlightning
            long time = Status.getLong("simulation_time");
            Configuration config = (Configuration) Status
                    .get("configuration");
            config.getCyclingLights().setAlarmClock(time, this);

            // if we are highlightning, repaint.
            if ((Boolean)Status.get("highlight_pulse")) {
                paintImmediately();
            }
        }
    }

    /**
     * @param e
     * @see eniac.simulation.EEventListener#process(eniac.simulation.EEvent)
     */
    public void process(EEvent e) {

        // check, if this cable hasn't removed
        if (!isDragging() && !isComplete()) {
            return;
        }
        // check if this cable is addressed correctly
        if (e.type == EEvent.ALARM && e.listener == this) {
            _pulseTransmittion = false;
            paintImmediately();
        }
    }
}
