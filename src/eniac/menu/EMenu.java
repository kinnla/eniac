package eniac.menu;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;

import eniac.lang.Dictionary;
import eniac.util.Status;

public class EMenu extends JMenu implements PropertyChangeListener {

	private String _sid;
	
	public EMenu(String sid) {
		_sid = sid;
		Status.getInstance().addListener("language", this);
		setText(Dictionary.get(_sid));
	}

	public void propertyChange(PropertyChangeEvent evt) {
		setText(Dictionary.get(_sid));
	}
}
