package vizster.render;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.RendererFactory;

/**
 * 
 * Apr 13, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterRendererFactory implements RendererFactory {

    private double scaleThreshold = 2;
    
    private Display  display;
    
    private VizsterImageRenderer imageRenderer;
    private VizsterImageRenderer imageRenderer2;
    private Renderer edgeRenderer;
    
    public VizsterRendererFactory(Display display) {
        this.display = display;
        
        imageRenderer = new VizsterImageRenderer();
        imageRenderer.setMaxImageDimensions(30,30);
        imageRenderer.setHorizontalPadding(2);
        //imageRenderer.setRoundedCorner(8,8);
        
        imageRenderer2 = new VizsterImageRenderer();
        imageRenderer2.setMaxImageDimensions(150,150);
        imageRenderer2.setImageSize(0.2);
        imageRenderer2.setHorizontalPadding(2);
        //imageRenderer2.setRoundedCorner(8,8);
        
        edgeRenderer  = new VizsterEdgeRenderer();
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
            double scale = display.getScale();
            if ( scale >= scaleThreshold ) {
                return imageRenderer2;
            } else {
                return imageRenderer;
            }
        } else {
            return null;
        }
        
    } //

    public void setDrawImages(boolean s) {
        imageRenderer.setDrawImages(s);
    } //
    
} // end of class VizsterRendererFactory
