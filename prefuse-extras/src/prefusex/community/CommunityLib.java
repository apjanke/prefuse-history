package prefusex.community;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import cern.colt.matrix.impl.SparseDoubleMatrix2D;

/**
 * 
 * Nov 19, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class CommunityLib {

    public static void writeDoubleArray(double[] a, String filename) throws IOException {
        PrintWriter bw = new PrintWriter(new FileWriter(filename));
        for ( int i=0; i < a.length; i++ ) {
            bw.println(a[i]);
        }
        bw.flush();
        bw.close();
    } //
    
    public static void writeParentList(List list, String filename)
        throws IOException
    {
        PrintWriter bw = new PrintWriter(new FileWriter(filename));
        for ( int i=0; i < list.size(); i++ ) {
            int[] e = (int[])list.get(i);
            bw.println(e[0]+" "+e[1]);
        }
        bw.flush();
        bw.close();
    } //
    
    public static SparseDoubleMatrix2D loadMatrix(String filename)
        throws IOException
    {
        int nrows = 0, ncols = 0, nnz = 0;
        int[] r = null;
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ( (line=br.readLine()) != null ) {
            try {
                r = parseLine(line, r);
                if ( r[0] > nrows ) nrows = r[0];
                if ( r[1] > ncols ) ncols = r[1];
                if ( r[2] > 0 ) nnz++;
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        br.close();
        
        if ( nrows > ncols ) ncols = nrows;
        if ( ncols > nrows ) nrows = ncols;
        
        System.out.println(nrows*ncols+" cap ? "+Integer.MAX_VALUE);
        System.out.println(nrows+" x "+ncols+" matrix, "+nnz+" non-zeroes.");
        
        SparseDoubleMatrix2D mat = new SparseDoubleMatrix2D(nrows, ncols);
        
        br = new BufferedReader(new FileReader(filename));
        while ( (line=br.readLine()) != null ) {
            try {
                r = parseLine(line, r);
                if ( r[2] > 0 )
                    mat.setQuick(r[0]-1,r[1]-1,1);
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        br.close();
        return mat;
    } //
    
    public static int[] parseLine(String line, int[] r) {
        String[] s = line.split("\t");
        if ( r == null )
            r = new int[s.length];
        for ( int i=0; i < r.length; i++ ) {
            r[i] = (int)Double.parseDouble(s[i].trim());
        }
        return r;
    } //
    
    public static List loadParentList(String file) throws IOException {
        List l = new ArrayList();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ( (line=br.readLine()) != null ) {
            String[] toks = line.split(" ");
            int u = Integer.parseInt(toks[0]);
            int v = Integer.parseInt(toks[1]);
            l.add(new int[] {u,v});
        }
        return l;
    } //
    
} // end of class CommunityLib