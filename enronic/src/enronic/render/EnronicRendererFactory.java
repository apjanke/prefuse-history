package enronic.render;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.RendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;

/**
 * Provides renderers for the enronic application. This factory supports
 * semantic zooming, updating image resolutions based on the current
 * zoom level, and two different modes - one for general browsing and
 * one for comparing node attributes.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class EnronicRendererFactory implements RendererFactory {

    private double scaleThreshold = 0.1;
    
    private Display  display;
    
    private TextItemRenderer imageRenderer;
    private TextItemRenderer imageRenderer2;
    private TextItemRenderer compareRenderer;
    private Renderer edgeRenderer1;
    private Renderer edgeRenderer2;
    
    private boolean browseMode = true;
    
    public EnronicRendererFactory(Display display) {
        this.display = display;
        
        imageRenderer2 = new EnronicItemRenderer();
        
        imageRenderer = new EnronicItemRenderer();
        imageRenderer.setHorizontalPadding(2);
        
        compareRenderer = new EnronicItemRenderer();
        compareRenderer.setRoundedCorner(8,8);
        compareRenderer.setHorizontalPadding(2);
        
        edgeRenderer1 = new EnronicEdgeRenderer();
        edgeRenderer2 = new CategoryEdgeRenderer();
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
            double scale = display.getScale();
            if ( scale >= scaleThreshold ) {
                return edgeRenderer2;
            } else {
                return edgeRenderer1;
            }
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
    
} // end of class enronicRendererFactory
