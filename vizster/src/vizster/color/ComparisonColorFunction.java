package vizster.color;

import java.awt.Color;
import java.awt.Paint;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;

/**
 * 
 * Apr 14, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class ComparisonColorFunction extends ColorFunction {

    // attributes to support
    // -- quantitative --
    //    "age"    (1-100)
    // -- dates (->quant) --
    //    "membersince"
    //    "lastlogin"
    //    "lastmod"
    // -- nominal --
    //    "gender" (M x F)
    //    "status" (S, M, OM, IAR, JHTH)
    // -- locations (->nom) --
    //    "location"
    //    "hometown"
    
    private static final int QUANTITATIVE = 0;
    private static final int NOMINAL      = 1;
    private static final int DATE         = 2;
    private static final int LOCATION     = 3;
    
    private static final String[] attrs = {
            "age", "membersince", "lastlogin", "lastmod",
            "gender", "status", "location", "hometown"
    };
    private static final int[] types = {
            QUANTITATIVE, DATE, DATE, DATE, NOMINAL, NOMINAL,
            LOCATION, LOCATION
    };
    
    private Color nullColor = Color.BLACK;
    
    public Paint getColor(VisualItem item) {
        return Color.WHITE;
    } //
    
    public Paint getFillColor(VisualItem item) {
        return Color.BLACK;
    } //            
    
} // end of class ComparisonColorFunction
