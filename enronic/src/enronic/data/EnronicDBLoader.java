package enronic.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.DefaultNode;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderListener;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderMulticaster;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphWriter;

/**
 * Custom database loader that allows for more fine-grain control over
 * which data is loaded from the database. This is much more memory
 * efficient that using the alternative enronicDatabaseLoader class.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class EnronicDBLoader {

    /**
     * The default set of profile entries to load
     */
    public static final String[] DEFAULT_COLUMNS =
        {"uid", "name", "location", "age", "photourl"};
    
    /**
     * A set of all the available friendster profile attributes
     */
//    public static final String[] ALL_COLUMNS =
//        {"uid", "name", "nfriends", "age", "gender", "status", 
//         "interested_in", "preference", "location", "hometown",
//         "occupation", "interests", "music", "books", "tvshows",
//         "movies", "membersince", "lastlogin", "lastmod", "about",
//         "want_to_meet", "photourl"};
    public static final String[] ALL_COLUMNS =
    {"personid", "email", "name", "title"};
    
    //protected String m_keyField = "uid";
    protected String m_keyField = "personid";
    //protected int m_maxSize = 1000000; //5000;
    //protected LinkedHashMap m_cache = new LinkedHashMap(m_maxSize,.75f,true) {
    //    public boolean removeEldestEntry(Map.Entry eldest) {
    //        return evict((Entity)eldest.getValue());
    //    }
    //};
    protected HashMap m_cache = new HashMap();
    protected HashMap m_mcache = new HashMap();
    protected GraphLoaderListener m_listener;
    protected Graph m_graph;
    protected ItemRegistry m_registry;
    
    private EnronicQueryFactory m_queryF = new EnronicQueryFactory();
    private EnronicDBParams m_dbParams;
    private final String m_columns[];
    
    protected String m_profileQuery;
    protected String m_neighborQuery;
    protected String m_edgeQuery;
    
    private Connection m_db;
    private PreparedStatement m_ps, m_ns, m_es;
    private PreparedStatement m_ms, m_mids1, m_mids2, m_rs, m_cs;
    
    /**
     * Creates a new loader to load items from a database retrieving a default
     * set of profile entries for each loaded profile.
     * @param registry the ItemRegistry to load to
     */
    public EnronicDBLoader(ItemRegistry registry) {
        this(registry, DEFAULT_COLUMNS);
    } //
    
    public EnronicDBLoader(ItemRegistry registry, String columns[]) {
        //this(new enronicDBParams("profiles","uid","graph","uid1","uid2"),registry,columns);
        this(new EnronicDBParams("people","personid","edges","senderid","recipientid"),registry,columns);
    }
    
    /**
     * Creates a new loader to load items from a database retrieving the given
     * set of profile entries for each loaded profile.
     * @param registry the ItemRegistry to load to
     * @param columns the names of the profile entries to include
     */
    public EnronicDBLoader(EnronicDBParams params, ItemRegistry registry, String columns[])  {
        m_dbParams = params;
        m_registry = registry;
        m_graph = registry.getGraph();
        m_columns = columns;
        try {
            setProfileQuery(m_queryF.getProfileQuery(m_dbParams));
            setNeighborQuery(m_queryF.getNeighborQuery(m_dbParams));
            setEdgeQuery(m_queryF.getEdgeQuery(m_dbParams));
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    } //
    
    public void updateCache() {
        m_graph = m_registry.getGraph();
        m_cache.clear();
        Iterator nodes = m_graph.getNodes();
        while ( nodes.hasNext() ) {
            Node n = (Node)nodes.next();
            String id = n.getAttribute(m_keyField);
            m_cache.put(id,n);
        }
    }
    
    public String[] getColumns() {
        return m_columns;
    } //
    
    Thread queryThread;
    
    public void loadNeighbors(final Node n) {
        Runnable r = new Runnable() {
            public void run() {
                prepareQuery(m_ns, n);
                loadNodes(m_ns, n);
                prepareQuery(m_es, n);
                loadEdges(m_es, n);
            }
        };
        if ( queryThread != null && queryThread.isAlive() ) {
            queryThread.stop();
            System.out.println("Stopped query thread: "+queryThread);
        }
        Thread t = new Thread(r);
        t.setPriority(Thread.MIN_PRIORITY);
        queryThread = t;
        t.start();
    } //
    
    private void loadNodes(PreparedStatement s, Node src) {
        try {
            ResultSet rs = s.executeQuery();
            while ( rs.next() )
                loadNode(rs, src);
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    } //
    
    public Node loadNode(ResultSet rs, Node src) throws SQLException {
        Node node = null;
        for ( int i=0; i<m_columns.length; i++ ) {
            String value = rs.getString(m_columns[i]);
            if ( i == 0 ) {
                Node n = (Node)m_cache.get(value);
                if ( n != null ) return n;
                node = new DefaultNode();
            }
            if ( value != null ) {
                value = value.replaceAll("\r","");
                value = value.replaceAll("\"","");
                node.setAttribute(m_columns[i], value);
            }
        }
        String label = node.getAttribute("name");
        if ( label == null || label.equals("") ) {
            label = node.getAttribute("email");
        }
        node.setAttribute("label", label);
        foundNode(src, node, null);
        return node;
    } //
    
    protected void foundNode(Node src, Node n, Edge e) {
        boolean inCache = false;
        String key = n.getAttribute(m_keyField);
        if ( m_cache.containsKey(key) ) {
            // switch n reference to original loaded version 
            n = (Node)m_cache.get(key);
            inCache = true;
        } else {
            m_cache.put(key, n);
        }
        
        if (e == null && src != null )
            e = new DefaultEdge(src, n, m_graph.isDirected());
        
        synchronized ( m_registry ) {
            m_graph.addNode(n);
            if ( src != null )
                m_graph.addEdge(e);
        }
        
        if ( m_listener != null && !inCache )
            m_listener.entityLoaded(null,n);
    } //
    
    private void loadEdges(PreparedStatement s, Node src) {
        try {
            ResultSet rs = s.executeQuery();
            while ( rs.next() )
                loadEdge(rs);
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    } //
    
    private int i = 0;
    
    public Edge loadEdge(ResultSet rs) throws SQLException {
        String id1 = rs.getString(m_dbParams.getGraphSrcIDField());
        String id2 = rs.getString(m_dbParams.getGraphDstIDField());
        Node source = (Node)m_cache.get(id1);
        Node target = (Node)m_cache.get(id2);
        
        if ( source == null || target == null )
            System.err.println(++i+"\t"+id1+"\t"+id2+"\t"+(source!=null)+"\t"+(target!=null));
        
        if ( source == null || target == null ) return null;
        Edge e = new DefaultEdge(source, target, m_graph.isDirected());
        loadEdgeAttributes(rs, e);

        boolean add = false;
        synchronized ( m_registry ) {
            add = m_graph.addEdge(e);
        }
        if ( !add ) {
            updateExistingEdge(e);
        }
        if ( add && m_listener != null ) {
            m_listener.entityLoaded(null,e);
        }
        return e;
    } //
    
    private String[] attr = {"base", "cat01","cat02","cat03","cat04","cat05",
            "cat06","cat07","cat08","cat09","cat10","cat11",
            "cat12","cat13"};
    
    private void updateExistingEdge(Edge e) {
        Node n1 = e.getFirstNode();
        Node n2 = e.getSecondNode();
        Edge e2 = n1.getEdge(n2);
        for ( int i=0; i < attr.length; i++ ) {
            String s1 = e.getAttribute(attr[i]);
            String s2 = e2.getAttribute(attr[i]);
            int r = Integer.parseInt(s1) + Integer.parseInt(s2);
            e2.setAttribute(attr[i],String.valueOf(r));
        }
    } //
    
    private void loadEdgeAttributes(ResultSet rs, Edge e ) throws SQLException {
        for ( int i=0; i < attr.length; i++ ) {
            String s = rs.getString(attr[i]);
            e.setAttribute(attr[i],s);
        }
    } //
    
    /**
     * Returns the node associated with the given user id value, loading it
     * from the database as necessary
     * @param uid the user id of the profile to retrieve
     * @return a Node representing the requested profile
     * @throws SQLException if an error occurs while talking to the database
     */
    public Node getProfileNode(String uid) throws SQLException {
        Node node = null;
        String sid = String.valueOf(uid);
        
        // return the node if it's already in the cache
        if ( (node=(Node)m_cache.get(sid)) != null )
            return node;
        
        // otherwise load the node from the database
        prepareQuery(m_ps, Integer.parseInt(uid));
        ResultSet rs = m_ps.executeQuery(); rs.first();
        node = loadNode(rs,null);
        return node;
    } //
    
    public void loadGraph() throws SQLException {
        System.out.print("Loading graph from database...");
        PreparedStatement ns = 
            m_db.prepareStatement(m_queryF.getAllProfilesQuery(m_dbParams));
        PreparedStatement es = 
            m_db.prepareStatement(m_queryF.getAllEdgesQuery(m_dbParams));
        loadNodes(ns, null);
        loadEdges(es, null);
        
        HashSet ids = new HashSet();
        Iterator edges = m_graph.getEdges();
        while ( edges.hasNext() ) {
            Edge e = (Edge)edges.next();
            ids.add(e.getFirstNode().getAttribute(m_keyField));
            ids.add(e.getSecondNode().getAttribute(m_keyField));
        }
        ArrayList nodeStore = new ArrayList();
        Iterator nodes = m_graph.getNodes();
        while ( nodes.hasNext() ) {
            Node n = (Node)nodes.next();
            String id = n.getAttribute(m_keyField);
            if ( !ids.contains(id) ) {
                nodeStore.add(n);
            }
        }
        nodes = nodeStore.iterator();
        while ( nodes.hasNext() ) {
            m_graph.removeNode((Node)nodes.next());
        }
        System.out.println("\tDONE");
    } //
    
    public List getMessages(int id1, int id2) {
        List list = new ArrayList();
        try {
            prepareQuery(m_mids2,id1,id2);
            ResultSet rs = m_mids2.executeQuery();
            while ( rs.next() ) {
                int msgid = rs.getInt("messageid");
                list.add(getMessage(msgid));
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        Collections.sort(list);
        return list;
    } //
    
    public List getMessages(int id1) {
        List list = new ArrayList();
        try {
            prepareQuery(m_mids1,id1);
            ResultSet rs = m_mids1.executeQuery();
            while ( rs.next() ) {
                int msgid = rs.getInt("messageid");
                list.add(getMessage(msgid));
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        Collections.sort(list);
        return list;
    } //
    
    public Message getMessage(int msgid) {
        Message msg = null;
        Integer key = new Integer(msgid);
        if ( (msg=(Message)m_mcache.get(key)) != null ) {
            return msg;
        }
        
        msg = new Message(msgid);
        try {
            prepareQuery(m_ms, msgid);
            ResultSet rs = m_ms.executeQuery();
            if ( !rs.next() ) {
                System.err.println("Empty result set!");
                return null;
            }
            msg.setSubject(rs.getString("subject"));
            msg.setBody(rs.getString("BODY"));
            msg.setTimestamp(rs.getTimestamp("messagedt"));
            msg.setSender((Node)m_cache.get(rs.getString("senderid")));
            
            prepareQuery(m_rs, msgid);
            rs = m_rs.executeQuery();
            while ( rs.next() ) {
               String id = rs.getString("personid");
               String type = rs.getString("reciptype");
               Node n = (Node)m_cache.get(id);
               if ( type.equals("to") ) {
                   msg.addTo(n);
               } else {
                   msg.addCc(n);
               }
            }
            
            prepareQuery(m_cs, msgid);
            rs = m_cs.executeQuery();
            rs.next();
            int[] cat = new int[14];
            int sum = 0;
            for ( int i=0; i<attr.length; i++ ) {
                cat[i] = rs.getInt(attr[i]);
                sum += cat[i];
            }
            sum -= cat[0];
            cat[0] -= sum;
            if ( cat[0] < 0 ) cat[0] = 0;
            msg.setCategories(cat);
            
        } catch ( SQLException ex ) {
            ex.printStackTrace();
            return null;
        }
        return msg;
    } //
    
    // ========================================================================
    // == SQL UTILITIES =======================================================
    
    protected void prepareQuery(PreparedStatement s, Node n) {
        prepareQuery(s, Integer.parseInt(n.getAttribute(m_keyField)));
    } //
    
    protected void prepareQuery(PreparedStatement s, int id) {
        try {
            s.clearParameters();
            s.setInt(1, id);
            if ( s == m_ns || s == m_mids1 )
                s.setInt(2, id);
        } catch ( SQLException e ) { e.printStackTrace(); }
    } //
    
    protected void prepareQuery(PreparedStatement s, int id1, int id2) {
        try {
            s.clearParameters();
            s.setInt(1, id1);
            s.setInt(2, id2);
            s.setInt(3, id2);
            s.setInt(4, id1);
        } catch ( SQLException e ) { e.printStackTrace(); }
    } //
    
//    public void setMaximumCacheSize(int size) {
//        m_maxSize = size;
//    } //
//    
//    public int getMaximumCacheSize() {
//        return m_maxSize;
//    } //
    
    public void addGraphLoaderListener(GraphLoaderListener l) {
        m_listener = GraphLoaderMulticaster.add(m_listener, l);
    } //
    
    public void removeGraphLoaderListener(GraphLoaderListener l) {
        m_listener = GraphLoaderMulticaster.remove(m_listener, l);
    } //
    
    public void touch(Entity e) {
        m_cache.get(e.getAttribute(m_keyField));
    } //
    
//    public boolean evict(Entity eldest) {
//        boolean b = m_cache.size()>m_maxSize;
//        if ( b && m_listener != null )
//            m_listener.entityUnloaded(null, eldest);
//        if ( b ) {
//            m_graph.removeNode((Node)eldest); 
//        }
//        return b;
//    } //
    
    public void connect(String driver, String url, String user, String password)
    throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        Class.forName(driver).newInstance();
        m_db = DriverManager.getConnection(url, user, password);
        if ( m_profileQuery != null )
            m_ps = prepare(m_profileQuery);
        if ( m_neighborQuery != null )
            m_ns = prepare(m_neighborQuery);
        if ( m_edgeQuery != null )
            m_es = prepare(m_edgeQuery);
        m_ms = prepare(m_queryF.getMessageQuery(m_dbParams));
        m_mids1 = prepare(m_queryF.getMessageIDs1Query(m_dbParams));
        m_mids2 = prepare(m_queryF.getMessageIDs2Query(m_dbParams));
        m_cs = prepare(m_queryF.getMessageCategoriesQuery(m_dbParams));
        m_rs = prepare(m_queryF.getRecipientsQuery(m_dbParams));
    } //
    
    public Connection getConnection() {
        return m_db;
    } //
    
    private PreparedStatement prepare(String query) throws SQLException {
        if ( query == null )
            throw new IllegalArgumentException("Input query must be non-null");
        if ( m_db == null )
            throw new IllegalStateException("Connection to database not yet"
                    + " established! Make sure connect() is called first.");
        
        return m_db.prepareStatement(query);
    } //
    
    public void setProfileQuery(String query) throws SQLException {
        if ( m_db != null )
            m_ps = prepare(query);
        System.out.println(query);
        m_profileQuery = query;
    } //
    
    public String getProfileQuery() {
        return m_profileQuery;
    } //
    
    public void setNeighborQuery(String query) throws SQLException {
        if ( m_db != null )
            m_ns = prepare(query);
        System.out.println(query);
        m_neighborQuery = query;
    } //
    
    public String getNeighborQuery() {
        return m_neighborQuery;
    } //
    
    public void setEdgeQuery(String query) throws SQLException {
        if ( m_db != null )
            m_es = prepare(query);
        System.out.println(query);
        m_edgeQuery = query;
    } //
    
    public String getEdgeQuery() {
        return m_edgeQuery;
    } //

    public static void main(String[] argv) {
        try {
	        String outputFile = argv.length > 0 ? argv[0] : "graph.xml";
	        Graph g = new DefaultGraph();
	        ItemRegistry registry = new ItemRegistry(g);
	        EnronicDBLoader loader = new EnronicDBLoader(registry, ALL_COLUMNS);
	        loader.connect("com.mysql.jdbc.Driver", 
	                "jdbc:mysql://localhost/enron",
	                "jheer",
	                "ripley-04");
	        loader.loadGraph();
	        new XMLGraphWriter().writeGraph(g, outputFile);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    } //
    
} // end of class enronicDatabaseLoader
