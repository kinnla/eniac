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
 * Created on 11.04.2004
 */
package eniac.menu.action;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import eniac.skin.Skin;
import eniac.util.Status;
import eniac.util.StatusListener;
import eniac.util.StatusMap;

/**
 * @author zoppke
 */
public class ZoomOut extends EAction {

	public ZoomOut() {
		StatusMap.getInstance().addListener(Status.ZOOMED_HEIGHT, new StatusListener() {

			@Override
			public void statusChanged(Status status, Object newValue) {
				setEnabled((int) newValue != getNewHeight());
			}
		});
	}

	int getNewHeight() {

		// get current height and zoomSteps
		int height = StatusMap.getInt(Status.ZOOMED_HEIGHT);
		Skin skin = (Skin) StatusMap.get(Status.SKIN);
		int[] steps = skin.getZoomSteps();

		// search index of current height in zoomSteps
		int index = Arrays.binarySearch(steps, height);
		if (index < 0) {
			// we are between 2 steps. Choose the upper one.
			index = -index - 1;
		}
		// decrease step, but check that we stay inside array bounds
		return steps[Math.max(index - 1, 0)];
	}

	public void actionPerformed(ActionEvent e) {
		StatusMap.set(Status.ZOOMED_HEIGHT, getNewHeight());
	}
}
