package enronic.color;

import java.awt.Color;
import java.awt.Paint;

import prefusex.community.CommunitySet;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.util.ColorLib;
import edu.berkeley.guir.prefuse.util.ColorMap;
import enronic.Enronic;

/**
 * Color function used for enronic's browsing mode
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class BrowsingColorFunction extends ColorFunction {

    private Color mouseColor     = ColorLib.getColor(255,125,125);
    private Color focusColor     = ColorLib.getColor(200,0,0);
    private Color edgeHighlight  = ColorLib.getColor(255,200,125);
    private Color nodeHighlight  = ColorLib.getColor(255,200,125);
    private Color defaultColor   = ColorLib.getColor(220,220,255);
    private Color fixedColor     = ColorLib.getColor(245,200,245);
    private Color searchColor    = ColorLib.getColor(255,255,150);
    
    //private CommunitySet community;
    private ColorMap communityColor;
    
    public void updateCommunityMap(CommunitySet community) {
        if ( community == null || community.size() == 0 ) {
            communityColor = null;
        } else {
            communityColor = new ColorMap(
                ColorMap.getHSBMap(community.size()+1, 0.2f, 1.f),
                0,
                community.size()+1);
        }
    } //
    
    public Paint getColor(VisualItem item) {
        ItemRegistry registry = item.getItemRegistry();
        FocusManager fmanager = registry.getFocusManager();
        FocusSet profileSet = fmanager.getFocusSet(Enronic.CLICK_KEY);
        
        if ( profileSet.contains(item.getEntity()) ) {
            return focusColor;
        } else if ( item.isFocus() ) {
            if ( item instanceof EdgeItem ) {
                return focusColor;
            } else {
                return Color.BLACK;
            }
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
        FocusSet mouseSet = fmanager.getFocusSet(Enronic.MOUSE_KEY);
        FocusSet searchSet = fmanager.getFocusSet(Enronic.SEARCH_KEY);
        CommunitySet communitySet = (CommunitySet)fmanager.getFocusSet(Enronic.COMMUNITY_KEY);
        
        if ( mouseSet.contains(item.getEntity()) ) {
            return mouseColor;
        } else if ( searchSet.contains(item.getEntity()) ) {
            return searchColor;
        } else if ( item.isHighlighted() ) {
            return nodeHighlight;
        } else if ( communitySet.contains(item.getEntity()) ) {
            try {
                return communityColor.getColor(
                        communitySet.getCommunity((Node)item.getEntity()));
            } catch ( Exception e ) {
                return Color.WHITE;
            }
        } else {
            return Color.WHITE;
        }
    } //
    
} // end of class BrowsingColorFunction
