package edu.berkeley.guir.prefuse.graph.external;

import java.util.LinkedList;
import java.util.List;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.SimpleGraph;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderListener;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderMulticaster;


/**
 * Loads graph data from an external data source, such as a database or
 * filesystem.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public abstract class GraphLoader implements Runnable {

    public static final int LOAD_NEIGHBORS = 0;
    public static final int LOAD_CHILDREN  = 1;
    
    protected List m_queue = new LinkedList();
    
    protected Graph m_graph;
    protected ItemRegistry m_registry;
    
    protected GraphLoaderListener m_listener;
    
    public GraphLoader(ItemRegistry registry) {
        m_registry = registry;
        m_graph = registry.getGraph();
        Thread t = new Thread(this);
        
        // we don't want this to slow down animation!
        // besides, most of its work is blocking on IO anyway...
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    } //
    
    public void addGraphLoaderListener(GraphLoaderListener l) {
        m_listener = GraphLoaderMulticaster.add(m_listener, l);
    } //
    
    public void removeGraphLoaderListener(GraphLoaderListener l) {
        m_listener = GraphLoaderMulticaster.remove(m_listener, l);
    } //
    
    public synchronized void loadNeighbors(ExternalNode n) {
        Job j = new Job(LOAD_NEIGHBORS,n);
        if ( !m_queue.contains(j) ) {
            m_queue.add(j);
            this.notifyAll();
        }
    } //
    
    public synchronized void loadChildren(ExternalTreeNode n) {
        Job j = new Job(LOAD_CHILDREN,n);
        if ( !m_queue.contains(j) ) {
            m_queue.add(j);
            this.notifyAll();
        }
    } //
    
    public boolean evict() {
        return false;
    } //
    
    public void run() {
        while ( true ) {
            Job job = getNextJob();
            if ( job != null ) {
                if ( job.type == LOAD_NEIGHBORS ) {
                    getNeighbors(job.n);
                    job.n.setNeighborsLoaded(true);
                } else if ( job.type == LOAD_CHILDREN ) {
                    getChildren((ExternalTreeNode)job.n);
                }
            } else {
                // nothing to do, chill out until notified
                try {
                    synchronized (this) { wait(); }
                } catch (InterruptedException e) { }
            }
        }
    } //
    
    protected synchronized Job getNextJob() {
        return (m_queue.isEmpty() ? null : (Job)m_queue.remove(0));
    } //
    
    protected void foundNode(int type, ExternalNode n1, ExternalNode n2) {
        if ( /*n2 is already loaded*/ false ) {
            // switch n2 reference to original loaded version
        }
        n2.setLoader(this);
        synchronized ( m_registry ) {
            ((SimpleGraph)m_graph).addNode(n2);
            if ( n1 != null )
                ((SimpleGraph)m_graph).addEdge(n1,n2);
        }
        if ( m_listener != null )
            m_listener.entityLoaded(n2);
    } //
    
    protected abstract void getNeighbors(ExternalNode n);
    
    protected abstract void getChildren(ExternalTreeNode n);
    
    public class Job {
        public Job(int type, ExternalNode n) {
            this.type = type;
            this.n = n;
        }
        int type;
        ExternalNode n;
        public boolean equals(Object o) {
            if ( !(o instanceof Job) )
                return false;
            Job j = (Job)o;
            return ( type==j.type && n==j.n );
        }
    } //
    
} // end of class GraphLoader