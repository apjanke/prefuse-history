package enronic.util;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefusex.community.CommunitySet;

import enronic.Enronic;


/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class CommunitySliderListener implements ChangeListener {

    private Enronic enronic;
    
    public CommunitySliderListener(Enronic enronic) {
        this.enronic = enronic;
    } //
    
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent arg0) {
        JSlider slider = (JSlider)arg0.getSource();
        CommunitySet comm = (CommunitySet)enronic.getItemRegistry().
        	getFocusManager().getFocusSet(Enronic.COMMUNITY_KEY);
        int value = slider.getValue();
        comm.reconstruct(value);
        enronic.redraw();
    } //

} // end of class CommunitySliderListener
