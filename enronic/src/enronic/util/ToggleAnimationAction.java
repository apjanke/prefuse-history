package enronic.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import enronic.Enronic;


/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class ToggleAnimationAction extends AbstractAction {

    private Enronic enronic;
    
    public ToggleAnimationAction(Enronic enronic) {
        this.enronic = enronic;
    } //
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        enronic.setAnimate(!enronic.isAnimate());
    } //

} // end of class ToggleAnimationAction
