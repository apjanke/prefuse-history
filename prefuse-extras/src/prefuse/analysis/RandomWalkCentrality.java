package prefuse.analysis;

import java.util.Iterator;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Node;
import prefusex.matrix.PrefuseMatrix;

/**
 * Computes betweenness centrality for each vertex in the graph. The betweenness values in this case
 * are based on random walks, measuring the expected number of times a node is traversed by a random walk
 * averaged over all pairs of nodes. The result is that each vertex has a UserData element of type
 * MutableDouble whose key is 'centrality.RandomWalkBetweennessCentrality'
 *
 * A simple example of usage is:  <br>
 * RandomWalkBetweenness ranker = new RandomWalkBetweenness(someGraph);   <br>
 * ranker.evaluate(); <br>
 * ranker.printRankings(); <p>
 *
 * Running time is: O((m+n)*n2).
 * @see "Mark Newman: A measure of betweenness centrality based on random walks, 2002."

 * @author Scott White
 * 
 * Ported from the JUNG {@link http://jung.sourceforge.net} project.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class RandomWalkCentrality {

    PrefuseMatrix mat;
    private DoubleMatrix2D mVoltageMatrix;
    
    public RandomWalkCentrality(PrefuseMatrix mat) {
        this.mat = mat;
    } //

    public void computeBetweenness() {
        setUp();

        int numVertices = mat.rows();
        double normalizingConstant = numVertices*(numVertices-1)/2.0;
        
        for ( int i=0; i<numVertices; i++ ) {
            double ithBetweenness = 0;
            for ( int t=0; t<numVertices; t++ ) {
                for ( int s=0; s<t; s++ ) {
                    ithBetweenness += computeSTBetweenness(i, s, t);
                }
            }
            // set rank for iV = ithBetweenness/normalizingConstant
            NodeItem iV = (NodeItem)mat.getNode(i);
            iV.setVizAttribute("betweenness",
                    new Double(ithBetweenness/normalizingConstant));
        }
    } //

    protected DoubleMatrix2D getVoltageMatrix() {
        return mVoltageMatrix;
    } //

    protected void setUp() {
        mVoltageMatrix = computeVoltagePotentialMatrix(mat);
    } //

    public double computeSTBetweenness(int i, int s, int t) {
        if (i == s || i == t) return 1;
        if (mVoltageMatrix == null) {
            setUp();
        }
        Node iV = mat.getNode(i);
        
        double betweenness = 0;
        for (Iterator vIt = iV.getNeighbors(); vIt.hasNext();) {
            Node jV = (Node) vIt.next();
            int j = mat.getIndex(jV);
            double currentFlow = 0;
            currentFlow += mVoltageMatrix.get(i,s);
            currentFlow -= mVoltageMatrix.get(i,t);
            currentFlow -= mVoltageMatrix.get(j,s);
            currentFlow += mVoltageMatrix.get(j,t);
            betweenness += Math.abs(currentFlow);
        }
        return betweenness/2.0;
    } //
    
    /**
     * The idea here is based on the metaphor of an electric circuit. We assume
     * that an undirected graph represents the structure of an electrical
     * circuit where each edge has unit resistance. One unit of current is
     * injected into any arbitrary vertex s and one unit of current is extracted
     * from any arbitrary vertex t. The voltage at some vertex i for source
     * vertex s and target vertex t can then be measured according to the
     * equation: V_i^(s,t) = T_is - T-it where T is the voltage potential matrix
     * returned by this method. *
     * 
     * @param graph
     *            an undirected graph representing an electrical circuit
     * @return the voltage potential matrix
     * @see "P. Doyle and J. Snell, 'Random walks and electric networks,', 1989"
     * @see "M. Newman, 'A measure of betweenness centrality based on random
     *      walks', pp. 5-7, 2003"
     */
    public static DoubleMatrix2D computeVoltagePotentialMatrix(PrefuseMatrix A)
    {
        int numVertices = A.rows();
        //create diagonal matrix of vertex degrees
        DoubleMatrix2D D = createVertexDegreeDiagonalMatrix(A);
        DoubleMatrix2D temp = new SparseDoubleMatrix2D(numVertices-1, numVertices-1);
        //compute D - A except for last row and column
        for (int i = 0; i < numVertices - 1; i++)
        {
            for (int j = 0; j < numVertices - 1; j++)
            {
                temp.set(i, j, D.get(i, j) - A.get(i, j));
            }
        }
        Algebra algebra = new Algebra();
        DoubleMatrix2D tempInverse = algebra.inverse(temp);
        DoubleMatrix2D T = new SparseDoubleMatrix2D(numVertices, numVertices);
        //compute "voltage" matrix
        for (int i = 0; i < numVertices - 1; i++)
        {
            for (int j = 0; j < numVertices - 1; j++)
            {
                T.set(i, j, tempInverse.get(i, j));
            }
        }
        return T;
    } //
    
    /**
     * Returns a diagonal matrix whose diagonal entries contain the degree for
     * the corresponding node.
     * 
     * @return SparseDoubleMatrix2D
     */
    public static SparseDoubleMatrix2D createVertexDegreeDiagonalMatrix(PrefuseMatrix A)
    {
        int numVertices = A.rows();
        SparseDoubleMatrix2D matrix = new SparseDoubleMatrix2D(numVertices,
                numVertices);
        for ( int i=0; i<numVertices; i++ ) {
            Node n = A.getNode(i);
            matrix.set(i,i,n.getEdgeCount());
        }
        return matrix;
    } //
    
} // end of class RandomWalkCentrality