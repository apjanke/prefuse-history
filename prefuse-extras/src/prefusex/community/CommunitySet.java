package prefusex.community;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

import prefusex.matrix.PrefuseMatrix;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.focus.DefaultFocusSet;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * 
 * Nov 25, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class CommunitySet extends DefaultFocusSet {

    private ItemRegistry registry = null;
    private HashMap communityMap = new HashMap();
    private HashMap memberSets = new HashMap();
    private int numCommunities = 0;
    private int maxCommunities = 0;
    
    private PrefuseMatrix mat;
    private List mergeList;
    private double[] qvals;
    private BoundedRangeModel range;
    private boolean removeFoci = true;
    
    public CommunitySet() {
        this(false);
    } //
    
    public CommunitySet(boolean removeFoci) {
        this.removeFoci = removeFoci; 
    } //
        
    public void clear() {
        super.clear();
        communityMap.clear();
        memberSets.clear();
        numCommunities = 0;
        maxCommunities = 0;
    } //
    
    public void init(ItemRegistry registry) {
        this.registry = registry;
        Graph g = registry.getFilteredGraph();
        mat = new PrefuseMatrix(g);
        
        if ( removeFoci ) {
	        Iterator iter = registry.getDefaultFocusSet().iterator();
	        while ( iter.hasNext() ) {
	            mat.remove((Node)iter.next());
	        }
        }
        
        CommunityStructure comm = new CommunityStructureDirected();
        comm.run(mat);
        
        mergeList = comm.getMergeList();
        qvals = comm.getQValues();
        
        reconstruct(qvals.length-1);
        reconstruct(getMaxQValueIndex());
    } //
    
    public BoundedRangeModel getRange() {
        return range;
    } //
    
    private int computeRange(int idx) {
        int max = qvals.length-1;
        for ( int i = 0; i < qvals.length; i++ ) {
            if ( Double.isInfinite(qvals[i]) || Double.isNaN(qvals[i]) || qvals[i] == 0.0 ) {
                break;
            } else {
                max = i;
            }
        }
        if ( idx > max ) 
            idx = max;
        range = new DefaultBoundedRangeModel(idx,0,0,max);
        return idx;
    } //
    
    private int getMaxQValueIndex() {
        // get index for "optimal" cut
        int idx = -1;
        double max = -1;
        for ( int i=0; i<qvals.length; i++ ) {
            if ( qvals[i] > max ) {
                max = qvals[i];
                idx = i;
            }
        }
        return idx;
    } //
    
    public void reconstruct(int idx) {
        synchronized ( registry ) {
	        clear();
	        
	        idx = computeRange(idx);
	        
	        // merge groups
	        int i = 0;
	        // use link hashed map to enforce ordering
	        // this is crucial for getting stable colors
	        HashMap merge = new LinkedHashMap();
	        Iterator iter = mergeList.iterator();
	        while ( iter.hasNext() && i <= idx ) {
	            int[] edge = (int[])iter.next();
	            Integer k1 = new Integer(edge[0]);
	            Integer k2 = new Integer(edge[1]);
	            List l1;
	            if ( (l1=(List)merge.get(k1)) == null ) {
	                l1 = new ArrayList();
	                l1.add(mat.getNode(k1));
	                merge.put(k1,l1);
	            }
	            List l2;
	            if ( (l2=(List)merge.get(k2)) == null ) {
	                l1.add(mat.getNode(k2));
	            } else {
	                l1.addAll(l2);
	                merge.remove(k2);
	            }
	            i++;
	            if ( merge.size() > maxCommunities )
	                maxCommunities = merge.size();
	        }
	        // set community count
	        this.numCommunities = merge.size();
	        
	        // re-label and index community groups
	        int id = 0;
	        iter = merge.keySet().iterator();
	        while ( iter.hasNext() ) {
	            Integer setidx = new Integer(id);
	            List l = (List)merge.get(iter.next());
	            Iterator listiter = l.iterator();
	            while ( listiter.hasNext() ) {
	                Node n = (Node)listiter.next();
	                if ( n instanceof NodeItem ) {
	                    n = (Node)((NodeItem)n).getEntity();
	                }
	                this.add(n);
	                communityMap.put(n, setidx);
	                Set set = (Set)memberSets.get(setidx);
	                if ( set == null ) {
	                    set = new HashSet();
	                    memberSets.put(setidx, set);
	                }
	                set.add(n);
	            }
	            id++;
	        }
        }
    } //
    
    public int getMaxCommunityCount() {
        return maxCommunities;
    } //
    
    public int getCommunityCount() {
        return numCommunities;
    } //
    
    public Set getCommunityMembers(int i) {
        return (Set)memberSets.get(new Integer(i));
    } //
    
    public int getCommunity(Node n) {
        Integer comm = (Integer)communityMap.get(n);
        return (comm == null ? -1 : comm.intValue());
    } //
    
} // end of class CommunitySet
