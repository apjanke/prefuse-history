package enronic.util;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphWriter;
import enronic.Enronic;
import enronic.EnronicLib;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class SaveVisibleNetworkAction extends AbstractAction {

    private Enronic enronic;
    private JFileChooser chooser;
    
    public SaveVisibleNetworkAction(Enronic enronic) {
        this.enronic = enronic;
        chooser = new JFileChooser();
    } //

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        // choose file to save to
        File f = null;
        int rval = chooser.showOpenDialog(enronic);
        if( rval == JFileChooser.APPROVE_OPTION ) {
           f = chooser.getSelectedFile();
        } else {
            return;
        }
        
        // write out the current graph
        ItemRegistry registry = enronic.getItemRegistry();
        XMLGraphWriter gw = new XMLGraphWriter();
        try {
            gw.writeGraph(registry.getFilteredGraph(), f);
        } catch ( Exception e ) {
            e.printStackTrace();
            EnronicLib.defaultError(enronic, "Error saving file!");
        }
    } //

} // end of class SaveVisibleNetworkAction
