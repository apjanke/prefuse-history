package vizster.color;

import java.awt.Color;
import java.awt.Paint;

import vizster.Vizster;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.util.ColorLib;
import edu.berkeley.guir.prefuse.util.FocusSet;

/**
 * Color function used for Vizster's browsing mode
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class BrowsingColorFunction extends ColorFunction {

    private Color mouseColor     = ColorLib.getColor(255,125,125);
    private Color focusColor     = ColorLib.getColor(200,0,0);
    private Color edgeHighlight  = ColorLib.getColor(255,200,125);
    private Color nodeHighlight  = ColorLib.getColor(255,200,125);
    private Color defaultColor   = ColorLib.getColor(220,220,255);
    private Color fixedColor     = ColorLib.getColor(245,200,245);
    private Color searchColor    = ColorLib.getColor(255,255,150);
    
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
                return Color.BLACK;
        } else if ( item instanceof EdgeItem ) {
            return Color.LIGHT_GRAY;
        } else {
            return Color.BLACK;
        }
    } //
    
    public Paint getFillColor(VisualItem item) {
        ItemRegistry registry = item.getItemRegistry();
        FocusManager fmanager = registry.getFocusManager();
        FocusSet mouseSet = fmanager.getFocusSet(Vizster.MOUSE_KEY);
        FocusSet searchSet = fmanager.getFocusSet(Vizster.SEARCH_KEY);
        
        if ( mouseSet.contains(item.getEntity()) ) {
            return mouseColor;
        } else if ( searchSet.contains(item.getEntity()) ) {
            return searchColor;
        } else if ( item.isHighlighted() ) {
            return nodeHighlight;
        } else {
            return Color.WHITE;
        }
    } //
    
} // end of class BrowsingColorFunction
