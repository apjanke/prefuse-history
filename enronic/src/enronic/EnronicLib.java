package enronic;

import java.awt.Component;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import enronic.util.LoginDialog;


/**
 * Library of useful routines supporting the enronic application
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizter(AT)jheer.org
 */
public class EnronicLib {

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
    
    public static final boolean authenticate(Enronic owner, int retries) {
        LoginDialog ld = new LoginDialog(owner);
        ld.setVisible(true);
        return ld.isLoggedIn();
    } //
    
    public static final Graph loadGraph(String graphfile) 
    	throws FileNotFoundException, IOException
    {
        XMLGraphReader gl = new XMLGraphReader();
        return gl.loadGraph(graphfile);
    } //
    
    public static final void setLookAndFeel() {
        try {
            String laf = UIManager.getSystemLookAndFeelClassName();             
            UIManager.setLookAndFeel(laf);  
        } catch ( Exception e ) {}
    } //
    
} // end of class enronicLib
