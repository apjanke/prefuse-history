package enronic.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefusex.community.CommunitySet;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import enronic.Enronic;


/**
 * CommunityPanel
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class CommunityPanel extends JPanel
	implements ChangeListener, ActionListener
{

    private static final String ENABLED  = "Disable";
    private static final String DISABLED = "Enable";
    
    private JSlider commSlider;
    private JButton enableButton;
    private Enronic enronic;
    
    public CommunityPanel(Enronic enronic) {
        this.enronic = enronic;
        
        commSlider = new JSlider();
        commSlider.setValue(0);
        commSlider.setPreferredSize(new Dimension(200,25));
        commSlider.setMaximumSize(new Dimension(200,25));
        commSlider.addChangeListener(this);
        commSlider.setEnabled(false);
        
        enableButton = new JButton(DISABLED);
        enableButton.addActionListener(this);
        
        final CommunitySet comm = (CommunitySet)enronic.getItemRegistry()
        	.getFocusManager().getFocusSet(Enronic.COMMUNITY_KEY);
        comm.addFocusListener(new FocusListener() {
            public void focusChanged(FocusEvent e) {
                commSlider.setModel(comm.getRange()); 
            } //
        });
        
        this.add(new JLabel("Community Analysis"));
        this.add(commSlider);
        this.add(enableButton);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    } //
    
    public void setForeground(Color c) {
        super.setForeground(c);
        if ( commSlider != null ) commSlider.setForeground(c);
        if ( enableButton != null ) enableButton.setForeground(c);
    } //
    
    public void setBackground(Color c) {
        super.setBackground(c);
        if ( commSlider != null ) commSlider.setBackground(c);
        if ( enableButton != null ) enableButton.setBackground(c);
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

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent arg0) {
        boolean enabled = (enableButton.getText() == ENABLED);
        enableButton.setText(enabled ? DISABLED : ENABLED);
        commSlider.setEnabled(!enabled);
        CommunitySet comm = (CommunitySet)enronic.getItemRegistry().
    		getFocusManager().getFocusSet(Enronic.COMMUNITY_KEY);
        if ( !enabled ) {
            comm.init(enronic.getItemRegistry());
        } else {
            comm.clear();
        }
        enronic.getBrowsingColorFunction().updateCommunityMap(comm);
    }
    
    
} // end of class CommunityPanel
