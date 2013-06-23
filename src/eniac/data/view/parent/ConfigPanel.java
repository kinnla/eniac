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
 * Created on 25.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.data.view.parent;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JViewport;
import javax.swing.Scrollable;

import eniac.data.CableManager;
import eniac.data.model.EData;
import eniac.data.type.EType;
import eniac.data.view.EPanel;
import eniac.skin.Descriptor;
import eniac.skin.Skin;
import eniac.util.EProperties;
import eniac.util.Status;
import eniac.util.StatusListener;
import eniac.util.StringConverter;

/**
 * @author zoppke
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ConfigPanel extends ParentPanel implements Scrollable, StatusListener {

	/*
	 * ============================== fields ===================================
	 */

	// cableManager tracking all cables
	private CableManager _cableManager = new CableManager();

	// current lod for detaied view
	private int _lod;

	// temporary Point for computation speed up
	private Point _p = new Point();

	/*
	 * =============================== lifecycle ===============================
	 */

	public void init() {

		// super call for initiating the guiGarten and adding children
		super.init();

		// init cables
		_cableManager.init();

		// add as propertychangelistener to status to receive simulation time
		// updates
		Status.HIGHLIGHT_PULSE.addListener(this);
		Status.ZOOMED_HEIGHT.addListener(this);
	}

	public void dispose() {
		super.dispose();
		Status.HIGHLIGHT_PULSE.removeListener(this);
		Status.ZOOMED_HEIGHT.removeListener(this);
		removeAll();
		_cableManager = null;
	}

	/*
	 * ============================== methods ==================================
	 */

	public int getLod() {
		return _lod;
	}

	public CableManager getCableManager() {
		return _cableManager;
	}

	/*
	 * =========================== gui methods =================================
	 */

	/**
	 * Computes the preferred size of this configPanel. The preferred width is
	 * always a multiple of the numberOfUnits. So every unit will have the same
	 * width.
	 */
	public Dimension getPreferredSize() {

		// get current configuration height
		int height = (int) Status.ZOOMED_HEIGHT.getValue();

		// set lod
		Skin skin = (Skin) Status.SKIN.getValue();
		_lod = skin.getLodByHeight(height);

		// get descriptor for this configuration
		EType type = _data.getType();
		int lod = skin.getLodByHeight(height);
		Descriptor descriptor = type.getDescriptor(lod);

		// if no descriptor, return a default dimension
		if (descriptor == null) {
			return new Dimension(0, 0);
		}

		// otherwise compute wanted width by the rule of three and return
		int width = height * descriptor.getWidth() / descriptor.getHeight();
		return new Dimension(width, height);
	}

	public void doLayout() {

		// compute bounds for all children
		Rectangle[] rectangles = getRectangles(_lod, getWidth(), getHeight());

		// set bounds for all of the children
		EPanel[] children = getChildren();
		for (int i = 0; i < children.length; ++i) {
			Rectangle r = rectangles[i];
			children[i].setBounds(r.x, r.y, r.width, r.height);
		}
	}

	public void paintAsIcon(Graphics g, int x, int y, int w, int h, int lod) {

		// paint background and children, then paint cables
		super.paintAsIcon(g, x, y, w, h, lod);
		_cableManager.paintOnBufferedImage(g, lod);
	}

	public void paintChildren(Graphics g) {

		// paint children by supercall, then paint cables
		super.paintChildren(g);
		_cableManager.paintOnConfigPanel(g, _lod);
	}

	/*
	 * ========================== scrollable methods ===========================
	 */

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 30;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		return 30;
	}

	/*
	 * ============================ event listening ============================
	 */

	@Override
	public void statusChanged(Status status, Object newValue) {
		if (status == Status.HIGHLIGHT_PULSE) {
			// highlight mode changed by user action.
			// Because this won't happen too often don't be cheap:
			// just repaint.
			update(_data, EData.REPAINT);
		}
		else if (status == Status.ZOOMED_HEIGHT) {

			// zoom changed.
			// init with default values
			double scrollPercentageX = 0.5;
			double scrollPercentageY = 0.5;

			// get values
			Dimension preferredSize = getPreferredSize();
			JViewport viewPort = ((JViewport) getParent());
			Dimension viewPortSize = viewPort.getSize();
			Point currentPosition = viewPort.getViewPosition();

			// check, if preferred width > viewport width
			int widthDiff = preferredSize.width - viewPortSize.width;
			if (widthDiff > 0) {
				// compute relative scroll position x
				scrollPercentageX = currentPosition.x / (double) (getSize().width - viewPortSize.width);
			}

			// check, if preferred height > viewport height
			int heightDiff = preferredSize.height - viewPortSize.height;
			if (heightDiff > 0) {
				// compute relative scroll position y
				scrollPercentageY = currentPosition.y / (double) (getSize().height - viewPortSize.height);
			}

			// default _p to current position
			_p = getLocation();

			// check, if need to update x location
			if (widthDiff > 0) {
				// we cannot see the complete configuration in x direction
				_p.x = (int) (widthDiff * scrollPercentageX);
			}

			// check, if need to update x location
			if (heightDiff > 0) {
				// we cannot see the complete configuration in x direction
				_p.y = (int) (heightDiff * scrollPercentageY);
			}

			setSize(preferredSize);
			viewPort.setViewPosition(_p);
			revalidate();
		}
	}

	/*
	 * ========================== static methods ===============================
	 */

	public static float heightToPercentage() {
		// determine zoom and lod
		int basicHeight = StringConverter.toInt(EProperties.getInstance().getProperty("BASIC_CONFIGURATION_HEIGHT"));
		int zoomedHeight = (int) Status.ZOOMED_HEIGHT.getValue();
		return (float) zoomedHeight / (float) basicHeight;
	}
}
