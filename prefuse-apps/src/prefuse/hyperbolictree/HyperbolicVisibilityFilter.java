package prefuse.hyperbolictree;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;

/**
 * 
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefusex(AT)jheer.org
 */
public class HyperbolicVisibilityFilter extends AbstractAction {

    private double m_thresh = 0.96;
    
    public void run(ItemRegistry registry, double frac) {
        Iterator iter = registry.getNodeItems(false);
        while ( iter.hasNext() ) {
            NodeItem item = (NodeItem)iter.next();
            HyperbolicParams np = getParams(item);
            double d = Math.sqrt(np.z[0]*np.z[0] + np.z[1]*np.z[1]);
            item.setVisible(d<m_thresh);
        }
        iter = registry.getEdgeItems(false);
        while ( iter.hasNext() ) {
            EdgeItem item = (EdgeItem)iter.next();
            NodeItem n = (NodeItem)item.getFirstNode();
            HyperbolicParams np = getParams(n);
            double d = Math.sqrt(np.z[0]*np.z[0] + np.z[1]*np.z[1]);
            item.setVisible(d<m_thresh);
        }
    } //
    
    public HyperbolicParams getParams(VisualItem item) {
        return (HyperbolicParams)item.getVizAttribute("hyperbolicParams");
    } //

} // end of class HyperbolicVisibilityFilter
