package vizster;

import java.awt.Color;
import java.awt.Paint;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.util.ColorMap;
import edu.berkeley.guir.prefuse.util.FocusSet;

/**
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterColorFunction extends ColorFunction {

    private Color transparent    = new Color(255,255,255,0);
    private Color focusColor     = new Color(200,0,0);
    private Color edgeHighlight  = new Color(255,200,125);
    private Color nodeHighlight  = Color.ORANGE;
    private Color defaultColor   = new Color(220,220,255);
    private Color fixedColor     = new Color(245,200,245);
    
    ColorMap cmap = new ColorMap(ColorMap.getGrayscaleMap(),-25,150);
    
    public Paint getColor(VisualItem item) {
        ItemRegistry registry = item.getItemRegistry();
        FocusManager fmanager = registry.getFocusManager();
        FocusSet profileSet = fmanager.getFocusSet(Vizster.CLICK_KEY);
        
        if ( profileSet.contains(item.getEntity()) ) {
            return focusColor;
        } else if ( item.isHighlighted() ) {
            if ( item instanceof EdgeItem )
                return edgeHighlight;
            else
                return nodeHighlight;
        } else if ( item instanceof EdgeItem ) {
            return Color.LIGHT_GRAY;
        } else {
            return Color.BLACK;
        }
    } //
    
    public Paint getFillColor(VisualItem item) {
//        if ( item instanceof NodeItem ) {
//            NodeItem nitem = (NodeItem)item;
//            Node n = (Node)nitem.getEntity();
//            return cmap.getColor(n.getEdgeCount());
//        } else
//            return Color.BLACK;
        return Color.WHITE;
    } //            
    
} // end of class VizsterColorFunction
