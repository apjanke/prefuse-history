package prefuse.hyperbolictree;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;

/**
 * 
 * Feb 14, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefusex(AT)jheer.org
 */
public class HyperbolicTranslationEnd extends AbstractAction {

    public void run(ItemRegistry registry, double frac) {
        Iterator nodeIter = registry.getNodeItems(false);
        while ( nodeIter.hasNext() ) {
            NodeItem n = (NodeItem)nodeIter.next();
            HyperbolicParams np = getParams(n);
            if ( np != null ) {
                np.zo[0] = np.z[0];
                np.zo[1] = np.z[1];
            }
        }
    } //
    
    private HyperbolicParams getParams(VisualItem n) {
        return (HyperbolicParams)n.getVizAttribute("hyperbolicParams");
    } //

} // end of class HyperbolicTranslationEnd
