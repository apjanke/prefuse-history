package edu.berkeley.guir.prefusex.community;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

/**
 * Computes the community structure of an input graph using the
 * algorithm due to Newman. This algorithm uses a hierarchical
 * agglomerative clustering approach to greedily optimize a metric
 * of network modularity or community clustering.
 * 
 * The output of the algorithm is a list of the successive values
 * of the community metric at each iteration of the algorithm, and
 * a record of each agglomeration, allowing the cluster tree to be
 * later reconstructed.
 * 
 * Nov 19, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class CommunityStructureDirected implements CommunityStructure {

    private double dQ, maxDQ = 0.0;
    private int x, y;
    private int[] maxEdge = new int[] {0,0};
    
    private DoubleMatrix2D e;
    private LinkedList E;
    private DoubleMatrix1D a;
    
    private boolean verbose = true;
    private List plist;
    private double[] qval;
    
    public static void main(String[] args) {
        try {
            DoubleMatrix2D g = CommunityLib.loadMatrix(args[0]);
            CommunityStructureDirected comm = new CommunityStructureDirected();
            comm.verbose = true;
            comm.run(g);
            comm.saveResults();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    } //
    
    public void run(DoubleMatrix2D g) {
        long timein = System.currentTimeMillis();
        
        int N = g.rows();
        double Q = 0, maxQ = 0, maxIter = 0;
        
        plist = new ArrayList(N-1);
        qval = new double[N-1];
        
        if (verbose) System.out.println("Weighting matrix");
        
        // initialize weighted matrix
        e = g.copy();
        if (verbose) System.out.println("\tComputing normalization factor");
        e.forEachNonZero(new ClearDiagonal());
        ZSum zSum = new ZSum();
        e.forEachNonZero(zSum);
        if (verbose) System.out.println("\tScaling matrix");
        e.forEachNonZero(new Mult(1/zSum.getSum()));
        
        if (verbose) System.out.println("Computing column sums");
        
        // initialize column sums
        a = new DenseDoubleMatrix1D(N);
        e.forEachNonZero(new RowSum());
        
        if (verbose) System.out.print("Collecting edges... ");
        
        // initialize edges
        E = new LinkedList();
        e.forEachNonZero(new EdgeCollector());
        if (verbose) System.out.println(E.size()+" edges");
        
        if (verbose) System.out.println("Starting clustering");
        
        for ( int i=0; i < N-1 && E.size() > 0; i++ ) {
            maxDQ = Double.NEGATIVE_INFINITY;
            maxEdge[0] = 0; maxEdge[1] = 0;
            
            Iterator iter = E.iterator();
            while ( iter.hasNext() ) {
                int[] edge = (int[])iter.next();
                x = edge[0]; y = edge[1];
                if ( x == y ) continue;
                // compute delta Q
                dQ = e.getQuick(x,y) + e.getQuick(y,x) 
                        - 2*a.getQuick(x)*a.getQuick(y);
                // check against max so far
                if ( dQ > maxDQ ) {
                    maxDQ = dQ;
                    maxEdge[0] = x; maxEdge[1] = y;
                }
            }
            
            // update the graph
            x = maxEdge[0]; y = maxEdge[1];
            if ( y < x ) { // ensure merge ordering to less index
                int tmp = y; y = x; x = tmp;
            }
            double na = 0.0;
            for ( int k=0; k < N; k++ ) {
                double v = e.getQuick(x,k) + e.getQuick(y,k);
                if ( v != 0 ) { 
                    na += v;
                    e.setQuick(x,k,v);
                    e.setQuick(y,k,0);
                }
            }
            for ( int k=0; k < N; k++ ) {
                double v = e.getQuick(k,x) + e.getQuick(k,y);
                if ( v != 0 ) {
                    e.setQuick(k,x,v);
                    e.setQuick(k,y,0);
                }
            }
            a.setQuick(x,na);
            a.setQuick(y,0.0);
            
            if ( i % 100 == 0 ) {
                e.trimToSize();
            }
            
            // update edge list
            iter = E.iterator();
            while ( iter.hasNext() ) {
                int[] edge = (int[])iter.next();
                if ( (edge[0]==x && edge[1]==y) || (edge[0]==y && edge[1]==x) ) {
                    iter.remove();
                } else if ( edge[0] == y ) {
                    edge[0] = x;
                } else if ( edge[1] == y ) {
                    edge[1] = x;
                }
            }
            
            Q += maxDQ;
            if ( Q > maxQ ) {
                maxQ = Q;
                maxIter = i+1;
            }
            
            qval[i] = Q;
            plist.add(new int[] {x,y}); // shift back from 0-base to 1-base
            
            if (verbose) System.out.println(Q+"\t"+"iter "+(i+1)+"("+(N-i-1)+")\t"+"nedges = "+E.size());
        }
        
        if (verbose) System.out.println();
        if (verbose) System.out.println("maxQ = "+maxQ+", at iter "+maxIter+" (-"+(N-maxIter)+")");
        if (verbose) System.out.println(((System.currentTimeMillis()-timein)/1000.0)+" seconds");
    } //
    
    public void saveResults() {
        if (verbose) System.out.println("\nSaving Data");
        try {
            CommunityLib.writeDoubleArray(qval, "qvals.txt");
            CommunityLib.writeParentList(plist, "plist.txt");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    } //
    
    public List getMergeList() {
        return plist;
    } //
    
    public double[] getQValues() {
        return qval;
    } //
    
    // -- helpers -------------------------------------------------------------
    
    public class ClearDiagonal implements IntIntDoubleFunction {
        public double apply(int arg0, int arg1, double arg2) {
            return ( arg0 == arg1 ? 0.0 : arg2 );
        }
    } //
    
    public class EdgeCollector implements IntIntDoubleFunction {
        public double apply(int arg0, int arg1, double arg2) {
            if ( arg0 != arg1 ) {
                int[] edge = new int[] {arg0,arg1};
                E.add(edge);
            }
            return arg2;
        }
    } //
    
    public class RowSum implements IntIntDoubleFunction {
        public double apply(int arg0, int arg1, double arg2) {
            a.setQuick(arg0, a.getQuick(arg0)+arg2);
            return arg2;
        }
    } //
    
    public class Mult implements IntIntDoubleFunction {
        private double scalar;
        public Mult(double s) {
            scalar = s;
        }
        public double apply(int arg0, int arg1, double arg2) {
            return arg2*scalar;
        }
    } //
    
    public class ZSum implements IntIntDoubleFunction {
        double sum = 0;
        public double apply(int arg0, int arg1, double arg2) {
            sum += arg2;
            return arg2;
        }
        public void reset() {
            sum = 0;
        }
        public double getSum() {
            return sum;
        }
    } //
    
} // end of class CommunityStructureDirected
