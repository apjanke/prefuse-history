package enronic.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

import enronic.Enronic;
import enronic.color.ComparisonColorFunction;


/**
 * Updates which attribute is visualized in the attribute comparison mode
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ColorAction extends AbstractAction {

    private Enronic enronic;
    
    public ColorAction(Enronic enronic) {
        this.enronic = enronic;
    } //
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        JToggleButton tog = (JToggleButton)e.getSource();
        Boolean onB = (Boolean)tog.getClientProperty("on");
        boolean on = (onB == null ? tog.isSelected() : !onB.booleanValue());
        if ( on ) {
            Integer attrI = (Integer)tog.getClientProperty("attr");
            tog.putClientProperty("on", Boolean.TRUE);
            int idx = attrI.intValue();
            ComparisonColorFunction cf = enronic.getComparisonColorFunction();
            cf.setCurrentAttribute(idx);
            enronic.setMode(Enronic.COMPARE_MODE);
        } else {
            JToggleButton inv = (JToggleButton)tog.getClientProperty("inv");
            inv.doClick();
            tog.putClientProperty("on", Boolean.FALSE);
            enronic.setMode(Enronic.BROWSE_MODE);
        }
        enronic.redraw();
    } //

} // end of class ColorAction
