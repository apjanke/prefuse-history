package edu.berkeley.guir.prefusex.community;

import java.util.List;

import cern.colt.matrix.DoubleMatrix2D;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public interface CommunityStructure {

    public void run(DoubleMatrix2D g);
    public List getMergeList();
    public double[] getQValues();
    
} // end of interface CommunityStructure
