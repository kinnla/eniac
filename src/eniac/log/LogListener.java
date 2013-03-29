/*
 * Created on 11.08.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package eniac.log;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public interface LogListener {

    /**
     * This method is called, when a line has been added to the logger
     * 
     * @param line
     *            The line that was added
     */
    public void incomingMessage(LogMessage message);

    /**
     * This method is called, when the logger is cleared.
     *  
     */
    public void cleared();

}