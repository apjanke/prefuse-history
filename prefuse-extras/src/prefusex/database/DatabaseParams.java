package prefusex.database;

/**
 * DatabaseParams
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DatabaseParams {

    private String protocol;
    private String driver;
    private String host;
    private String database;
    private String user;
    private String password;

    /**
     * @return Returns the protocol.
     */
    public String getProtocol() {
        return protocol;
    }
    
    /**
     * @param protocol The protocol to set.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getDatabaseURL() {
        return protocol+"//"+host+"/"+database;
    } //
    
    /**
     * @return Returns the database name.
     */
    public String getDatabase() {
        return database;
    }
    /**
     * @param database The database name to set.
     */
    public void setDatabase(String database) {
        this.database = database;
    }
    /**
     * @return Returns the SQL driver.
     */
    public String getDriver() {
        return driver;
    }
    /**
     * @param driver The SQL driver to set.
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }
    /**
     * @return Returns the hostname or ip of the database.
     */
    public String getHost() {
        return host;
    }
    /**
     * @param host The hostname or ip of the database to set.
     */
    public void setHost(String host) {
        this.host = host;
    }
    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return Returns the username.
     */
    public String getUser() {
        return user;
    }
    /**
     * @param user The username to set.
     */
    public void setUser(String user) {
        this.user = user;
    }
    
} // end of class DatabaseParams
