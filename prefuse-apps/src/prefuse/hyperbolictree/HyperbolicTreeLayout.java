package prefuse.hyperbolictree;

import java.util.Iterator;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import edu.berkeley.guir.prefuse.graph.DefaultTree;

/**
 * 
 * Feb 13, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefusex(AT)jheer.org
 */
public class HyperbolicTreeLayout extends TreeLayout {

    private double m_length = 0.3;
    
    public NodeItem getLayoutRoot(ItemRegistry registry) {
        NodeItem r = super.getLayoutRoot();
        if ( r != null )
            return r;
        DefaultTree t = (DefaultTree)registry.getGraph();
        return registry.getNodeItem(t.getRoot());
    } //

    public void run(ItemRegistry registry, double frac) {
        NodeItem n = getLayoutRoot(registry);
        calcWeight(n);
        layout(n, 0.0, Math.PI, m_length);
    } //

    private double calcWeight(NodeItem n) {
        HyperbolicParams np = getParams(n);
        double w = 0;
        Iterator iter = n.getChildren();
        while ( iter.hasNext() ) {
            NodeItem c = (NodeItem)iter.next();
            w += calcWeight(c);
        }
        np.weight = w;
        return Math.max(1,n.getSize()) + (w!=0 ? Math.log(w) : 0);
    } //
    
    /**
     * Layout this node in the hyperbolic space.
     * First set the point at the right distance,
     * then translate by father's coordinates.
     * Then, compute the right angle and the right width.
     *
     * @param angle     the angle from the x axis (bold as love)
     * @param width     the angular width to divide, / 2
     * @param length    the parent-child length
     */
    private void layout(NodeItem n, double angle, double width, double length) {
        HyperbolicParams np = getParams(n);
        NodeItem p = (NodeItem)n.getParent();
        
        if ( p != null) {
            HyperbolicParams pp = getParams(p);

            np.z[0] = length * Math.cos(angle);
            np.z[1] = length * Math.sin(angle);

            // Then translate by parent's coordinates
            HyperbolicParams.translate(np.z, pp.z);
            np.zo[0] = np.z[0];
            np.zo[1] = np.z[1];
            
            if ( n.getChildCount() > 0 ) {
                // Compute the new starting angle
                // e(i a) = T(z)oT(zp) (e(i angle))
                double[] a = { Math.cos(angle), Math.sin(angle) };
                double[] nz = { -np.z[0], -np.z[1] };
                HyperbolicParams.translate(a, pp.z);
                HyperbolicParams.translate(a, nz);
                angle = HyperbolicParams.angle(a);
    
                // Compute the new width
                // e(i w) = T(-length) (e(i width))
                // decomposed to do it faster :-)
                double c = Math.cos(width);
                double A = 1 + length*length;
                double B = 2*length;
                width = Math.acos((A*c - B) / (A-B * c));
            }
        }

        int numChildren = n.getChildCount();
        if ( numChildren == 0 )
            return; // if no kids, we're done
        
        double l1 = 0.95 - m_length;
        double l2 = Math.cos(20.0*Math.PI / (2.0*numChildren + 38.0)); 
        length = m_length + (l1 * l2);
        double startAngle = angle - width;

        Iterator childIter = n.getChildren();
        while ( childIter.hasNext() ) {
            // layout child node and descendants
            NodeItem c = (NodeItem)childIter.next();
            HyperbolicParams cp = getParams(c);
            double cweight = Math.max(1.0,c.getSize())
                + (cp.weight!=0 ? Math.log(cp.weight) : 0);
            double cwidth = width * (cweight/np.weight);
            double cangle = startAngle + cwidth;
            layout(c, cangle, cwidth, length);
            startAngle += 2.0*cwidth;
            
            // compute hyperbolic control point for edge
            EdgeItem e = (EdgeItem)n.getEdge(c);
            HyperbolicParams ep = getParams(e);
            HyperbolicParams.setControlPoint(ep.z, np.z, cp.z);
        }
    } //
    
    private HyperbolicParams getParams(VisualItem n) {
        HyperbolicParams np = 
            (HyperbolicParams)n.getVizAttribute("hyperbolicParams");
        if ( np == null ) {
            np = new HyperbolicParams();
            n.setVizAttribute("hyperbolicParams", np);
        }
        return np;
    } //

} // end of class HyperbolicTreeLayout
