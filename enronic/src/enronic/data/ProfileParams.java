package enronic.data;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class ProfileParams {

    public String[] fields;
    public Object[] vizType;
    
    public String[] getFields() {
        return fields;
    } //
    
    public int size() {
        return fields.length;
    } //
    
    public String getField(int i) {
        return fields[i];
    } //
    
    public Object getVizType(int i) {
        return vizType[i];
    } //
    
} // end of class ProfileParams
