package enronic.util;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JFileChooser;

import enronic.Enronic;


/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class LoadNetworkAction extends AbstractAction {

    private Enronic enronic;
    private JFileChooser chooser;
    
    public LoadNetworkAction(Enronic enronic) {
        this.enronic = enronic;
        chooser = new JFileChooser();
    } //

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        String datafile = null;
        String startUID = null;
        AbstractButton jc = (AbstractButton)arg0.getSource();
        
        if ( EnronicMenuBar.LOAD.equals(jc.getActionCommand()) ) {
	        // choose file to save to
	        File f = null;
	        int rval = chooser.showOpenDialog(enronic);
	        if( rval == JFileChooser.APPROVE_OPTION ) {
	           f = chooser.getSelectedFile();
	        } else {
	            return;
	        }
	        datafile = f.toString();
        } else {
            startUID = Enronic.DEFAULT_START_UID;
        }
        enronic.loadGraph(datafile, startUID);
    } //

} // end of class SaveVisibleGraphAction
