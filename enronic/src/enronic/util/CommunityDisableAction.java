package enronic.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefusex.community.CommunitySet;
import enronic.Enronic;


/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class CommunityDisableAction extends AbstractAction {

    private Enronic enronic;
    
    public CommunityDisableAction(Enronic enronic) {
        this.enronic = enronic;
    } //
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        ItemRegistry registry = enronic.getItemRegistry();
        CommunitySet comm = (CommunitySet) 
            registry.getFocusManager().getFocusSet(Enronic.COMMUNITY_KEY);
        comm.clear();
        enronic.getBrowsingColorFunction().updateCommunityMap(comm);
    } //

} // end of class CommunityDisableAction
