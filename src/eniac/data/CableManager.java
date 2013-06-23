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
 * Created on 01.05.2004 
 */

package eniac.data;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import eniac.Manager;
import eniac.data.control.Controler;
import eniac.data.model.Connector;
import eniac.data.model.EData;
import eniac.data.type.EType;
import eniac.data.view.ConnectorPanel;
import eniac.data.view.EPanel;
import eniac.data.view.parent.ConfigPanel;
import eniac.util.Status;
import eniac.window.EFrame;
import eniac.window.OVWindow;

/**
 * @author zoppke
 */
public class CableManager implements Observer, Controler {

	// list of cables.
	private List<Cable> _cables = new LinkedList<>();

	// temporary connectorPanel, when we are dragging on top of it.
	private ConnectorPanel _tempCop;

	// temporary cable
	private Cable _tempCable;

	// flag indicating whether we are potentially creating or removing a loadbox
	private boolean _loadBox;

	// ================================ lifecycle
	// ===============================

	public CableManager() {
		// empty constructor
	}

	public void init() {
		// iterate on all cables and remove the incomplete ones
		Cable[] cables = new Cable[_cables.size()];
		_cables.toArray(cables);
		for (int i = 0; i < cables.length; ++i) {
			if (!cables[i].isComplete()) {
				removeCable(cables[i]);
			}
		}
	}

	// ================================= methods
	// ================================

	public void paintOnConfigPanel(Graphics g, int lod) {

		// get zoom
		float zoom = ConfigPanel.heightToPercentage();

		// iterate on connectorPanels
		for (Cable cable : _cables) {
			cable.paintOnConfigPanel(g, zoom, lod);
		}
	}

	public void paintOnBufferedImage(Graphics g, int lod) {
		// paint cables
		int ovHeight = OVWindow.getInstance().getOVPanel().getHeight();
		int configHeight = (int) Status.ZOOMED_HEIGHT.getValue();
		float zoom = (float) ovHeight / (float) configHeight;
		for (Cable cable : _cables) {
			cable.paintOnBufferedImage(g, zoom, lod);
		}
	}

	public void addCop(ConnectorPanel cop) {
		// if we already have a cable containing this cop,
		// then is nothing to do.
		Connector con = (Connector) cop.getData();
		Cable cable = findCable(con);
		if (cable != null) {
			return;
		}
		// otherwise check cop's partner, if there is any.
		int id = con.getPartner();
		if (id >= 0) {
			IDManager idManager = con.getConfiguration().getIDManager();
			Connector partner = (Connector) idManager.get(id);
			cable = findCable(partner);
			if (cable == null) {
				// no cable for the partner. So create new cable.
				addCable(new Cable(cop));
			}
			else {
				// we found a cable containing the partner.
				// Check, if we are those cable's partner, too.
				if (partner.getPartner() == con.getID()) {
					cable.addCop(cop);
				}
			}
		}
	}

	private void addCable(Cable cable) {
		_cables.add(cable);
		cable.addObserver(this);
	}

	private void removeCable(Cable cable) {
		cable.dispose();
		_cables.remove(cable);
		update(cable, EData.REPAINT);
	}

	private Cable findCable(Connector con) {
		for (Cable cable : _cables) {
			if (cable.containsCon(con)) {
				return cable;
			}
		}
		return null;
	}

	private static Point computeDragPoint(MouseEvent e) {
		Point p = ((EPanel) e.getComponent()).getLocationOnConfigPanel();
		p.x += e.getX();
		p.y += e.getY();
		return p;
	}

	// ============================ Observer methods
	// ============================

	/**
	 * @param o
	 * @param arg
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		assert arg != null;
		if (arg == EData.REPAINT && Status.LIFECYCLE.getValue() == Manager.LifeCycle.STATE_RUNNING) {

			// TODO: Find a better way to repaint
			EFrame.getInstance().getConfigPanel().repaint();
			OVWindow.getInstance().getOVPanel().repaint();
		}
	}

	// =========================== Actionator methods
	// ===========================

	public void mpressed(MouseEvent e) {
		// only track when the left button clicked
		if (e.getButton() == MouseEvent.BUTTON1) {
			ConnectorPanel cop = (ConnectorPanel) e.getComponent();
			Connector con = (Connector) cop.getData();
			if (con.isPlugged()) {

				// in case of a loadbox just remove loadbox
				if (con.getPartner() == con.getID()) {
					con.setPlugged(false);
					con.setPartnerID(-1);
					_loadBox = true;
					return;
				}
				// otherwise find cable and make it dragging.
				_tempCable = findCable(con);
				_tempCable.removeCop(cop);
				_tempCable.setDragPoint(computeDragPoint(e));
				_tempCop = cop;
			}
			else {
				_tempCable = new Cable(cop);
				_tempCable.setDragPoint(computeDragPoint(e));
				addCable(_tempCable);

				// if this is an interconnector, set component as tempCop
				if (con.getType() == EType.INTER_CONNECTOR) {
					_tempCop = cop;
					_loadBox = true;
				}
			}
		}
	}

	public void mreleased(MouseEvent e) {
		// only track when the left button clicked
		if (e.getButton() == MouseEvent.BUTTON1) {

			// determine which component was hit
			if (_tempCop == null) {
				// we did not hit a connectorPanel
				// or the connectorPanel already had a cable.
				// Remove cable and forget temporary reference.
				if (_tempCable != null) {
					removeCable(_tempCable);
					_tempCable = null;
				}
			}
			else {
				// we hit a connectorPanel. Check, if we should set a loadBox.
				Connector con = (Connector) _tempCop.getData();
				ConnectorPanel ecop = (ConnectorPanel) e.getSource();
				if (_loadBox && ecop == _tempCop) {
					// set con as self-partner and remove cable
					removeCable(_tempCable);
					con.setPartnerID(con.getID());
					con.setPlugged(true);
				}
				else {
					// Set cop to cable and keep cable
					_tempCable.addCop(_tempCop);
				}
				// reset temporary references.
				_tempCable = null;
				_tempCop = null;
			}
			_loadBox = false;
		}
	}

	public void mdragged(MouseEvent e) {
		// if tempcable is null, we are not dragging with left button down.
		if (_tempCable == null) {
			return;
		}
		// compute and adjust cables dragpoint
		Point dragPoint = computeDragPoint(e);
		_tempCable.setDragPoint(dragPoint);

		// determine which component we are dragging on
		ConfigPanel configPanel = EFrame.getInstance().getConfigPanel();
		Component c = configPanel.findComponentAt(dragPoint);

		// if component is our temporary cop, then nothing to do
		if (_tempCop == c) {
			return;
		}
		// otherwise dispose temporary cop, if any
		if (_tempCop != null) {
			// if we have a loadbox, we should keep the tempcop plugged.
			if (!_loadBox || _tempCop != e.getSource()) {
				((Connector) _tempCop.getData()).setPlugged(false);
			}
			_tempCop = null;
		}
		// check, if we hit a connectorpanel.
		if (c instanceof ConnectorPanel) {

			// check if cop is unplugged and a valid partner
			ConnectorPanel newCop = (ConnectorPanel) c;
			Connector newCon = (Connector) newCop.getData();
			ConnectorPanel oldCop = (ConnectorPanel) e.getComponent();
			if (!newCon.isPlugged() && Cable.canConnect(newCop, oldCop)) {

				// We got possible connection.
				_tempCop = newCop;
				newCon.setPlugged(true);
			}
		}
	}
}
