package edu.berkeley.guir.prefusex.matrix;

import java.util.HashMap;
import java.util.Iterator;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * A COLT SparseDoubleMatrix2D instance representing the adjacency
 * matrix for a prefuse graph. Useful for performing more advanced
 * mathematical graph theoretic analyses on prefuse graphs.
 * 
 * Nov 25, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class PrefuseMatrix extends SparseDoubleMatrix2D {

    private HashMap nodeIndexMap = new HashMap();
    private HashMap indexNodeMap = new HashMap();
    
    public PrefuseMatrix(Graph g) {
        super(g.getNodeCount(), g.getNodeCount());
        init(g);
    } //
    
    protected void init(Graph g) {
        int id = 0;
        Iterator iter = g.getNodes();
        while ( iter.hasNext() ) {
            Node n = (Node)iter.next();
            if ( n instanceof NodeItem ) {
                n = (Node)((NodeItem)n).getEntity();
            }
            Integer k = new Integer(id++);
            indexNodeMap.put(k,n);
            nodeIndexMap.put(n,k);
        }
        
        int size = nodeIndexMap.size();
        iter = g.getEdges();
        while ( iter.hasNext() ) {
            Edge e = (Edge)iter.next();
            Node nn1 = e.getFirstNode();
            Node nn2 = e.getSecondNode();
            if ( nn1 instanceof NodeItem ) {
                nn1 = (Node)((NodeItem)nn1).getEntity();
            }
            if ( nn2 instanceof NodeItem ) {
                nn2 = (Node)((NodeItem)nn2).getEntity();
            }
            int n1 = ((Integer)nodeIndexMap.get(nn1)).intValue();
            int n2 = ((Integer)nodeIndexMap.get(nn2)).intValue();
            String wstr = e.getAttribute("weight");
            double w = (wstr==null || wstr.equals("") ? 
                    	1.0 : Double.parseDouble(wstr));
            this.setQuick(n1,n2,w);
            if ( !g.isDirected() ) {
                this.setQuick(n2,n1,w);
            }
        }
    } //
    
    public Node getNode(int idx) {
        return (Node)indexNodeMap.get(new Integer(idx));
    } //
    
    public Node getNode(Integer idx) {
        return (Node)indexNodeMap.get(idx);
    } //
    
    public int getIndex(Node n) {
        Integer idx = (Integer)nodeIndexMap.get(n);
        return ( idx == null ? -1 : idx.intValue() );
    } //
    
    public void remove(Node n) {
        Integer idx = (Integer)nodeIndexMap.get(n);
        if ( idx == null ) return;
        
        nodeIndexMap.remove(n);
        indexNodeMap.remove(idx);
        
        int k = idx.intValue();
        for ( int i=0; i<rows(); i++ ) {
            this.setQuick(k,i,0.0);
            this.setQuick(i,k,0.0);
        }
    } //
    
} // end of class PrefuseMatrix
