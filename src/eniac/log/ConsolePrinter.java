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
 * Created on 20.02.2004
 */
package eniac.log;

/**
 * @author zoppke
 */
public class ConsolePrinter implements LogListener {

	public ConsolePrinter() {
		// empty
	}

	/**
	 * @param message
	 * @see eniac.log.LogListener#incomingMessage(eniac.log.Message)
	 */
	public void incomingMessage(LogMessage message) {
		System.out.println(message);
	}

	/**
	 * 
	 * @see eniac.log.LogListener#cleared()
	 */
	public void cleared() {
		// nop
	}

}
