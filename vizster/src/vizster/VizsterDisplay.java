package vizster;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.util.ColorLib;
import edu.berkeley.guir.prefuse.util.FontLib;


/**
 * 
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterDisplay extends Display {
    
    private AffineTransform id = new AffineTransform();
    
    public VizsterDisplay(ItemRegistry registry) {
        super(registry);
    } //
    
    public void prePaint(Graphics2D g) {
        Object o = g.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        AffineTransform at = g.getTransform();
        Color c = ColorLib.getColor(200,200,200,255);
        Font f = FontLib.getFont("SansSerif",Font.BOLD|Font.ITALIC,48);
        FontMetrics fm = g.getFontMetrics(f);
        int x = 8, y = fm.getAscent();
        g.setTransform(id);
        g.setColor(c);
        g.setFont(f);
        g.drawString("vizster",x,y);
        g.setTransform(at);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, o);
    } //
    
} // end of class VizsterDisplay
