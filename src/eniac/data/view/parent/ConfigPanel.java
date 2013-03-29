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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Inherited;

import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import eniac.data.CableManager;
import eniac.data.model.EData;
import eniac.data.type.EType;
import eniac.data.view.EPanel;
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
public class ConfigPanel extends ParentPanel implements Scrollable,
		PropertyChangeListener {

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
		Status.getInstance().addListener("highlight_pulse", this);
		Status.getInstance().addListener("zoomed_height", this);
	}

	public void dispose() {
		super.dispose();
		Status.getInstance().removeListener(this);
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
		int height = Status.getInt("zoomed_height");

		// set lod
        Skin skin = (Skin) Status.get(("skin"));
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

	/**
	 * 
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

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 30;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 30;
	}

	public static float heightToPercentage() {
		// determine zoom and lod
		int basicHeight = StringConverter.toInt(EProperties.getInstance()
				.getProperty("BASIC_CONFIGURATION_HEIGHT"));
		int zoomedHeight = Status.getInt("zoomed_height");
		return (float) zoomedHeight / (float) basicHeight;
	}

	/*
	 * ============================ event listening ============================
	 */

	/**
	 * @param evt
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("highlight_pulse")) {
			// highlight mode changed by user action.
			// Because this won't happen too often don't be niggardly:
			// just repaint.
			update(_data, EData.REPAINT);
		} else if (evt.getPropertyName().equals("zoomed_height")) {
			
			Dimension preferredSize = getPreferredSize();
			JViewport viewPort = ((JViewport) getParent());
			Dimension viewPortSize = viewPort.getSize();
			Point currentPosition = viewPort.getViewPosition();

			_p.x = -(currentPosition.x + viewPortSize.width / 2) * preferredSize.width / getWidth() - viewPortSize.width / 2;
			_p.y = -(currentPosition.y + viewPortSize.height / 2) * preferredSize.height / getHeight() - viewPortSize.height / 2;

			if (viewPortSize.width > Math.min(preferredSize.width, getWidth())) {
				_p.x = -(preferredSize.width - viewPortSize.width) / 2;
			}
			
			if (viewPortSize.height > Math.min(preferredSize.height, getHeight())) {
				_p.y = -(preferredSize.height- viewPortSize.height) / 2;
			}
			
			System.out.println("setLocation: "+_p.x+",  "+_p.y);
			
			setBounds(_p.x,_p.y, preferredSize.width, preferredSize.height);
			revalidate();
			
			//viewPort.setViewPosition(_p);
			
			/*SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setLocation(_p.x,_p.y);
				}
			});*/

			//viewPort.getParent().doLayout();
			
	        // set new bounds to the configPanel
	        /*Dimension prefSize = view.getPreferredSize();
	        view.setBounds((int) ((getWidth() - prefSize.width) * _scrollX),
	                (int) ((getHeight() - prefSize.height) * _scrollY),
	                prefSize.width, prefSize.height);*/
		}
	}
}