package prefusex.matrix;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.matrix.DoubleMatrix2D;

/**
 * Utility methods for use with matrices.
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class MatrixLib {

    private MatrixLib() {
        // do not allow instantiation
    } //
    
    public static void writeCLUTOMatrix(DoubleMatrix2D mat, String filename) 
    	throws IOException
    {
        FileOutputStream     fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        PrintStream          out = new PrintStream(bos);
        
        int nrows = mat.rows(), ncols = mat.columns();
        out.println(nrows+" "+ncols+" "+mat.cardinality());
        for (int row=0; row < nrows; row++) {
            for (int col=0; col < ncols; col++) {
                out.print((col+1)+" "+mat.getQuick(row,col)+" ");
            }
            out.println();
        }
        
        out.flush();
        out.close();
    } //
    
    public static void writeMATLABMatrix(DoubleMatrix2D mat, String filename)
    	throws IOException
    {
        FileOutputStream     fos = new FileOutputStream(filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        final PrintStream    out = new PrintStream(bos);
        
        IntIntDoubleFunction printer = new IntIntDoubleFunction() {
            public double apply(int row, int col, double val) {
                out.println((row+1)+"\t"+(col+1)+"\t"+val);
                return val;
            }
        };
        int nrows = mat.rows(), ncols = mat.columns();
        if ( mat.getQuick(nrows-1,ncols-1) == 0.0 ) {
            out.println(nrows+"\t"+ncols+"\t0");
        }
        
        out.flush();
        out.close();
    } //
    
} // end of class MatrixLib
