package enronic.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.util.ColorMap;
import enronic.Enronic;
import enronic.data.Message;

/**
 * Displays e-mail message contents and controls for visualizing individual
 * attributes.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class MessagePanel extends JPanel {

    protected ColorMap cmap = new ColorMap(ColorMap.getHSBMap(15,1f,1f),0,15);
    
    private JLabel        title;
    private JList         messages;
    private CategoryPanel categories;
    private JTextArea     content;
    private JScrollPane   listscroll, textscroll;
    private Runnable      viewPositionSetter;
    private int           curID;
    
    private Enronic enronic;
    
    public MessagePanel(Enronic enronic) {
        this.enronic = enronic;
        setBackground(Color.WHITE);
        initUI();
    } //
    
    private void initUI() {
        setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        
        title = new JLabel("Messages");
        title.setFont(new Font("SansSerif",Font.BOLD,12));        
        title.setBackground(Color.LIGHT_GRAY);
        
        Box titlebox = new Box(BoxLayout.X_AXIS);
        titlebox.add(Box.createHorizontalStrut(2));
        titlebox.add(title);
        titlebox.add(Box.createHorizontalGlue());
        titlebox.setBackground(Color.LIGHT_GRAY);
        titlebox.setMaximumSize(new Dimension(800,15));
        
        messages = new JList();
        messages.setCellRenderer(new MessageRenderer());
        listscroll = new JScrollPane(messages);
        listscroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listscroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        listscroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        messages.setVisibleRowCount(5);
        
        categories = new CategoryPanel();
        
        content  = new JTextArea();
        //content.setColumns(15);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setEditable(false);
        content.setFont(new Font("Courier New",Font.PLAIN,10));
        
        textscroll = new JScrollPane(content);
        textscroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textscroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        textscroll.setPreferredSize(new Dimension(300,500));
        
        viewPositionSetter = new Runnable() {
            public void run() {
                textscroll.getViewport().setViewPosition(new Point(0,0));
            } //
        };
        
        messages.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent arg0) {
                Message m = (Message)messages.getSelectedValue();
                String s = m == null ? "" : m.toString();
                content.setText(s);
                if ( m != null )
                    categories.setCounts(m.getCategories());
            } //
        });
        
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(titlebox);
        this.add(listscroll);
        this.add(categories);
        this.add(textscroll);
    } //
    
    public void setContent(Node n1, Node n2, List list) {
        String t = n1.getAttribute("label");
        if ( n2 != null )
            t +=" <--> "+n2.getAttribute("label");
        title.setText(t);
        curID = Integer.parseInt(n1.getAttribute("personid"));
        messages.setListData(list.toArray(new Message[list.size()]));
        messages.setSelectedIndex(0);
        content.setText(messages.getSelectedValue().toString());     
        this.validate();
        
        SwingUtilities.invokeLater(viewPositionSetter);
    } //
    
    public class CategoryPanel extends JPanel {
        private int[] counts;
        private CategoryLabel[] labels;
        public CategoryPanel() {
            this.setBackground(Color.WHITE);
            counts = new int[14];
            labels = new CategoryLabel[14];
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.add(Box.createHorizontalGlue());
            for ( int i = 0; i < labels.length; i++ ) {
                labels[i] = new CategoryLabel();
                labels[i].setText("  ");
                labels[i].setCategory(i);
                this.add(labels[i]);
                this.add(Box.createHorizontalGlue());
            }
        }
        public void setCounts(int[] counts) {
            for ( int i=0; i<labels.length; i++ ) {
                labels[i].setText(String.valueOf(counts[i]));
            }
        }
    } //
    
    public class CategoryLabel extends JLabel {
        protected ColorIcon icon = new ColorIcon();
        public CategoryLabel() {
            this.setIcon(icon);
        } //
        public void setCategory(int cat) {
            icon.setColor(getColor(cat));
        }
        protected Color getColor(int cat) {
            return (Color)cmap.getColor(cat);
        } //
    } //
    
    public class ColorIcon implements Icon {
        private Color color;
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
    
    public class PieIcon implements Icon {
        private int[] cats;
        private boolean left;
        public PieIcon(int[] categories, boolean left) {
            cats = categories;
            this.left = left;
        }
        public int getIconWidth() {
            return 20;
        } //
        public int getIconHeight() {
            return 10;
        } //
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2D = (Graphics2D)g;
            g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            double sum = 0;
            for ( int i=1; i < cats.length; i++ ) {
                sum += cats[i];
            }
            
            x += (left?0:10);
            
            double angle = 0;
            for ( int i=0; i<cats.length; i++ ) {
                if ( cats[i] > 0 ) {
                    double frac = 360.0*((double)cats[i])/sum;
                    Arc2D arc = new Arc2D.Float(x,y,10f,10f,
                                       			(float)angle,(float)frac,Arc2D.PIE);
                    angle += frac;
                    g2D.setPaint(cmap.getColor(i));
                    g2D.fill(arc);
                }
            }
            
            
            Ellipse2D circle = new Ellipse2D.Float(x,y,10,10);
            
    		g2D.setPaint(Color.LIGHT_GRAY);
    		g2D.draw(circle);
        } //
    } //
    
    public class MessageRenderer extends CategoryLabel implements ListCellRenderer {
        public Component getListCellRendererComponent(
                JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lab = new JLabel();
            Message m = (Message)value;
            String text = m.getTimestamp()+" "+m.getSubject(); 
            
            int sid = Integer.parseInt(m.getSender().getAttribute("personid"));
            lab.setText(text);
            
            boolean left = sid == curID;
            lab.setIcon(new PieIcon(m.getCategories(), left));
            if (isSelected) {
                lab.setBackground(Color.BLUE);
                lab.setForeground(Color.WHITE);
			} else {
			    lab.setBackground(list.getBackground());
			    lab.setForeground(list.getForeground());
			}
            lab.setOpaque(true);
            return lab;
        } //
    } //
    
} // end of class MessagePanel
