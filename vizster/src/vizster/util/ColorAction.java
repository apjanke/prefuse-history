package vizster.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

import vizster.Vizster;
import vizster.color.ComparisonColorFunction;

/**
 * Updates which attribute is visualized in the attribute comparison mode
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class ColorAction extends AbstractAction {

    private Vizster vizster;
    
    public ColorAction(Vizster vizster) {
        this.vizster = vizster;
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
            ComparisonColorFunction cf = vizster.getComparisonColorFunction();
            cf.setCurrentAttribute(idx);
            vizster.setMode(Vizster.COMPARE_MODE);
        } else {
            JToggleButton inv = (JToggleButton)tog.getClientProperty("inv");
            inv.doClick();
            tog.putClientProperty("on", Boolean.FALSE);
            vizster.setMode(Vizster.BROWSE_MODE);
        }
    } //

} // end of class ColorAction