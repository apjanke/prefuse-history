package vizster.render;

import java.awt.Image;

import edu.berkeley.guir.prefuse.render.ImageFactory;

/**
 * An image factory subclass, which upon loading an image
 *  adds it to an additional factory as well. This reduces image loading
 *  times by sharing data.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class SharingImageFactory extends ImageFactory {

    private ImageFactory m_shared;
    
    public SharingImageFactory(ImageFactory factory) {
        m_shared = factory;
    } //
    
    public Image addImage(String location, Image image) {
        m_shared.addImage(location, image);
        return super.addImage(location, image);
    } //
    
} // end of class SharingImageFactory
