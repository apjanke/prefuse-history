package vizster.render;

import java.awt.BasicStroke;
import java.awt.Image;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;

/**
 * Subclass of TextImageItemRenderer that allows control of image rendering.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterImageRenderer extends TextImageItemRenderer {

    private boolean drawImages = true;
    private BasicStroke stroke = new BasicStroke(2);
    
    public VizsterImageRenderer() {
        this.setImageAttributeName("photourl");
        this.setTextAttributeName("name");
    } //
    
    public int getRenderType(VisualItem item) {
        if ( item.isFocus() )
            return RENDER_TYPE_DRAW_AND_FILL;
        else if ( item.isHighlighted() )
            return RENDER_TYPE_FILL;
        else
            return RENDER_TYPE_NONE;
    } //
    
    public BasicStroke getStroke(VisualItem item) {
        return stroke;
    } //
    
    public void setDrawImages(boolean s) {
        drawImages = s;
    } //
    
    protected Image getImage(VisualItem item) {
        return ( drawImages ? super.getImage(item) : null );
    } //
    
} // end of class VizsterImageRenderer
