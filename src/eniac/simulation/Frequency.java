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
package eniac.simulation;

/**
 * @author zoppke
 */
public class Frequency {

    private double _min;

    private double _logMaxDivMin;

    private Double _linear;

    private Double _logarithmic;

    public Frequency(double min, double max) {
        _min = min;
        _logMaxDivMin = Math.log(max / min);
    }

    public void setLinear(double d) {
        _linear = new Double(d);
    }

    public void setLogarithmic(double d) {
        _logarithmic = new Double(d);
    }

    public double logarithmic() {
        if (_logarithmic == null) {
            double d = Math.log(_linear.doubleValue() / _min) / _logMaxDivMin;
            d = Math.max(d, Double.MIN_VALUE);
            _logarithmic = new Double(d);
        }
        return _logarithmic.doubleValue();
    }

    public double linear() {
        if (_linear == null) {
            double d = Math.exp(_logMaxDivMin * _logarithmic.doubleValue())
                    * _min;
            _linear = new Double(d);
        }
        return _linear.doubleValue();
    }

    public String toString() {
        return "linear: " + linear() + ",  logarithmic: " + logarithmic(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static Frequency getNew() {
        return new Frequency(0.01, 10000);
    }
}
