package enronic.render;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;

/**
 * Enronic edge renderer, toggles edge width based on highlighted status
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class EnronicEdgeRenderer extends DefaultEdgeRenderer {

    public int getLineWidth(VisualItem item) {
        return m_width*(item.isHighlighted() ? 2 : 1);
    } //
    
} // end of class enronicEdgeRenderer
