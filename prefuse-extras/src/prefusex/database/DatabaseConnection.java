package prefusex.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseConnection
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DatabaseConnection {

    protected DatabaseParams params;
    protected Connection conn;
    protected Statement statement;
    
    public DatabaseConnection(DatabaseParams dbparams) {
        this.params = dbparams;
    } //
    
    public void process(QueryProcessor qp) throws Exception {
	    // connect to database if necessary
	    if ( conn == null ) { // TODO: more sophisticated checking here?
	        connect();
	    }
	    
	    // prepare a generic statement
	    statement = conn.createStatement(
	            ResultSet.TYPE_SCROLL_INSENSITIVE, 
	            ResultSet.CONCUR_READ_ONLY);
	    
	    // initialize query processor
	    qp.start();
	    
	    // run query processor until it's done
	    while ( qp.hasNext() ) {
		    // construct query and issue to database
		    String query = qp.getQuery();
		    ResultSet rs = statement.executeQuery(query);
		    // process results
		    qp.processResults(rs);
	    }
	} //
	
	protected void connect() 
		throws InstantiationException, IllegalAccessException, 
			   ClassNotFoundException, SQLException
	{
	    // connect to database
	    Class.forName(params.getDriver()).newInstance();
	    conn = DriverManager.getConnection(params.getDatabaseURL(),
	            params.getUser(), params.getPassword());
	} //
    
} // end of class DatabaseConnection
