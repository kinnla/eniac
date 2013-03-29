/*
 * Created on 25.01.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package eniac.data.type;

/**
 * @author zoppke
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ParentGrid extends Grid {

    public float zoomX;

    public float zoomY;

    public int[] xValues;

    public int[] yValues;

    public ParentGrid(int w, int h) {
        super(w, h);
    }
}