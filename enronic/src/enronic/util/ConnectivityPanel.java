package enronic.util;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import enronic.Enronic;

/**
 * Provides keyword search over the currently visualized data.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ConnectivityPanel extends JPanel {
    
    private JSlider    conSlider  = new JSlider();

    public ConnectivityPanel(Enronic enronic) {
        conSlider.setModel(new DefaultBoundedRangeModel(1,0,1,20));
        conSlider.addChangeListener(new ConnectivitySliderListener(enronic));
        conSlider.setPreferredSize(new Dimension(200,25));
        conSlider.setMaximumSize(new Dimension(200,25));
        //conSlider.setPaintTicks(true);
        //conSlider.setPaintLabels(true);
        
        setBackground(Color.WHITE);
        initUI();
    } //

    private void initUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        Box b = new Box(BoxLayout.X_AXIS);
        b.add(Box.createHorizontalGlue());
        b.add(Box.createHorizontalStrut(5));
        b.add(new JLabel("Connectivity"));
        b.add(Box.createHorizontalStrut(5));
        b.add(conSlider);
        b.add(Box.createHorizontalStrut(5));
        b.add(Box.createHorizontalGlue());
        
        this.add(b);
    } //
    
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if ( conSlider != null ) conSlider.setBackground(bg);
    } //
    
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if ( conSlider != null ) conSlider.setForeground(fg);
    } //

} // end of class SearchPanel
