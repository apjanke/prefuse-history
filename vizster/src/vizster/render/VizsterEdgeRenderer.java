package vizster.render;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;

/**
 * Vizster edge renderer
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterEdgeRenderer extends DefaultEdgeRenderer {

    public int getLineWidth(VisualItem item) {
        return m_width*(item.isHighlighted() ? 2 : 1);
    } //
    
} // end of class VizsterEdgeRenderer
