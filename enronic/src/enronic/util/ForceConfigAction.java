package enronic.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPanel;

import edu.berkeley.guir.prefusex.force.ForcePanel;
import enronic.Enronic;


/**
 * Brings up a dialog allowing users to configure the force simulation
 *  that provides enronic's layout.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ForceConfigAction extends AbstractAction {

    private Enronic enronic;
    private JDialog dialog;
    
    public ForceConfigAction(Enronic enronic) {
        this.enronic = enronic;
        dialog = new JDialog(enronic, false);
        dialog.setTitle("Configure Force Simulator");
        JPanel forcePanel = new ForcePanel(enronic.getForceSimulator());
        dialog.getContentPane().add(forcePanel);
        dialog.pack();
    } //
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(true);
    } //

} // end of class ForceConfigAction
