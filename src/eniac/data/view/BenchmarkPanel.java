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
 * Created on 31.03.2004
 */
package eniac.data.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import eniac.data.model.Benchmark;
import eniac.data.type.ParentGrid;
import eniac.io.Tag;
import eniac.simulation.Frequency;
import eniac.skin.Descriptor;

/**
 * @author zoppke
 */
public class BenchmarkPanel extends EPanel {

    /**
     * @param data
     */
    public BenchmarkPanel() {
        // empty
    }

    protected void paintComponent(Graphics g, int x, int y, int width,
            int height, int lod) {

        // super paints background image
        super.paintComponent(g, x, y, width, height, lod);

        // get descriptor. If no descriptor, just return.
        Descriptor descriptor = getDescriptor(lod);
        if (descriptor == null) {
            return;
        }

        // if power, paint curve
        if (_data.hasPower()) {

            // get variables
            Benchmark benchmark = (Benchmark) _data;
            Frequency[] freqs = benchmark.getFrequencies();
            int pointer = benchmark.getPointer();
            ParentGrid grid = (ParentGrid) _data.getType().getGrid(width,
                    height, lod);
            int gridHeight = grid.yValues[1] - grid.yValues[0];
            Point previous = null;
            Color color = (Color) descriptor.get(Tag.COLOR);
            // draw benchmark curve
            g.setColor(color);
            for (int i = 0; i < freqs.length; ++i) {

                // get variables
                Frequency freq = freqs[(i + pointer) % freqs.length];
                int yy = grid.yValues[1]
                        - (int) (freq.logarithmic() * gridHeight);
                yy = Math.min(yy, grid.yValues[1] - 1);
                int xx = grid.xValues[i];

                // draw
                if (previous == null) {
                    // if no previous point, we don't need to draw a line.
                    // just initialize the point.
                    previous = new Point(xx, yy);
                } else {
                    // draw line from previous to current point.
                    g.drawLine(x + previous.x, y + previous.y, x + xx, y + yy);
                    // adjust previous point.
                    previous.x = xx;
                    previous.y = yy;
                }
            }
        }

        // draw foreground image
        Image img = (Image) descriptor.get(Tag.FORE_IMAGE);
        g.drawImage(img, x, y, width, height, this);
    }
}
