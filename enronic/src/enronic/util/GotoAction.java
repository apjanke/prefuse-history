package enronic.util;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.graph.Node;
import enronic.Enronic;
import enronic.EnronicLib;
import enronic.data.EnronicDBLoader;

/**
 * Allows users to jump to a given user id.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class GotoAction extends AbstractAction {

    private Enronic enronic;
    
    public GotoAction(Enronic enronic) {
        this.enronic = enronic;
    } //
    
    public void actionPerformed(ActionEvent e) {
        // get the friendster uid from the user
        String uid = JOptionPane.showInputDialog(
                        enronic,
                        "Enter the User ID for the profile to go to.",
                        "Go To Profile",
                        JOptionPane.QUESTION_MESSAGE);
        if ( uid == null ) {
            // user canceled, so do nothing
            return;
        }
        
        // load the profile as needed
        EnronicDBLoader loader = enronic.getLoader();
        Node n = null;
        try {
            n = loader.getProfileNode(uid);
        } catch (SQLException e1) {
            // bail if profile not found
            EnronicLib.defaultError(enronic,
                    "Couldn't find the requested profile!");
            return;
        }
        
        // set the profile node as the new focus
        ItemRegistry registry = enronic.getItemRegistry();
        FocusManager fmanager = registry.getFocusManager();
        fmanager.getFocusSet(Enronic.CLICK_KEY).set(n);
        registry.getDefaultFocusSet().set(n);
    } //

} // end of class GotoAction
