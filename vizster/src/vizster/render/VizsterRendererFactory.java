package vizster.render;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.RendererFactory;

/**
 * Provides renderers for the Vizster application. This factory supports
 * semantic zooming, updating image resolutions based on the current
 * zoom level, and two different modes - one for general browsing and
 * one for comparing node attributes.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterRendererFactory implements RendererFactory {

    private double scaleThreshold = 2;
    
    private Display  display;
    
    private VizsterImageRenderer imageRenderer;
    private VizsterImageRenderer imageRenderer2;
    private VizsterImageRenderer compareRenderer;
    private Renderer edgeRenderer;
    
    private boolean browseMode = true;
    
    public VizsterRendererFactory(Display display) {
        this.display = display;
        
        imageRenderer2 = new VizsterImageRenderer();
        imageRenderer2.setMaxImageDimensions(150,150);
        imageRenderer2.setImageSize(0.2);
        imageRenderer2.setHorizontalPadding(2);
        
        imageRenderer = new VizsterImageRenderer();
        imageRenderer.setImageFactory(
            new SharingImageFactory(imageRenderer2.getImageFactory()));
        imageRenderer.setMaxImageDimensions(30,30);
        imageRenderer.setHorizontalPadding(2);
        
        compareRenderer = new VizsterImageRenderer() {
            public int getRenderType(VisualItem item) {
                if ( item.isFocus() )
                    return RENDER_TYPE_DRAW_AND_FILL;
                else if ( item.isHighlighted() )
                    return RENDER_TYPE_DRAW_AND_FILL;
                else
                    return RENDER_TYPE_FILL;
            } //
        };
        compareRenderer.setDrawImages(false);
        compareRenderer.setRoundedCorner(8,8);
        compareRenderer.setHorizontalPadding(2);
        
        edgeRenderer  = new VizsterEdgeRenderer();
    } //
    
    public void setBrowseMode(boolean b) {
        browseMode = b;
    } //
    
    public void setScaleThreshold(double scale) {
        scaleThreshold = scale;
    } //
    
    /**
     * @see edu.berkeley.guir.prefuse.render.RendererFactory#getRenderer(edu.berkeley.guir.prefuse.VisualItem)
     */
    public Renderer getRenderer(VisualItem item) {
        if ( item instanceof EdgeItem ) {
            return edgeRenderer;
        } else if ( item instanceof NodeItem ) {
            if ( browseMode ) {
                double scale = display.getScale();
                if ( scale >= scaleThreshold ) {
                    return imageRenderer2;
                } else {
                    return imageRenderer;
                }
            } else {
                return compareRenderer;
            }
        } else {
            return null;
        }
        
    } //

    public void setDrawImages(boolean s) {
        imageRenderer.setDrawImages(s);
    } //
    
} // end of class VizsterRendererFactory
