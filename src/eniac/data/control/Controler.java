/*
 * Created on 28.03.2004
 */
package eniac.data.control;

import java.awt.event.MouseEvent;

/**
 * @author zoppke
 */
public interface Controler {

    public static final BasicControler NONE = new BasicControler();

    public void mpressed(MouseEvent e);

    public void mreleased(MouseEvent e);

    public void mdragged(MouseEvent e);

}