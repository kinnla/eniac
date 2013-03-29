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