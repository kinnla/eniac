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
 * Created on 16.04.2004
 */
package eniac.window;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;

import eniac.util.Status;

/**
 * @author zoppke
 */
public class EViewPort extends JViewport {

    // representing the location of the scrollbar according to configPanel Size.
    private float _scrollX = 0.5F;

    private float _scrollY = 0.5F;

//    public EViewPort() {
//        Status.getInstance().addListener(this);
//    }

    @Override
    protected LayoutManager createLayoutManager() {
        return null;
    }

    /*protected ViewListener createViewListener() {
        return new ViewListener() {
            public void componentResized(ComponentEvent e) {
                // empty
            }
        };
    }*/

    public void doLayout() {
    	
    	
    	Component view = getView();
    	Scrollable scrollableView = null;

    	if (view == null) {
    	    return;
    	}
    	else if (view instanceof Scrollable) {
    	    scrollableView = (Scrollable) view;
    	}

    	/* All of the dimensions below are in view coordinates, except
    	 * vpSize which we're converting.
    	 */

    	Dimension viewPrefSize = view.getPreferredSize();
    	Dimension vpSize = getSize();
    	Dimension viewSize = new Dimension(viewPrefSize);
    	Point viewPosition = getViewPosition();

    	/* If the new viewport size would leave empty space to the
    	 * right of the view, right justify the view or left justify
    	 * the view when the width of the view is smaller than the
    	 * container.
    	 */
    	if (scrollableView == null ||
    	    getParent() == null ||
    	    getParent().getComponentOrientation().isLeftToRight()) {
    	    if ((viewPosition.x + vpSize.width) > viewSize.width) {
    		viewPosition.x = Math.max(0, viewSize.width - vpSize.width);
    	    }
    	} else {
    	    if (vpSize.width > viewSize.width) {
    		viewPosition.x = viewSize.width - vpSize.width;
    	    } else {
    		viewPosition.x = Math.max(0, Math.min(viewSize.width - vpSize.width, viewPosition.x));
    	    }
    	}

    	/* If the new viewport size would leave empty space below the
    	 * view, bottom justify the view or top justify the view when
    	 * the height of the view is smaller than the container.
    	 */
    	if ((viewPosition.y + vpSize.height) > viewSize.height) {
    	    viewPosition.y = Math.max(0, viewSize.height - vpSize.height);
    	}

    	/* If we haven't been advised about how the viewports size 
    	 * should change wrt to the viewport, i.e. if the view isn't
    	 * an instance of Scrollable, then adjust the views size as follows.
    	 * 
    	 * If the origin of the view is showing and the viewport is
    	 * bigger than the views preferred size, then make the view
    	 * the same size as the viewport.
    	 */
    	if (scrollableView == null) {
                if ((viewPosition.x == 0) && (vpSize.width > viewPrefSize.width)) {
            	viewSize.width = vpSize.width;
                }
                if ((viewPosition.y == 0) && (vpSize.height > viewPrefSize.height)) {
            	viewSize.height = vpSize.height;
                }
            }
    	
    	setViewPosition(new Point((int) ((getWidth() - viewPrefSize.width) * _scrollX),
                (int) ((getHeight() - viewPrefSize.height) * _scrollY) ));
    	setViewSize(viewPrefSize);
	getView().repaint();
    	
    	//===========================

        // set new bounds to the configPanel
        /*Dimension prefSize = view.getPreferredSize();
        view.setBounds((int) ((getWidth() - prefSize.width) * _scrollX),
                (int) ((getHeight() - prefSize.height) * _scrollY),
                prefSize.width, prefSize.height);
*/
//super.doLayout();
        // fire events especially to notify scrollbars, that bounds of view
        // changed.
        //fireStateChanged();
    }

    /**
     * @param evt
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
    
        if (evt.getPropertyName().equals("zoomed_height")) {

            // get the current
            Rectangle bounds = getView().getBounds();

            // compute scroll position x
            int xDiff = getWidth() - bounds.width;
            if (xDiff < 0) {
                _scrollX = bounds.x / (float) (xDiff);
            } else {
                _scrollX = 0.5F;
            }

            // compute scroll position y
            int yDiff = getHeight() - bounds.height;
            if (yDiff < 0) {
                _scrollY = bounds.y / (float) (yDiff);
            } else {
                _scrollY = 0.5F;
            }

            //((ConfigPanel)getView()).propertyChange(evt);
            // update layout
            revalidate();
            //((JComponent)getView()).revalidate();
            //doLayout();
        }
    }
}
