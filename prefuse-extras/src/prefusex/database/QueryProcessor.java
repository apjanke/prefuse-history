package prefusex.database;

import java.sql.ResultSet;

/**
 * An interface for issuing and processing a SQL database query.
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public interface QueryProcessor {

    public void start();
    public boolean hasNext();
    public String getQuery();
    public void processResults(ResultSet rs) throws Exception;
    
} // end of interface QueryProcessor
