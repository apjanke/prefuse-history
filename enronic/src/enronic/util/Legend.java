package enronic.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;

import edu.berkeley.guir.prefuse.util.ColorMap;
import enronic.Enronic;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> enronic(AT)jheer.org</a>
 */
public class Legend extends JDialog {

    private static final String[] labels = {
            "company business, strategy, etc.",    
            "regulations and regulators (includes price caps)",
            "internal projects -- progress and strategy",
            "company image -- current",
            "company image -- changing / influencing",
            "political influence / contributions / contacts",
            "california energy crisis / california politics",
            "internal company policy",
            "internal company operations",
            "alliances / partnerships",
            "legal advice",
            "talking points",
            "meeting minutes",
            "trip reports"
    };
    
    protected ColorMap cmap = new ColorMap(ColorMap.getHSBMap(15,1f,1f),0,15);
    
    public Legend(Enronic enronic) {
        super(enronic, "Category Legend");
        setBackground(Color.WHITE);
        initUI();
        pack();
    } //
    
    private void initUI() {
        Container c = this.getContentPane();
        c.setBackground(Color.WHITE);
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        for ( int i=0; i<labels.length; i++ ) {
            JLabel lab = new JLabel(labels[i],
                    new ColorIcon((Color)cmap.getColor(i)), JLabel.LEFT);
            c.add(lab);
        }
    } //
    
    public class ColorIcon implements Icon {
        private Color color;
        public ColorIcon(Color c) {
            color = c;
        } //
        public void setColor(Color c) {
            color = c;
        } //
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillRect(2,2,10,10);
        } //
        public int getIconWidth() {
            return 10;
        } //
        public int getIconHeight() {
            return 10;
        } //
    } //
    
} // end of class Legend
