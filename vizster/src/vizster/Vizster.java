package vizster;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.sql.SQLException;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import vizster.color.BrowsingColorFunction;
import vizster.color.ComparisonColorFunction;
import vizster.render.VizsterRendererFactory;
import vizster.util.ProfilePanel;
import vizster.util.SearchPanel;
import vizster.util.VizsterMenuBar;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.action.Action;
import edu.berkeley.guir.prefuse.action.ActionMap;
import edu.berkeley.guir.prefuse.action.ActionSwitch;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.filter.FisheyeGraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderListener;
import edu.berkeley.guir.prefuse.graph.external.GraphLoader;
import edu.berkeley.guir.prefuse.util.DefaultFocusSet;
import edu.berkeley.guir.prefuse.util.FocusSet;
import edu.berkeley.guir.prefuse.util.KeywordSearchFocusSet;
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

/**
 * An application for visual exploration of the friendster social networking
 * service.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class Vizster extends JFrame {

    // default starting friendster user id
    private static final String DEFAULT_START_UID = "186297";
    
    // keys for additional focus sets
    public static final String CLICK_KEY  = "clicked";
    public static final String MOUSE_KEY  = "moused";
    public static final String SEARCH_KEY = "search";
    
    // modes the interface can be in
    public static final int BROWSE_MODE = 0;
    public static final int COMPARE_MODE = 1;
    
    // prefuse architecture components
    private ItemRegistry registry;
    private ActionList forces, filter;
    private VizsterDBLoader loader;
    private VizsterRendererFactory renderers;
    private ForceSimulator fsim;
    private ActionMap actionMap;
    
    // ui components
    private Display display;
    private ProfilePanel profile;
    private SearchPanel searcher;
    
    // number of login attempts before application exits
    private int loginRetries = 5;
    
    /**
     * Launches the Vizster application
     * @param argv this app takes one optional argument - a friendster user id
     *  to show upon launch.
     */
    public static void main(String[] argv) {
        VizsterLib.setLookAndFeel();
        String startUID = argv.length > 0 ? argv[0] : DEFAULT_START_UID;
        new Vizster(startUID);
    } //
    
    /**
     * Construct a new Vizster application instance.
     */
    public Vizster() {
        this(DEFAULT_START_UID);
    } //
    
    /**
     * Construct a new Vizster application instance.
     * @param startUID the friendster user id to show first
     */
    public Vizster(String startUID) {
        super("Vizster");
        
        // initialize empty graph and registry
        Graph g = new DefaultGraph();
        registry = new ItemRegistry(g);
        
        // initialize focus handling
        // -We already get a default focus set, use it for centered nodes
        // -Add another set for nodes that are clicked, to show profiles
        // -Add another set for nodes moused-over, providing highlights
        FocusManager fmanager = registry.getFocusManager();
        fmanager.putFocusSet(CLICK_KEY, new DefaultFocusSet());
        fmanager.putFocusSet(MOUSE_KEY, new DefaultFocusSet());
        final KeywordSearchFocusSet searchSet = new KeywordSearchFocusSet();
        fmanager.putFocusSet(SEARCH_KEY, searchSet);
        
        // create a new loader to talk to the database
        loader = new VizsterDBLoader(registry, VizsterDBLoader.ALL_COLUMNS);
        // register update listener with graph loader
        loader.addGraphLoaderListener(new GraphLoaderListener() {
            public void entityLoaded(GraphLoader loader, Entity e) {
                filter.runNow();
            } //
            public void entityUnloaded(GraphLoader loader, Entity e) {
                filter.runNow();
            } //
        });
        
        // initialize user interface components
        // set up the primary display
        display = new VizsterDisplay(registry);
        display.setSize(700,650);
        // create the panel which shows friendster profile data
        profile = new ProfilePanel(this);
        // create the search panel
        searcher = new SearchPanel(this);
        
        // initialize the prefuse renderers and action lists
        initPrefuse();
        
        // attempt to login to database
        if ( !VizsterLib.authenticate(this, loginRetries) ) {
            System.exit(0); // user canceled login so exit
        }
        
        // load the initial profile from the database
        Node r = null;
        try {
            r = loader.getProfileNode(startUID);
        } catch ( SQLException e ) {
            e.printStackTrace();
            VizsterLib.profileLoadError(this, startUID);
            System.exit(1);
        }
        // add initial node to the graph, and set as focus
        g.addNode(r);
        registry.getDefaultFocusSet().set(r);
        fmanager.getFocusSet(CLICK_KEY).set(r);
        
        // initialize the display's control listeners
        display.addControlListener(new FocusControl(2));
        display.addControlListener(new FocusControl(1, CLICK_KEY));
        display.addControlListener(new FocusControl(0, MOUSE_KEY));
        display.addControlListener(new NeighborHighlightControl());
        display.addControlListener(new DragControl(false, true));
        display.addControlListener(new PanControl(false));
        
        // add a zoom control that works everywhere
        ZoomControl zc = new ZoomControl(false);
        display.addMouseListener(zc);
        display.addMouseMotionListener(zc);
        
        // set up the JFrame
        setJMenuBar(new VizsterMenuBar(this));
        initUI(); pack();
        setVisible(true);
        
        // wait until graphics are available
        while ( display.getGraphics() == null );
        filter.runNow();
        forces.runNow();
    } //
    
    private void initUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // recenter the display upon resizing
        display.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                centerDisplay();
            } //
        });
        
        JScrollPane scroller = new JScrollPane(profile);
        scroller.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        Dimension pd = profile.getPreferredSize();
        scroller.setPreferredSize(new Dimension(300,pd.height));
        
        searcher.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        
        JPanel main = new JPanel(new BorderLayout());
        main.add(display, BorderLayout.CENTER);
        main.add(searcher, BorderLayout.SOUTH);
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                false, main, scroller);
        split.setDividerSize(10);
        split.setResizeWeight(1.0);
        split.setOneTouchExpandable(true);
        
        getContentPane().add(split);
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
        renderers = new VizsterRendererFactory(display);
        renderers.setBrowseMode(true);
        registry.setRendererFactory(renderers);
        
        // set up actions
        FisheyeGraphFilter      feyeFilter = new FisheyeGraphFilter(-1);
        BrowsingColorFunction   bcolorFunc = new BrowsingColorFunction();
        ComparisonColorFunction ccolorFunc = new ComparisonColorFunction();
        ActionSwitch colorSwitch = new ActionSwitch(
                new Action[] {bcolorFunc, ccolorFunc}, 0);
        
        actionMap = new ActionMap();
        actionMap.put("filter", feyeFilter);
        actionMap.put("browseColors", bcolorFunc);
        actionMap.put("compareColors", ccolorFunc);
        actionMap.put("colorSwitch", colorSwitch);
        
        // initialize the force simulator
        fsim = new ForceSimulator();
        fsim.addForce(new NBodyForce(-1.5f, -1f, 0.9f));
        final SpringForce springF = new SpringForce(2E-5f, 150f);
        fsim.addForce(springF);
        fsim.addForce(new DragForce(-0.005f));
        
        // initialize the filter action list
        filter = new ActionList(registry);
        filter.add(feyeFilter);
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
        forces.add(new ForceDirectedLayout(fsim, false, false) {
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
        });
        forces.add(colorSwitch);
        forces.add(new RepaintAction());
        
        // initialize focus listeners
        FocusSet defaultSet = registry.getDefaultFocusSet();
        defaultSet.addFocusListener(new FocusListener() {
            public void focusChanged(FocusEvent e) {
                // unfix previous center item
                NodeItem n = registry.getNodeItem((Node)e.getFirstRemoved());
                if ( n != null ) n.setFixed(false);
                
                centerDisplay(); // center display on the new focus
                filter.runNow(); // refilter
                loader.loadNeighbors((Node)e.getFirstAdded());
            } //
        });
        
        FocusManager fmanager = registry.getFocusManager();
        FocusSet clickedSet = fmanager.getFocusSet(CLICK_KEY);
        clickedSet.addFocusListener(new FocusListener() {
            public void focusChanged(FocusEvent e) {
                // update profile panel to show new focus
                Node f = (Node)e.getFirstAdded();
                profile.updatePanel(f);
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
    
    public VizsterDBLoader getLoader() {
        return loader;
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
    
} // end of class Vizster
