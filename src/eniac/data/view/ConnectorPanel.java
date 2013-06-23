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
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import eniac.data.CableManager;
import eniac.data.control.Controler;
import eniac.data.model.Connector;
import eniac.skin.Descriptor;
import eniac.util.Status;
import eniac.util.StatusMap;
import eniac.window.EFrame;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ConnectorPanel extends EPanel {

	// last point that we have been painted on the buffered image
	private Point _bufferedPaintPoint = new Point();

	// ============================= lifecycle //===============================

	public ConnectorPanel() {
		// empty
	}

	public void init() {
		super.init();
		// check, if the connector has a partner.
		int partnerID = ((Connector) _data).getPartner();
		if (partnerID == _data.getID()) {
			// if we are partner of ourself, then we have a loadbox
		}
		else if (partnerID >= 0) {

			// if otherwise positive, register at cableManager
			CableManager cm = EFrame.getInstance().getConfigPanel().getCableManager();
			cm.addCop(this);
		}
	}

	// ============================== methods //================================

	protected void paintComponent(Graphics g, int x, int y, int width, int height, int lod) {

		// get descriptor. If no descriptor, just return.
		Descriptor d = getDescriptor(lod);
		if (d == null) {
			return;
		}
		// draw background
		drawBackground(g, x, y, width, height, lod, d);

		// get Image according to connector's state
		Connector con = (Connector) _data;
		boolean loadbox = con.getID() == con.getPartner();
		boolean plugged = con.isPlugged();
		Descriptor.Key key;
		if (loadbox) {
			key = Descriptor.Key.LOADBOX;
		}
		else if (plugged) {
			key = Descriptor.Key.PLUGGED;
		}
		else {
			key = Descriptor.Key.UNPLUGGED;
		}
		Image img = (Image) d.get(key);

		// init helper variables
		long lastPulse = con.getLastPulse();
		long simTime = StatusMap.getLong(Status.SIMULATION_TIME);
		boolean highlightPulse = (Boolean) StatusMap.get(Status.HIGHLIGHT_PULSE);

		// if highlight and we have a current pulse, mark connector
		if (highlightPulse && lastPulse == simTime) {
			g.setXORMode(Color.RED);
		}
		// draw image
		g.drawImage(img, x, y, width, height, this);
		g.setPaintMode();

		// if we are painting on the bufferedImage, save our mid point
		// in the bufferedImage's coordinates.
		if (x != 0 || y != 0) {
			_bufferedPaintPoint.x = x + (width >> 1);
			_bufferedPaintPoint.y = y + (height >> 1);
		}
	}

	public Point getBufferedPaintPoint() {
		return _bufferedPaintPoint;
	}

	protected Controler getController() {
		return EFrame.getInstance().getConfigPanel().getCableManager();
	}
}
