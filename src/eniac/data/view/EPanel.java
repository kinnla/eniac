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
 * Created on 06.12.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputListener;

import eniac.Manager;
import eniac.data.control.BasicControler;
import eniac.data.control.Controler;
import eniac.data.model.EData;
import eniac.data.type.EType;
import eniac.data.type.ParentGrid;
import eniac.data.view.parent.ConfigPanel;
import eniac.lang.Dictionary;
import eniac.property.Property;
import eniac.property.PropertyPanel;
import eniac.skin.Descriptor;
import eniac.util.EProperties;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class EPanel extends JPanel implements Observer, MouseInputListener {

    // reference to dataObject
    protected EData _data;

    // =========================== lifecycle
    // ====================================

    public EPanel() {
        super(null);
    }

    public void init() {
        // System.out.println("init " + _data.getType());
        // init listeners
        addMouseListener(this);
        addMouseMotionListener(this);
        _data.addObserver(this);

        // find a non-empty name
        String name;
        EPanel panel = this;
        do {
            name = panel.getData().getName();
            panel = (EPanel) getParent();
        } while (name.equals("")); //$NON-NLS-1$

        // set name as tooltip
        setToolTipText(name);
    }

    /**
     * @see eniac.data.IDataPanel#dispose() TODO: how to dispose? dataObject
     *      calls its tree, dataPanel calls its tree?
     */
    public void dispose() {
        removeMouseListener(this);
        removeMouseMotionListener(this);
        _data.deleteObserver(this);
    }

    // =============================== methods //===============================

    public void setData(EData data) {
        _data = data;
    }

    public EData getData() {
        return _data;
    }

    public JComponent createPropertiesPanel() {
        return new PropertyPanel(_data.getProperties());
    }

    public List<Action> getActions() {
        List<Action> l = new LinkedList<>();
        l.add(new ShowProperties());
        return l;
    }

    public void paintAsIcon(Graphics g, int x, int y, int w, int h, int lod) {
        paintComponent(g, x, y, w, h, lod);
    }

    /**
     * Paints this dataPanel.
     */
    public void paintComponent(Graphics g) {
        // maybe call super-paint: super.paintComponent(g);
        paintComponent(g, 0, 0, getWidth(), getHeight(), getLod());
    }

    protected void paintComponent(Graphics g, int x, int y, int width,
            int height, int lod) {

        // get descriptor. If no descriptor, just return.
        Descriptor d = getDescriptor(lod);
        if (d == null) {
            return;
        }
        // draw background
        drawBackground(g, x, y, width, height, lod, d);

        // paint bgimage, if defined
        Image bgimage = (Image) d.get(Descriptor.Key.BACK_IMAGE);
        if (bgimage != null) {
            g.drawImage(bgimage, x, y, width, height, this);
        }
    }

    protected void drawBackground(Graphics g, int x, int y, int width,
            int height, int lod, Descriptor d) {

        // get bgcolor. If background color is not defined, take ancestor one's.
        Color color = (Color) d.get(Descriptor.Key.COLOR);
        EPanel p = this;
        while (color == null) {

            // get parent's background color
            Component c = p.getParent();
            if (c == null) {
                // sometimes we have no parent yet
                color = StringConverter.toColor(EProperties.getInstance().getProperty(
                        "BACKGROUND_COLOR"));
            } else {
                p = (EPanel) c;
                d = p.getDescriptor(lod);
                // note: the parent's descriptor is not null, because ours is
                // not.
                color = (Color) d.get(Descriptor.Key.COLOR);
            }
        }
        // draw background
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public Descriptor getDescriptor(int lod) {
        return _data.getType().getDescriptor(lod);
    }

    public int getLod() {
        return ((EPanel) getParent()).getLod();
    }

    public void update(Observable o, Object arg) {
        assert arg != null;
        // if (Simulator.getInstance().isSimulationThread()) {
        // paintComponent(getGraphics());
        // } else {
        // repaint();
        // }
        if (arg == EData.REPAINT && getDescriptor(getLod()) != null) {
            repaint();
        } else if (arg == EData.PAINT_IMMEDIATELY) {
            paintComponent(getGraphics());
        }
    }

    public Point getLocationOnConfigPanel() {
        Component c = this;
        Point p = new Point();
        while (!(c instanceof ConfigPanel)) {
            p.x += c.getX();
            p.y += c.getY();
            c = c.getParent();
        }
        return p;
    }

    public boolean isEnabled() {
        Descriptor d = _data.getType().getDescriptor(getLod());
        return d != null;
    }

    public Rectangle computeBound(ParentGrid pg, int lod) {

        EType type = _data.getType();
        int[] gridNums = _data.getGridNumbers();

        // helper variables
        int x1 = gridNums[0];
        int y1 = gridNums[1];
        int x2 = gridNums[2];
        int y2 = gridNums[3];

        // compute size of the grid-square
        int newWidth = pg.xValues[x2] - pg.xValues[x1];
        int newHeight = pg.yValues[y2] - pg.yValues[y1];
        int newX = pg.xValues[x1];
        int newY = pg.yValues[y1];

        // determine size of the panel that should be centered
        // check, if the whole grid-rectangle should be filled.
        Descriptor descriptor = type.getDescriptor(lod);
        // if (descriptor == null) {
        // System.out.println(type);
        // }
        Descriptor.Fill fill = descriptor.getFill();
        if (fill == Descriptor.Fill.HORIZONTAL || fill == Descriptor.Fill.NONE) {
            int h = (int) (descriptor.getHeight() * pg.zoomY);
            newY = newY + ((newHeight - h) >> 1);
            newHeight = h;
        }
        if (fill == Descriptor.Fill.VERTICAL || fill == Descriptor.Fill.NONE) {
            int w = (int) (descriptor.getWidth() * pg.zoomX);
            newX = newX + ((newWidth - w) >> 1);
            newWidth = w;
        }
        return new Rectangle(newX, newY, newWidth, newHeight);
    }

    // ======================= MouseListener methods
    // ============================

    public void mouseReleased(MouseEvent e) {
        if (isEnabled() && e.getButton() != MouseEvent.BUTTON3) {
            getController().mreleased(e);
        }
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {

            // if right button, create pop-up menu
            JPopupMenu menu = new JPopupMenu();
            for (Action a : getActions()) {
                menu.add(a);
            }
            menu.show(this, e.getX(), e.getY());
        } else {
            if (isEnabled()) {
                getController().mpressed(e);
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (isEnabled()) {
            getController().mdragged(e);
        }
    }

    public void mouseMoved(MouseEvent e) {
        // empty
    }

    public void mouseClicked(MouseEvent e) {
        // empty
    }

    public void mouseEntered(MouseEvent e) {
        // empty
    }

    public void mouseExited(MouseEvent e) {
        // empty
    }

    protected Controler getController() {
        // get actionator by descriptor
        Descriptor descriptor = getDescriptor(getLod());
        if (descriptor != null) {
            Object o = descriptor.get(Descriptor.Key.ACTIONATOR);
            if (o != null) {
                return (BasicControler) o;
            }
        }
        return Controler.NONE;
    }

    public class ShowProperties extends AbstractAction {

        public ShowProperties() {
            putValue(Action.NAME, Dictionary.PROPERTIES + "..."); //$NON-NLS-1$
        }

        public void actionPerformed(ActionEvent e) {

            // create and init new dataPropertyPanel
            List<Property> properties = EPanel.this.getData().getProperties();
            PropertyPanel panel = new PropertyPanel(properties);
            panel.init();

            // show dialog
            Manager.getInstance().makeDialog(panel, Dictionary.PROPERTIES.getText());

            // if input result is null, then the dialog was canceled.
            // otherwise set new configuration
            if (panel.isCommitChanges()) {
                EPanel.this.getData().setProperties(properties);
            }
        }
    }

}
