package vizster;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import vizster.util.LoginDialog;
import edu.berkeley.guir.prefuse.graph.external.DatabaseLoader;

/**
 * Library of useful routines supporting the Vizster application
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizter(AT)jheer.org
 */
public class VizsterLib {

    public static final int DEFAULT_ERROR = 0;
    public static final int PROFILE_ERROR = 1;
    
    /**
     * Exit the application upon occurrence of an error
     * @param e the exception accompanying the error, if any
     * @param c the parent component, if exists 
     * @param msg the error message
     */
    public static final void errexit(Exception e, Component c, String msg) {
        errexit(DEFAULT_ERROR, e, c, msg);
    } //
    
    /**
     * Exit the application upon occurrence of an error
     * @param type the type of error
     * @param e the exception accompanying the error, if any
     * @param c the parent component, if exists 
     * @param msg the error message
     */
    public static final void errexit(int type, Exception e, Component c, String msg) {
        if ( e != null )
            e.printStackTrace();
        switch ( type ) {
        case PROFILE_ERROR:
            profileLoadError(c,msg);
            break;
        default:
            defaultError(c,msg);
        }
        System.exit(1);
    } //
    
    
    /**
     * Show an error dialog for a failed profile load
     * @param c the parent component
     * @param uid the user id of the failed profile
     */
    public static final void profileLoadError(Component c, String uid) {
        JOptionPane.showMessageDialog(c, "Error loading profile: "+uid,
                "Error Loading Profile", JOptionPane.ERROR_MESSAGE);
    } //
    
    /**
     * Show an error dialog
     * @param c the parent component
     * @param msg the error message
     */
    public static final void defaultError(Component c, String msg) {
        JOptionPane.showMessageDialog(c, msg);
    } //
    
//    /**
//     * Get login information.
//     * @param frame
//     * @return
//     */
//    public static final String[] authenticate(JFrame frame) {
//        if ( loginDialog == null )
//            loginDialog = new LoginDialog(frame);
//        loginDialog.show();
//        return loginDialog.getLoginInfo();
//    } //
    
    public static final boolean authenticate(Vizster owner, 
            DatabaseLoader loader, int retries) {
        LoginDialog ld = new LoginDialog(owner);
        ld.show();
        return ld.isLoggedIn();
        
//        for (int i=1; retries>=0 && i<=retries; i++) {
//            try {
//                if ( loginDialog == null )
//                    loginDialog = new LoginDialog(frame);
//                loginDialog.show();
//                String[] auth = loginDialog.getLoginInfo();
//                if ( auth == null )
//                    return false;
//                loader.connect("com.mysql.jdbc.Driver",
//                        // first use the host and database name
//                        "jdbc:mysql://" + auth[0] + "/" + auth[1],
//                        auth[2],    // now use the login
//                        auth[3]);   // and finally the password
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                VizsterLib.defaultError(frame,"Couldn't connect to database");
//            }
//            if ( i == retries )
//                VizsterLib.errexit(null,frame,"Too many login attempts.");
//        }
//        return false;
    } //
    
    public static final void setLookAndFeel() {
        try {
            String laf = UIManager.getSystemLookAndFeelClassName();             
            UIManager.setLookAndFeel(laf);  
        } catch ( Exception e ) {}
    } //
    
} // end of class VizsterLib
