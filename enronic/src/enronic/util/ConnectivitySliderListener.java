package enronic.util;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import enronic.ConnectivityFilter;
import enronic.Enronic;


/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org</a>
 */
public class ConnectivitySliderListener implements ChangeListener {

    private Enronic enronic;
    
    public ConnectivitySliderListener(Enronic enronic) {
        this.enronic = enronic;
    } //
    
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent arg0) {
        JSlider slider = (JSlider)arg0.getSource();
        ConnectivityFilter cfilter = (ConnectivityFilter)enronic.getAction("connectivity");
        cfilter.setThreshold(slider.getValue());
        enronic.filter();
        enronic.redraw();
    } //

} // end of class CommunitySliderListener
