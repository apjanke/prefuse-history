package enronic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.action.Action;
import edu.berkeley.guir.prefuse.action.ActionMap;
import edu.berkeley.guir.prefuse.action.ActionSwitch;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.animate.PolarLocationAnimator;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.focus.DefaultFocusSet;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.focus.PrefixSearchFocusSet;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderListener;
import edu.berkeley.guir.prefuse.graph.external.GraphLoader;
import edu.berkeley.guir.prefusex.community.CommunitySet;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.FruchtermanReingoldLayout;
import enronic.color.BrowsingColorFunction;
import enronic.color.ComparisonColorFunction;
import enronic.data.EnronicDBLoader;
import enronic.render.EnronicRendererFactory;
import enronic.util.EnronicMenuBar;
import enronic.util.Legend;
import enronic.util.MessagePanel;
import enronic.util.ProfilePanel;
import enronic.util.SearchPanel;

/**
 * An application for visual exploration of the friendster social networking
 * service.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class Enronic extends JFrame {

    // default starting friendster user id
    public static final String DEFAULT_START_UID = "1490";
    public static final String ID_FIELD = "uid";
    
    // keys for additional focus sets
    public static final String CLICK_KEY  = "clicked";
    public static final String MOUSE_KEY  = "moused";
    public static final String SEARCH_KEY = "search";
    public static final String COMMUNITY_KEY = "community";
    
    // modes the interface can be in
    public static final int BROWSE_MODE = 0;
    public static final int COMPARE_MODE = 1;
    
    // prefuse architecture components
    private ItemRegistry registry;
    private ActionList redraw, forces, altForces, altAnimate, filter;
    private EnronicDBLoader loader;
    private boolean useDatabase;
    private String datafile;
    private EnronicRendererFactory renderers;
    private ForceSimulator fsim;
    private ActionMap actionMap;
    
    // control if layout remains animated
    private boolean animate = true;
    
    // ui components
    private Display display;
    private MessagePanel messageview;
    private ProfilePanel profile;
    private SearchPanel searcher;
    
    // number of login attempts before application exits
    private int loginRetries = 5;
    
    /**
     * Launches the enronic application
     * @param argv this app takes one optional argument - a friendster user id
     *  to show upon launch.
     */
    public static void main(String[] argv) {
        EnronicLib.setLookAndFeel();
        String startUID = DEFAULT_START_UID;
        String file = argv.length > 0 ? argv[0] : "enron.xml";
        new Enronic(startUID, file);
    } //
    
    /**
     * Construct a new enronic application instance.
     */
    public Enronic() {
        this(DEFAULT_START_UID, null);
    } //
    
    /**
     * Construct a new enronic application instance.
     * @param startUID the user id to show first
     */
    public Enronic(String startUID) {
        this(startUID, null);
    } //
    
    /**
     * Construct a new enronic application instance.
     * @param startUID the user id to show first
     * @param datafile the data file to use, if null, 
     *   a connection dialog for a database will be provided 
     */
    public Enronic(String startUID, String datafile) {
        super("Enron Corpus Viewer");
        
        // determine input method
        this.datafile = datafile;
        this.useDatabase = true;
        
        // create the registry
        registry = new ItemRegistry(new DefaultGraph());
        
        // initialize focus handling
        // -We already get a default focus set, use it for double-clicked nodes
        // -Add another set for nodes that are single-clicked, to show profiles
        // -Add another set for nodes moused-over, providing highlights
        // -Add another set for keyword search hits
        FocusManager fmanager = registry.getFocusManager();
        fmanager.putFocusSet(CLICK_KEY, new DefaultFocusSet());
        fmanager.putFocusSet(MOUSE_KEY, new DefaultFocusSet());
        fmanager.putFocusSet(COMMUNITY_KEY, new CommunitySet());
        final PrefixSearchFocusSet searchSet = new PrefixSearchFocusSet();
        fmanager.putFocusSet(SEARCH_KEY, searchSet);
        
        if ( useDatabase ) {
	        // create a new loader to talk to the database if needed
	        loader = new EnronicDBLoader(registry, EnronicDBLoader.ALL_COLUMNS);
	        // register update listener with graph loader
	        loader.addGraphLoaderListener(new GraphLoaderListener() {
	            public void entityLoaded(GraphLoader loader, Entity e) {
	                filter.runNow(); forces.runNow();
	            } //
	            public void entityUnloaded(GraphLoader loader, Entity e) {
	                filter.runNow(); forces.runNow();
	            } //
	        });
        }
        
        // initialize user interface components
        // set up the primary display
        display = new EnronicDisplay(registry);
        display.setSize(700,650);
        // create the panel to show e-mail messages
        messageview = new MessagePanel(this);
        // create the panel which shows friendster profile data
        profile = new ProfilePanel(this);
        // create the search panel
        searcher = new SearchPanel(this);
        
        // initialize the prefuse renderers and action lists
        initPrefuse();
        
        // initialize the display's control listeners
        Class[] types = new Class[] { NodeItem.class, EdgeItem.class };
        display.addControlListener(new FocusControl(2));
        display.addControlListener(new FocusControl(1, CLICK_KEY, types));
        display.addControlListener(new FocusControl(0, MOUSE_KEY, types));
        display.addControlListener(new NeighborHighlightControl(redraw));
        display.addControlListener(new DragControl(true, true));
        display.addControlListener(new ControlAdapter() {
            boolean f1, f2, h1, h2;
            public void itemEntered(VisualItem item, MouseEvent e) {
                if ( item instanceof EdgeItem ) {
                    EdgeItem edge = (EdgeItem)item;
                    NodeItem n1 = (NodeItem)edge.getFirstNode();
                    NodeItem n2 = (NodeItem)edge.getSecondNode();
                    f1 = n1.isFixed(); h1 = n1.isHighlighted();
                    f2 = n2.isFixed(); h2 = n2.isHighlighted();
                    n1.setFixed(true); n1.setHighlighted(true);
                    n2.setFixed(true); n2.setHighlighted(true);
                    redraw();
                }
            }
        	public void itemExited(VisualItem item, MouseEvent e) {
        	    if ( item instanceof EdgeItem ) {
                    EdgeItem edge = (EdgeItem)item;
                    NodeItem n1 = (NodeItem)edge.getFirstNode();
                    NodeItem n2 = (NodeItem)edge.getSecondNode();
                    n1.setFixed(f1); n1.setHighlighted(h1);
                    n2.setFixed(f2); n2.setHighlighted(h2);
                    redraw();
                } 
        	}
        });
        display.addControlListener(new PanControl(true));
        
        // add a zoom control that works everywhere
        ZoomControl zc = new ZoomControl(true);
        display.addMouseListener(zc);
        display.addMouseMotionListener(zc);
        
        // set up the JFrame
        setJMenuBar(new EnronicMenuBar(this));
        initUI(); pack();
        setVisible(true);
        
        Legend legend = new Legend(this);
        legend.setVisible(true);
        
        // wait until graphics are available
        while ( display.getGraphics() == null );
        
        // load the network data
        loadGraph(datafile, startUID);
    } //
    
    public void loadGraph(String datafile, String startUID) {
        // stop any running actions
        forces.cancel();
        
        // alter settings as needed
        useDatabase = (datafile==null);
        if ( !EnronicLib.authenticate(this, loginRetries) ) {
            System.exit(0); // user canceled login so exit
        }
        
        // load graph
        try {
	        if ( useDatabase ) {
	            loader.loadGraph();
	        } else {
	            Graph g = EnronicLib.loadGraph(datafile);
	            registry.setGraph(g);
	            loader.updateCache();
	        }
        } catch ( Exception e ) {
            e.printStackTrace();
            EnronicLib.defaultError(this, "Couldn't load input graph.");
            return;
        }        
        
        // retrieve the initial profile and set as focus
        Node r = getInitialNode(startUID);
        registry.getDefaultFocusSet().set(r);
        registry.getFocusManager().getFocusSet(CLICK_KEY).set(r);
        
        filter.runNow();
        if ( animate ) {
            forces.runNow();
        } else {
            runStaticLayout();
        }
    } //
    
    private Node getInitialNode(String uid) {
        Node r = null;
        if ( useDatabase ) {
	        try {
	            r = loader.getProfileNode(uid);
	        } catch ( SQLException e ) {
	            e.printStackTrace();
	            EnronicLib.profileLoadError(this, uid);
	            System.exit(1);
	        }
	        // add initial node to the graph
	        registry.getGraph().addNode(r);
        } else {
            if ( uid == null ) {
                r = GraphLib.getMostConnectedNodes(registry.getGraph())[0]; 
            } else {
                Node[] matches = GraphLib.search(registry.getGraph(), ID_FIELD, uid);
                if ( matches.length > 0 ) {
                    r = matches[0];
                } else {
                    r = GraphLib.getMostConnectedNodes(registry.getGraph())[0];
                }
            }
        }
        return r;
    } //
    
    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // recenter the display upon resizing
