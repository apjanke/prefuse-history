package enronic.render;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;

/**
 * EnronicItemRenderer
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class EnronicItemRenderer extends TextItemRenderer {

    public int getRenderType(VisualItem item) {
        if ( item.isFocus() )
            return RENDER_TYPE_DRAW_AND_FILL;
        else if ( item.isHighlighted() )
            return RENDER_TYPE_FILL;
        else
            return RENDER_TYPE_NONE;
    } //
    
} // end of class EnronicItemRenderer
