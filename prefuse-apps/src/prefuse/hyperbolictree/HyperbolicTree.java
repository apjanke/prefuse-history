package prefuse.hyperbolictree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.net.URL;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.TreeFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.ActivityMap;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.collections.DOIItemComparator;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.HDirTreeReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.NullRenderer;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.RendererFactory;
import edu.berkeley.guir.prefuse.render.ShapeRenderer;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefuse.util.ColorLib;
import edu.berkeley.guir.prefuse.util.StringAbbreviator;

/**
 * prefuse Hyperbolic Tree demo
 * 
 * @version 1.0
 * @author Jeffrey Heer <a href="mailto:jheer@acm.org">jheer@acm.org</a>
 */
public class HyperbolicTree extends JFrame {

	public static final String TREE_CHI = "/chitest.hdir";
		
	public static ItemRegistry registry;
	public static Tree tree;
	public static Display display;
    public static HyperbolicTranslation translation;
    public static ActivityMap actmap = new ActivityMap();
    
    private static Font frameCountFont = new Font("SansSerif", Font.PLAIN, 14);
		
	public HyperbolicTree(String dataFile) {
	    super("HyperbolicTree -- "+dataFile);
		try {
			// load graph
			URL input = HyperbolicTree.class.getResource(dataFile);
			tree = new HDirTreeReader().loadTree(input);
			
			// create display and filter
            registry = new ItemRegistry(tree);
            registry.setItemComparator(new DOIItemComparator());
            display = new Display(registry);

			// initialize renderers
			// create a text renderer with rounded corners and labels
            // with a maximum length of 75 pixels, abbreviated as names.
            TextItemRenderer nodeRenderer = new TextItemRenderer();
			nodeRenderer.setRoundedCorner(8,8);
            nodeRenderer.setMaxTextWidth(75);
            nodeRenderer.setAbbrevType(StringAbbreviator.NAME);
            // create a null renderer for use when no label should be shown
			NullRenderer nodeRenderer2 = new NullRenderer();
            // create an edge renderer with custom curved edges
			DefaultEdgeRenderer edgeRenderer = new DefaultEdgeRenderer() {
                protected void getCurveControlPoints(EdgeItem eitem, 
                    Point2D[] cp, double x1, double y1, double x2, double y2) 
                {
                    Point2D c = eitem.getLocation();      
                    cp[0].setLocation(c);
                    cp[1].setLocation(c);
                } //
			};
            edgeRenderer.setEdgeType(DefaultEdgeRenderer.EDGE_TYPE_CURVE);
            edgeRenderer.setRenderType(ShapeRenderer.RENDER_TYPE_DRAW);
			
			// set the renderer factory
			registry.setRendererFactory(new DemoRendererFactory(
				nodeRenderer, nodeRenderer2, edgeRenderer));
			
			// initialize the display
			display.setSize(500,460);
			display.setBackground(Color.WHITE);
			display.addControlListener(new DemoControl());			
			TranslateControl dragger = new TranslateControl();
            display.addMouseListener(dragger);
            display.addMouseMotionListener(dragger);
			
            // initialize repaint list
            ActionList repaint = new ActionList(registry);
            repaint.add(new HyperbolicTreeMapper());
            repaint.add(new HyperbolicVisibilityFilter());
            repaint.add(new RepaintAction());
            actmap.put("repaint", repaint);
            
			// initialize filter
            ActionList filter  = new ActionList(registry);
            filter.add(new TreeFilter());
            filter.add(new HyperbolicTreeLayout());
            filter.add(new DemoColorFunction());
            filter.add(repaint);
            actmap.put("filter", filter);
   
            // intialize hyperbolic translation
            ActionList translate = new ActionList(registry);
            translation = new HyperbolicTranslation();
            translate.add(translation);
            translate.add(repaint);
            actmap.put("translate", translate);
            
            // intialize animated hyperbolic translation
            ActionList animate = new ActionList(registry, 1000, 20);
            animate.setPacingFunction(new SlowInSlowOutPacer());
            animate.add(translate);
            actmap.put("animate", animate);
            
            // intialize the end translation list
            ActionList endTranslate = new ActionList(registry);
            endTranslate.add(new HyperbolicTranslationEnd());
            actmap.put("endTranslate", endTranslate);
			
            // construct the application frame
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			getContentPane().add(display, BorderLayout.CENTER);
			pack();
			setVisible(true);
			
            // set initial focus
            registry.getDefaultFocusSet().set(tree.getRoot());
            
            actmap.scheduleNow("filter");
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
	public static void main(String[] args) {
	    String infile = TREE_CHI;
        if ( args.length > 0 )
            infile = args[0];
	    new HyperbolicTree(infile);
	} //
	
    public class TranslateControl extends MouseAdapter implements MouseMotionListener {
        boolean drag = false;
        public void mousePressed(MouseEvent e) {
            translation.setStartPoint(e.getX(), e.getY());
        } //
        public void mouseDragged(MouseEvent e) {
            drag = true;
            translation.setEndPoint(e.getX(), e.getY());
            actmap.scheduleNow("translate");
        } //
        public void mouseReleased(MouseEvent e) {
            if ( drag ) {
                actmap.scheduleNow("endTranslate");
                drag = false;
            }
        } //
        public void mouseMoved(MouseEvent e) {
        } //
    } // end of inner class TranslateControl
    
    
	public class DemoControl extends ControlAdapter {
        public void itemEntered(VisualItem item, MouseEvent e) {
            e.getComponent().setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } //
        public void itemExited(VisualItem item, MouseEvent e) {
            e.getComponent().setCursor(Cursor.getDefaultCursor());
        } //        
		public void itemClicked(VisualItem item, MouseEvent e) {
		    // animate a translation when a node is clicked
			int cc = e.getClickCount();
			if ( item instanceof NodeItem ) {
				if ( cc == 1 ) {
					TreeNode node = (TreeNode)registry.getEntity(item);
                    if ( node != null ) {                           
                        translation.setStartPoint(e.getX(), e.getY());
                        translation.setEndPoint(e.getX(), e.getY());
                        actmap.scheduleNow("animate");
                        actmap.scheduleAfter("animate", "endTranslate");
					}
                }
			}
		} //
	} // end of inner class DemoController
	
    public class DemoRendererFactory implements RendererFactory {
        private Renderer nodeRenderer1;
        private Renderer nodeRenderer2;
        private Renderer edgeRenderer;
        public DemoRendererFactory(Renderer nr1, Renderer nr2, Renderer er) {
            nodeRenderer1 = nr1;
            nodeRenderer2 = nr2;
            edgeRenderer = er;
        } //
        public Renderer getRenderer(VisualItem item) {
            if ( item instanceof NodeItem ) {
                NodeItem n = (NodeItem)item;
                NodeItem p = (NodeItem)n.getParent();
                
                double d = Double.MAX_VALUE;
                
                Point2D nl = n.getLocation();
                if ( p != null) {
                    d = Math.min(d,nl.distance(p.getLocation()));
                    int idx = p.getChildIndex(n);
                    NodeItem b;
                    if ( idx > 0 ) {
                        b = (NodeItem)p.getChild(idx-1);
                        d = Math.min(d,nl.distance(b.getLocation()));
                    }
                    if ( idx < p.getChildCount()-1 ) {
                        b = (NodeItem)p.getChild(idx+1);
                        d = Math.min(d,nl.distance(b.getLocation()));
                    }
                }
                if ( n.getChildCount() > 0 ) {
                    NodeItem c = (NodeItem)n.getChild(0);
                    d = Math.min(d,nl.distance(c.getLocation()));
                }
                
                if ( d > 15 ) {
                    return nodeRenderer1;
                } else {
                    return nodeRenderer2;
                }
            } else if ( item instanceof EdgeItem ) {
                return edgeRenderer;
            } else {
                return null;
            }
        } //
    } // end of inner class DemoRendererFactory
	
    public class DemoColorFunction extends ColorFunction {
	    private int  thresh = 5;
	    private Color graphEdgeColor = Color.LIGHT_GRAY;
	    private Color nodeColors[];
	   	private Color edgeColors[];
	   
	   	public DemoColorFunction() {
	   		nodeColors = new Color[thresh];
	   	    edgeColors = new Color[thresh];
	   	    for ( int i = 0; i < thresh; i++ ) {
	   	    	double frac = i / ((double)thresh);
	   	    	nodeColors[i] = ColorLib.getIntermediateColor(Color.RED, Color.BLACK, frac);
	   	    	edgeColors[i] = ColorLib.getIntermediateColor(Color.RED, Color.BLACK, frac);
	   	    }
	   	} //
	   
	   	public Paint getFillColor(VisualItem item) {
	   		if ( item instanceof NodeItem ) {
	   			return Color.WHITE;
	   		} else if ( item instanceof AggregateItem ) {
	   			return Color.LIGHT_GRAY;
	   		} else if ( item instanceof EdgeItem ) {
	   			return getColor(item);
	   		} else {
	   			return Color.BLACK;
	   		}
	   	} //
	   
		public Paint getColor(VisualItem item) {
			if (item instanceof NodeItem) {
                int d = ((NodeItem)item).getDepth();
				return nodeColors[Math.min(d,thresh-1)];
			} else if (item instanceof EdgeItem) {
				EdgeItem e = (EdgeItem) item;
				if ( e.isTreeEdge() ) {
					int d, d1, d2;
                    d1 = ((NodeItem)e.getFirstNode()).getDepth();
                    d2 = ((NodeItem)e.getSecondNode()).getDepth();
                    d = Math.max(d1, d2);
					return edgeColors[Math.min(d,thresh-1)];
				} else {
					return graphEdgeColor;
				}
			} else {
				return Color.BLACK;
			}
		} //
   } // end of inner class DemoColorFunction

} // end of classs HyperbolicTreeDemo
