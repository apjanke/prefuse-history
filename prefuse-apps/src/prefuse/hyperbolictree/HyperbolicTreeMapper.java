package prefuse.hyperbolictree;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;

/**
 * Maps the stored points on the complex unit circle for each
 * VisualItem onto the corresponding points on the screen.
 * 
 * Feb 13, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefusex(AT)jheer.org
 */
public class HyperbolicTreeMapper extends TreeLayout {

    private Point2D m_max    = new Point2D.Float();
    
    public Point2D getLayoutAnchor(ItemRegistry registry) {
        Point2D anchor = super.getLayoutAnchor();
        if ( anchor != null )
            return anchor;
        
        double x = 0, y = 0;
        if ( registry != null ) {
            Display d = registry.getDisplay(0);
            x = d.getWidth()/2; y = d.getHeight()/2;
        }
        return new Point2D.Double(x,y);
    } //
    
    public Rectangle2D getLayoutBounds(ItemRegistry registry) {
        Rectangle2D r = super.getLayoutBounds();
        if ( r != null )
            return r;
        
        r = new Rectangle2D.Double();
        if ( registry != null ) {
            Display d = registry.getDisplay(0);
            r.setFrame(0,0,d.getWidth(),d.getHeight());
        }
        return r;
    } //
    
    public void run(ItemRegistry registry, double frac) {
        Rectangle2D b = getLayoutBounds(registry);
        Point2D anchor = getLayoutAnchor(registry);
        m_max.setLocation(b.getWidth()/2, b.getHeight()/2);
        
        Iterator itemIter = registry.getItems(false);
        while ( itemIter.hasNext() ) {
            VisualItem item = (VisualItem)itemIter.next();
            HyperbolicParams hp = 
                (HyperbolicParams)item.getVizAttribute("hyperbolicParams");
            if ( hp != null ) {
                HyperbolicParams.project(
                    item.getLocation(), hp.z, anchor, m_max);
            }
        }
    } //
    
} // end of class HyperbolicTreeMapper
