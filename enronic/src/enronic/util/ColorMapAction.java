package enronic.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import enronic.Enronic;


/**
 * Updates which color map is used in attribute comparison mode
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ColorMapAction extends AbstractAction {

    private Enronic enronic;
    
    public ColorMapAction(Enronic enronic) {
        this.enronic = enronic;
    } //
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        int map = -1;
        if ( cmd == EnronicMenuBar.GMAP ) {
            map = 0;
        } else if ( cmd == EnronicMenuBar.HMAP ) {
            map = 1;
        } else if ( cmd == EnronicMenuBar.CMAP ) {
            map = 2;
        }
        enronic.getComparisonColorFunction().setColorMap(map);
        enronic.redraw();
    } //

} // end of class ColorMapAction
