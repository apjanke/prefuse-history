package enronic.render;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.util.ColorMap;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> enronic(AT)jheer.org</a>
 */
public class CategoryEdgeRenderer implements Renderer {

    protected ColorMap cmap = new ColorMap(ColorMap.getHSBMap(15,1f,1f),0,15);
    
	protected Line2D    m_line   = new Line2D.Float();
	protected Ellipse2D m_circle = new Ellipse2D.Float();
	protected Arc2D[]   m_slices = new Arc2D[14];
	protected int[]     m_temp   = new int[14];
	
	protected int     m_width    = 1;
	protected int     m_curWidth = 1;
	protected double  m_radius = 5;
	protected double[] m_pts = new double[4];
	
	/**
	 * Constructor.
	 */
	public CategoryEdgeRenderer() {
	    for ( int i=0; i < m_slices.length; i++ ) {
	        m_slices[i] = new Arc2D.Float(Arc2D.PIE);
	    }
	} //
  	
  	/**
  	 * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRawShape(edu.berkeley.guir.prefuse.VisualItem)
  	 */
	protected void calcShapes(VisualItem item) {
		EdgeItem   edge = (EdgeItem)item;
		VisualItem item1 = (VisualItem)edge.getFirstNode();
		VisualItem item2 = (VisualItem)edge.getSecondNode();
		
		Point2D p1 = item1.getLocation();
		Point2D p2 = item2.getLocation();
		
		double x1 = p1.getX(), y1 = p1.getY();
		double x2 = p2.getX(), y2 = p2.getY();
		
		double mx = x1+(x2-x1)/2;
		double my = y1+(y2-y1)/2;
		
		m_circle.setFrameFromCenter(mx,my,mx+m_radius,my+m_radius);
		m_line.setLine(x1,y1,x2,y2);
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.render.Renderer#render(java.awt.Graphics2D, edu.berkeley.guir.prefuse.VisualItem)
	 */
	public void render(Graphics2D g, VisualItem item) {
	    calcShapes(item);
	    
	    // set up colors
        Paint itemColor = item.getColor();
        Paint fillColor = item.getFillColor();
        
        // calculate the category counts
        int[] cats = getCategories(item.getEntity());
        double sum = 0;
        for ( int i=1; i < cats.length; i++ ) {
            sum += cats[i];
        }
        cats[0] -= sum;
        if ( cats[0] < 0 ) cats[0] = 0;
        sum += cats[0];
        item.setAttribute("weight", String.valueOf(sum));
        
        // render the shape
        Stroke s = g.getStroke();
        Stroke is = getStroke(item);
        g.setStroke((is!=null?is:s));
        g.setPaint(itemColor);
        g.draw(m_line);
        
        // draw pie menu
        double angle = 0;
        for ( int i=0; i<cats.length; i++ ) {
            if ( cats[i] > 0 ) {
                double frac = 360.0*((double)cats[i])/sum;
                m_slices[i].setArc(m_circle.getX(),m_circle.getY(),
                                   m_circle.getWidth(),m_circle.getHeight(),
                                   angle,frac,Arc2D.PIE);
                angle += frac;
                g.setPaint(cmap.getColor(i));
                g.fill(m_slices[i]);
            }
        }

        g.setStroke(s);
		g.setPaint(itemColor);
		g.draw(m_circle);
	} //

	private String[] attr = {"base", "cat01","cat02","cat03","cat04","cat05",
            "cat06","cat07","cat08","cat09","cat10","cat11",
            "cat12","cat13"};
	
	private int[] getCategories(Entity e) {
	    for ( int i=0; i < attr.length; i++ ) {
	        m_temp[i] = Integer.parseInt(e.getAttribute(attr[i]));
	    }
	    return m_temp;
	} //
	
    /**
     * @see edu.berkeley.guir.prefuse.render.Renderer#locatePoint(java.awt.geom.Point2D, edu.berkeley.guir.prefuse.VisualItem)
     */
    public boolean locatePoint(Point2D p, VisualItem item) {
        calcShapes(item);
        Shape s1 = m_line;
        Shape s2 = m_circle;
        if ( s1 == null ) {
            return false;
        } else {
            double width = Math.max(2, getLineWidth(item));
            double halfWidth = width/2.0;
            return s2.contains(p) || s1.intersects(p.getX()-halfWidth,
                                	  p.getY()-halfWidth,
                                	  width,width);
        }
    } //

	/**
	 * Returns the line width to be used for this VisualItem. By default,
	 * returns the value set using the <code>setWidth</code> method.
	 * Subclasses should override this method to perform custom line
	 * width determination.
	 * @param item the VisualItem for which to determine the line width
	 * @return the desired line width, in pixels
	 */
	protected int getLineWidth(VisualItem item) {
	    String wstr = item.getAttribute("weight");
        if ( wstr != null && !wstr.equals("")) {
            try {
                double w = Double.parseDouble(wstr);
                return Math.max(1,1+(int)Math.round(Math.log(w)));
            } catch ( Exception e ) {
                System.err.println("Weight value is not a valid number: "+wstr);
                e.printStackTrace();
            }
        }
		return m_width;
	} //
    
    /**
     * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getStroke(edu.berkeley.guir.prefuse.VisualItem)
     */
    protected BasicStroke getStroke(VisualItem item) {
        int w = getLineWidth(item);
        return (w == 1 ? null : new BasicStroke(w));
    } //
	
	/**
	 * Sets the desired width of lines. Currently only supported by edges
	 * of type EDGE_TYPE_LINE.
	 * @param w the desired line width, in pixels
	 */
	public void setWidth(int w) {
		m_width = w;
	} //

    /**
     * @see edu.berkeley.guir.prefuse.render.Renderer#getBoundsRef(edu.berkeley.guir.prefuse.VisualItem)
     */
    public Rectangle2D getBoundsRef(VisualItem item) {
        calcShapes(item);
        Shape s = m_line;
        if ( s == null ) {
            return new Rectangle(-1,-1,0,0);
        } else {
            Rectangle2D r = s.getBounds2D();
            BasicStroke st = (BasicStroke)getStroke(item);
            if ( st != null ) {
                double w = st.getLineWidth();
                double w2 = w/2.0;
                r.setFrame(r.getX()-w2,r.getY()-w2,
                    r.getWidth()+w,r.getHeight()+w);
            }
            return r;
        }
    } //
}
