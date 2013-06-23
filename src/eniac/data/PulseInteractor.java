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
 * Created on 02.04.2004
 */
package eniac.data;

/**
 * @author zoppke
 */
public interface PulseInteractor {
	public void receiveProgram(long time, PulseInteractor source);

	public void sendProgram(long time, PulseInteractor source);

	public void receiveDigit(long time, long value, PulseInteractor source);

	public void sendDigit(long time, long value, PulseInteractor source);

	public boolean canReceiveDigit(long time, PulseInteractor source);

	public boolean canReceiveProgram(long time, PulseInteractor source);
}
