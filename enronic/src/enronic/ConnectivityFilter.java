package enronic;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> enronic(AT)jheer.org</a>
 */
public class ConnectivityFilter extends AbstractAction {

    private int threshold = 1;
    
    public int getThreshold() {
        return threshold;
    } //
    
    public void setThreshold(int thresh) {
        threshold = thresh;
    } //
    
    /**
     * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
     */
    public void run(ItemRegistry registry, double frac) {
        Iterator iter = registry.getNodeItems();
        while ( iter.hasNext() ) {
            NodeItem n = (NodeItem)iter.next();
            int degree = n.getEdgeCount();
            if ( degree < threshold ) {
                Iterator edges = n.getEdges();
                while ( edges.hasNext() ) {
                    EdgeItem e = (EdgeItem)edges.next();
                    e.setVisible(false);
                }
                n.setVisible(false);
            }
        }
    } //

} // end of class ConnectivityFilter