//        display.addComponentListener(new ComponentAdapter() {
//            public void componentResized(ComponentEvent e) {
//                centerDisplay();
//            } //
//        });
 
        searcher.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        
        JPanel main = new JPanel(new BorderLayout());
        main.add(display, BorderLayout.CENTER);
        main.add(searcher, BorderLayout.SOUTH);
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                false, main, messageview);
        split.setDividerSize(10);
        split.setResizeWeight(0.0);
        split.setOneTouchExpandable(true);
        
        getContentPane().add(split);
        //getContentPane().add(main);
    } //
    
    public void centerDisplay() {
        Iterator iter = registry.getDefaultFocusSet().iterator();
        if ( iter.hasNext() ) {
            Node f = (Node)iter.next();
            NodeItem n = registry.getNodeItem(f);
            if ( n == null ) {
                n = registry.getNodeItem(f,true);
                filter.runNow();
            }
            display.animatePanToAbs(n.getLocation(), 2000);
        }
    } //
    
    public void unzoom() {
        int x = display.getWidth()/2;
        int y = display.getHeight()/2;
        display.animateZoom(new Point2D.Float(x,y),0,2000);
    } //
    
    private void initPrefuse() {
        // initialize renderers
        renderers = new EnronicRendererFactory(display);
        renderers.setBrowseMode(true);
        registry.setRendererFactory(renderers);
        
        // initialize the force simulator
        fsim = new ForceSimulator();
        fsim.addForce(new NBodyForce(-1.5f, -1f, 0.9f));
        final SpringForce springF = new SpringForce(2E-5f, 150f);
        fsim.addForce(springF);
        fsim.addForce(new DragForce(-0.005f));
        
        // set up actions
        //FisheyeGraphFilter      feyeFilter = new FisheyeGraphFilter(-1);
        GraphFilter             feyeFilter = new GraphFilter();
        ConnectivityFilter      connFilter = new ConnectivityFilter();
        BrowsingColorFunction   bcolorFunc = new BrowsingColorFunction();
        ComparisonColorFunction ccolorFunc = new ComparisonColorFunction();
        
        Layout frLayout = new FruchtermanReingoldLayout(400);
        Layout fdLayout = new ForceDirectedLayout(fsim, false, false) {
            private float normal = 2E-5f;
            private float slack1 = 2E-6f;
            private float slack2 = 2E-7f;
            protected float getSpringLength(NodeItem n1, NodeItem n2) {
                int minE = Math.min(n1.getEdgeCount(),n2.getEdgeCount());
                double doi = Math.max(n1.getDOI(), n2.getDOI());
                return ( minE == 1 ? 75.f : (doi==0? 200.f : 100.f));
            } //
            protected float getSpringCoefficient(NodeItem n1, NodeItem n2) {
                int maxE = Math.max(n1.getEdgeCount(),n2.getEdgeCount());
                if ( maxE <= 80 )
                    return normal;
                else if ( maxE <= 180 )
                    return slack1;
                else
                    return slack2;
            } //
        };
        
        ActionSwitch colorSwitch = new ActionSwitch(
                new Action[] {bcolorFunc, ccolorFunc}, 0);
        
        actionMap = new ActionMap();
        actionMap.put("filter", feyeFilter);
        actionMap.put("connectivity", connFilter);
        actionMap.put("browseColors", bcolorFunc);
        actionMap.put("compareColors", ccolorFunc);
        actionMap.put("colorSwitch", colorSwitch);
        actionMap.put("dynamicForces", fdLayout);
        actionMap.put("staticForces", frLayout);
        
        // initialize basic recoloring-drawing action list
        redraw = new ActionList(registry, 0);
        redraw.add(colorSwitch);
        redraw.add(new RepaintAction());
        
        // initialize the filter action list
        filter = new ActionList(registry);
        filter.add(feyeFilter);
        filter.add(connFilter);
        filter.add(colorSwitch);
        
        // initilaize the forces action list
        forces = new ActionList(registry,-1,20);
        forces.add(new AbstractAction() {
            public void run(ItemRegistry registry, double frac) {                
                Iterator iter = registry.getDefaultFocusSet().iterator();
                while ( iter.hasNext() ) {
                    Entity e = (Entity)iter.next();
                    if (e instanceof Node) {
                        NodeItem item = registry.getNodeItem((Node)e);
                        if ( item != null ) item.setFixed(true);
                    }
                }
            } //
        });
        forces.add(fdLayout);
        forces.add(redraw);
        
        // initialize action list for an alternate, static layout
        altForces = new ActionList(registry, 0);
        altForces.add(frLayout);
        altForces.add(colorSwitch);
        
        altAnimate = new ActionList(registry, 2000, 20);
        altAnimate.setPacingFunction(new SlowInSlowOutPacer());
        altAnimate.add(new PolarLocationAnimator());
        altAnimate.add(new RepaintAction());
        
        // initialize focus listeners
        FocusSet defaultSet = registry.getDefaultFocusSet();
        defaultSet.addFocusListener(new FocusListener() {
            public void focusChanged(FocusEvent e) {
                // unfix previous center item
                NodeItem n = registry.getNodeItem((Node)e.getFirstRemoved());
                if ( n != null ) n.setFixed(false);
                
                centerDisplay(); // center display on the new focus
                filter.runNow(); // refilter
                setAnimate(true);
                if ( useDatabase )
                    loader.loadNeighbors((Node)e.getFirstAdded());
            } //
        });
        
        FocusManager fmanager = registry.getFocusManager();
        FocusSet clickedSet = fmanager.getFocusSet(CLICK_KEY);
        clickedSet.addFocusListener(new FocusListener() {
            public void focusChanged(FocusEvent e) {
                // update profile panel to show new focus
                Entity ent = e.getFirstAdded();
                if ( ent instanceof Node ) {
                    Node n = (Node)ent;
                    int id1 = Integer.parseInt(n.getAttribute("personid"));
                    List msgs = loader.getMessages(id1);
                    messageview.setContent(n,null,msgs);
                } else if ( ent instanceof Edge ) {
                    Edge edge = (Edge)ent;
                    Node n1 = edge.getFirstNode();
                    Node n2 = edge.getSecondNode();
                    int id1 = Integer.parseInt(n1.getAttribute("personid"));
                    int id2 = Integer.parseInt(n2.getAttribute("personid"));
                    List msgs = loader.getMessages(id1,id2);
                    messageview.setContent(n1,n2,msgs);
                }
            } //
        });
        
        FocusSet searcher = fmanager.getFocusSet(SEARCH_KEY);
        searcher.addFocusListener(new FocusListener() {
            public void focusChanged(FocusEvent e) {
                redraw();
            } //
        });
    } //
    
    // ========================================================================
    // == ACCESSOR METHODS ====================================================
    
    public int getLoginRetries() {
        return loginRetries;
    } //
    
    public ItemRegistry getItemRegistry() {
        return registry;
    } //
    
    public Display getDisplay() {
        return display;
    } //
    
    public EnronicDBLoader getLoader() {
        return loader;
    } //
    
    public Action getAction(String name) {
        return actionMap.get(name);
    } //
    
    public ForceSimulator getForceSimulator() {
        return fsim;
    } //
    
    public void setMode(int mode) {
        if ( mode != BROWSE_MODE && mode != COMPARE_MODE )
            return;
        boolean b = (mode == BROWSE_MODE);
        Color bg = b ? Color.WHITE : Color.BLACK;
        Color fg = b ? Color.BLACK : Color.WHITE;
        display.setBackground(bg);
        display.setForeground(fg);
        searcher.setBackground(bg);
        searcher.setForeground(fg);
        ActionSwitch as = (ActionSwitch)actionMap.get("colorSwitch");
        as.setSwitchValue(b?0:1);
        renderers.setBrowseMode(b);
    } //
    
    public ComparisonColorFunction getComparisonColorFunction() {
        return (ComparisonColorFunction)actionMap.get("compareColors");
    } //
    
    public BrowsingColorFunction getBrowsingColorFunction() {
        return (BrowsingColorFunction)actionMap.get("browseColors");
    } //
    
    public boolean isAnimate() {
        return animate;
    } //
    
    public void filter() {
        filter.runNow();
    } //
    
    public void redraw() {
        if ( !animate )
            redraw.runNow();
    } //
    
    public void setAnimate(boolean b) {
        if ( b ) {
            forces.runNow();
        } else {
            forces.cancel();
        }
        animate = b;
    } //
    
    public void runStaticLayout() {
        altAnimate.runAfter(altForces);
        altForces.runNow();
    } //
    
} // end of class enronic
