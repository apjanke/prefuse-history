package enronic.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import edu.berkeley.guir.prefuse.Display;
import enronic.Enronic;

/**
 * Turns on the display of a debugging info string on the enronic display.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DebugInfoAction extends AbstractAction {

    private Enronic enronic;
    
    public DebugInfoAction(Enronic enronic) {
        this.enronic = enronic;
    } //
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Display d = enronic.getDisplay();
        d.setDebug(!d.getDebug());
    } //

} // end of class DebugInfoAction