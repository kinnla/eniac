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
package eniac.window;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eniac.data.model.parent.Configuration;
import eniac.data.type.ProtoTypes;
import eniac.data.view.parent.ConfigPanel;
import eniac.io.Tags;
import eniac.skin.Descriptor;
import eniac.skin.Skin;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class OVPanel extends JPanel implements ChangeListener, Observer,
        PropertyChangeListener {

    // rectangle to store the bounds of the xored area
    private Rectangle _xorBounds = new Rectangle();

    // bufferedImage on which we draw the configuration.
    // if we are just moving the xored image, we can unxor at the old position,
    // than xor at the new position without refreshing the bufferedimage.
    private BufferedImage _bufferImage;

    // flag indicating, whether the bufferedImage should be refreshed
    private boolean _dirty = true;
    
    /*
     * ============================== lifecycle ================================
     */

    public OVPanel() {
        super(null);
    }

    public void init() {
        // System.out.println("ovinit");
        // we don't need a buffer, because our image is already buffered.
        setDoubleBuffered(false);

        // add ChangeListener to the configurationsPanels viewPort
        EFrame.getInstance().addChangeListener(this);
        
        // enable mouse and mouse modtion events
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);

        // observe configuration
        Configuration config = (Configuration) Status.get("configuration");
        if (config != null) {
            config.addObserverToTree(this);
        }

        // add as propertychanglistener to status for get notified
        // when skin changes or configuration changes
        Status.getInstance().addListener(this);

        // add as componentlistener to get notified when we are resized.
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                update((Configuration) Status.get("configuration"), null);
            }
        });
    }

    public void dispose() {
        Status.getInstance().removeListener(this);
        // TODO: do we need to unregister as observer?
    }

    /*
     * ============================= methods ===================================
     */
    
    @Override
    protected void processMouseEvent(MouseEvent e) {
    		if (e.getID() == MouseEvent.MOUSE_PRESSED) {
    			centerConfigurationPanel(e.getX(), e.getY());
    		}
    }
    
    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
			centerConfigurationPanel(e.getX(), e.getY());
		}
    }
    
    // centers the configuration panel 
    private void centerConfigurationPanel(int x, int y) {
        ConfigPanel configPanel = EFrame.getInstance().getConfigPanel();
        float dx = (float) configPanel.getWidth() / (float) getWidth();
        float dy = (float) configPanel.getHeight() / (float) getHeight();
        
        // determine viewport, init helpers
		JViewport viewPort = ((JViewport) configPanel.getParent());
		Point p = viewPort.getViewPosition();
		
		// compute x-coordinate
		if (viewPort.getWidth() <= configPanel.getWidth()) {
			p.x = ((int) (x * dx)) - viewPort.getWidth() / 2;
			if (p.x < 0) {
				p.x = 0;
			} else if (p.x > configPanel.getWidth() - viewPort.getWidth()) {
				p.x = configPanel.getWidth() - viewPort.getWidth();
			}
		}
		
		// compute y-coordinate
		if (viewPort.getHeight() <= configPanel.getHeight()) {
			p.y = ((int) (y * dy)) - viewPort.getHeight() / 2;
			if (p.y < 0) {
				p.y = 0;
			} else if (p.y > configPanel.getHeight() - viewPort.getHeight()) {
				p.y = configPanel.getHeight() - viewPort.getHeight();
			}
		}
		// set view position
		viewPort.setViewPosition(p);
    }

    /**
     * Returns the preferred Size for this ovPanel. This method will be called
     * during initializing the related ovWindow.
     */
    public Dimension getPreferredSize() {

        // multiply the DV preferred size by the initial ov zoom
        int height = StringConverter.toInt(EProperties.getInstance().getProperty(
                "INITIAL_OV_HEIGHT"));
        Skin skin = (Skin) Status.get("skin");
        int lod = skin.getLodByHeight(height);
        Configuration config = (Configuration) Status.get("configuration");

        // if no configuration, return empty dimension
        if (config == null) {
            return new Dimension(0, 0);
        }
        Descriptor descriptor = config.getType().getDescriptor(lod);

        // if no descriptor, return empty dimension
        if (descriptor == null) {
            return new Dimension(0, 0);
        }

        // compute wanted width by rule of three and return
        int width = height * descriptor.getWidth() / descriptor.getHeight();
        return new Dimension(width, height);
    }

    /**
     * Paints this ovPanel.
     */
    public void paintComponent(Graphics g) {

        // if configuration is null, just paint background
        if (Status.get("configuration") == null) {
            g.setColor(StringConverter.toColor(EProperties.getInstance().getProperty(
                    "BACKGROUND_COLOR")));
            g.fillRect(0, 0, getWidth(), getHeight());
            return;
        }

        // compute zoom and appropriate lod
        Skin skin = (Skin) Status.get("skin");
        int lod = skin.getLodByHeight(getHeight());

        // check that we have a valid lod
        if (lod < 0) {
            return;
        }

        // check Image
        if (_dirty) {
            // create new buffered image
            _bufferImage = (BufferedImage) createImage(getWidth(), getHeight());

            // determine configurationPanel and paint as icon
            ConfigPanel configPanel = EFrame.getInstance().getConfigPanel();
            configPanel.paintAsIcon(_bufferImage.getGraphics(), 0, 0,
                    getWidth(), getHeight(), lod);
        } else {

            // image is not dirty, so unxor the rectangle
            paintXORedRectangle(_bufferImage.getGraphics(), lod);
        }

        // compute and paint the xored rectangle
        // for highlighting the visible area
        updateXORedRectangle();
        paintXORedRectangle(_bufferImage.getGraphics(), lod);

        // paint buffer image to screen
        g.drawImage(_bufferImage, 0, 0, getWidth(), getHeight(), this);

        // reset dirty flag
        _dirty = false;
    }

    /**
     * ChangeListener method. This is called, when the user scrolled the
     * detailview frame
     * 
     * @param e
     */
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    private void updateXORedRectangle() {
        ConfigPanel configPanel = EFrame.getInstance().getConfigPanel();
        Rectangle r = configPanel.getVisibleRect();
        float dx = (float) getWidth() / (float) configPanel.getWidth();
        float dy = (float) getHeight() / (float) configPanel.getHeight();
        _xorBounds.setBounds((int) (r.x * dx), (int) (r.y * dy),
                (int) (r.width * dx), (int) (r.height * dy));
    }

    private void paintXORedRectangle(Graphics g, int lod) {
        // if image covers the whole dataPanel, we don't need to draw an image
        // note: +1 is nice, because for some reason the xorbounds are smaller
        // than the panel's width, even when this should not be.
        if (_xorBounds.width + 1 >= getWidth()
                && _xorBounds.height + 1 >= getHeight()) {
            return;
        }

        // get xor image and color
        Descriptor d = ProtoTypes.XOR_IMAGE.getDescriptor(lod);
        Image img = (Image) d.get(Tags.BACK_IMAGE);
        Color color = (Color) d.get(Tags.COLOR);

        // set xore mode and paint xor image
        g.setXORMode(color);
        g.drawImage(img, _xorBounds.x, _xorBounds.y, _xorBounds.width,
                _xorBounds.height, this);
        g.setPaintMode();
    }

    // //////////////////////////// listener stuff
    // //////////////////////////////

    /**
     * @param o
     * @param arg
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        // TODO: translate bounds of the calling observable to ovbounds
        // and do a partial repaint.
        _dirty = true;
        repaint();
    }

    /**
     * @param evt
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("skin")) {

            // new skin loaded. Repaint.
            update((Configuration) Status.get("configuration"), null);
        }
        // else if (evt.getPropertyName() == Status.CONFIGURATION) {
        //
        // // new Configuration. Repaint.
        // initAsObserver();
        // update((Configuration) evt.getNewValue(), null);
        // }
    }
}
