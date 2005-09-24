package prefusex.lucene;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ItemRegistryListener;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.graph.Entity;

/**
 * Provides keyword search over the currently visualized data.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class TextSearchPanel extends JPanel
    implements DocumentListener, ActionListener
{
    private TextSearchFocusSet searcher;
    private FocusSet focus;

    private JTextField queryF   = new JTextField(15);
    private JLabel     resultL = new JLabel();
    private JLabel     matchL  = new JLabel();
    private JLabel     searchL = new JLabel("search >> ");
    private IconButton upArrow = new IconButton(new ArrowIcon(ArrowIcon.UP),
            new ArrowIcon(ArrowIcon.UP_DEPRESSED));
    private IconButton downArrow = new IconButton(new ArrowIcon(ArrowIcon.DOWN),
            new ArrowIcon(ArrowIcon.DOWN_DEPRESSED));

    private String[] m_searchAttr;
    private Entity   m_results[];
    private int      m_curResult;
    
    private boolean m_includeSpinner = false;
    private boolean m_includeHitCount = false;

    private boolean m_autoIndex;
    
    public TextSearchPanel(String[] attr, ItemRegistry registry, boolean autoIndex) {
        m_searchAttr = attr;
        m_autoIndex = autoIndex;
        FocusManager fmanager = registry.getFocusManager();
        focus = fmanager.getDefaultFocusSet();
        FocusSet search = registry.getFocusManager()
        					.getFocusSet(FocusManager.SEARCH_KEY);
        if ( search != null ) {
            if ( search instanceof TextSearchFocusSet ) {
                searcher = (TextSearchFocusSet)search;
            } else {
                throw new IllegalStateException(
                    "Search focus set not instance of TextSearchFocusSet!");
            }
        } else {
            searcher = new TextSearchFocusSet();
            fmanager.putFocusSet(FocusManager.SEARCH_KEY, searcher);
        }
        init(registry);
    } //
    
    public TextSearchPanel(String[] attr, ItemRegistry registry, 
            TextSearchFocusSet searchSet, FocusSet focusSet) {
        this(attr,registry,searchSet,focusSet,true);
    } //
    
    public TextSearchPanel(String[] attr, ItemRegistry registry, 
            TextSearchFocusSet searchSet, FocusSet focusSet, boolean autoIndex) {
        m_searchAttr = attr;
        m_autoIndex = autoIndex;
        searcher = searchSet;
        focus = focusSet;
        init(registry);
    } //

    private void init(ItemRegistry registry) {
        if ( m_autoIndex ) {
	        // add a listener to dynamically build search index
	        registry.addItemRegistryListener(new ItemRegistryListener() {
	            public void registryItemAdded(VisualItem item) {
	                if ( !(item instanceof NodeItem) ) return;
	                for ( int i=0; i < m_searchAttr.length; i++ )
	                    searcher.index(item.getEntity(), m_searchAttr[i]);
	                //searchUpdate();
	            } //
	            public void registryItemRemoved(VisualItem item) {
	                if ( !(item instanceof NodeItem) ) return;
//	                for ( int i=0; i < searchAttr.length; i++ )
//  	                searcher.remove(item.getEntity(), searchAttr[i]);
//      	        searchUpdate();
	            } //
	        });
        }
        
        queryF.addActionListener(this);
        //queryF.getDocument().addDocumentListener(this);
        queryF.setMaximumSize(new Dimension(200, 20));
        queryF.setPreferredSize(new Dimension(200, 20));
        queryF.setBorder(null);

        upArrow.addActionListener(this);
        upArrow.setEnabled(false);
        downArrow.addActionListener(this);
        downArrow.setEnabled(false);
        
        matchL.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if ( matchL.getText().length() > 0 ) {
                    matchL.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            } //
            public void mouseExited(MouseEvent e) {
                if ( matchL.getText().length() > 0 ) {
                    matchL.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            } //
            public void mouseClicked(MouseEvent e) {
                if ( matchL.getText().length() > 0 ) {
                    focus.set(m_results[m_curResult]);
                }
            } //
        });
        
        setBackground(Color.WHITE);
        initUI();
    } //
    
    private void initUI() {
        this.removeAll();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        Box sb = new Box(BoxLayout.X_AXIS);
        sb.add(Box.createHorizontalStrut(3));
        sb.add(queryF);
        sb.add(Box.createHorizontalStrut(3));
        sb.add(new CloseButton());
        sb.add(Box.createHorizontalStrut(3));
        sb.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        sb.setMaximumSize(new Dimension(221, 20));
        sb.setPreferredSize(new Dimension(171, 20));
        
        Box b = new Box(BoxLayout.X_AXIS);
        if ( m_includeHitCount ) {
            b.add(resultL);
            b.add(Box.createHorizontalStrut(5));
            b.add(Box.createHorizontalGlue());
        }
        if ( m_includeSpinner ) {
	        b.add(matchL);
	        b.add(Box.createHorizontalStrut(5));
	        b.add(downArrow);
	        b.add(upArrow);
	        b.add(Box.createHorizontalStrut(5));
        }
        b.add(searchL);
        b.add(Box.createHorizontalStrut(3));
        b.add(sb);
        
        this.add(b);
    } //

    public void searchUpdate() {
        String query = queryF.getText();
        if ( query.length() == 0 ) {
            searcher.clear();
            resultL.setText("");
            matchL.setText("");
            downArrow.setEnabled(false);
            upArrow.setEnabled(false);
            m_results = null;
        } else {
            searcher.search(query);
            int r = searcher.size();
            resultL.setText(r + " match" + (r==1?"":"es"));
            m_results = new Entity[r];
            Iterator iter = searcher.iterator();
            for ( int i=0; iter.hasNext(); i++ ) {
                m_results[i] = (Entity)iter.next();
            }
            if ( r > 0 ) {
                String label = "name";
                matchL.setText("1/"+r+": " +
                        m_results[0].getAttribute(label));
                downArrow.setEnabled(true);
                upArrow.setEnabled(true);
            } else {
                matchL.setText("");
                downArrow.setEnabled(false);
                upArrow.setEnabled(false);
            }
            m_curResult = 0;
        }
        validate();
    } //
    
    public void setQuery(String query) {
        queryF.setText(query);
        searchUpdate();
    } //
    
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if ( queryF  != null ) queryF.setBackground(bg);
        if ( resultL != null ) resultL.setBackground(bg);
        if ( matchL  != null ) matchL.setBackground(bg);
        if ( searchL != null ) searchL.setBackground(bg);
        if ( upArrow != null ) upArrow.setBackground(bg);
        if ( downArrow != null ) downArrow.setBackground(bg);
    } //
    
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if ( queryF  != null ) {
            queryF.setForeground(fg);
            queryF.setCaretColor(fg);
        }
        if ( resultL != null ) resultL.setForeground(fg);
        if ( matchL  != null ) matchL.setForeground(fg);
        if ( searchL != null ) searchL.setForeground(fg);
        if ( upArrow != null ) upArrow.setForeground(fg);
        if ( downArrow != null ) downArrow.setForeground(fg);
    } //

    public void setLabelText(String text) {
        searchL.setText(text);
    } //
    
    public void changedUpdate(DocumentEvent e) {
        searchUpdate();
    } //
    public void insertUpdate(DocumentEvent e) {
        searchUpdate();
    } //
    public void removeUpdate(DocumentEvent e) {
        searchUpdate();
    } //

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if ( src == queryF ) {
            searchUpdate();
        } else if ( src == upArrow || src == downArrow ) {
	        if ( matchL.getText().length() == 0 ) return;
	        if ( e.getSource() == downArrow ) {
	            m_curResult = (m_curResult + 1) % m_results.length;
	        } else if ( e.getSource() == upArrow ) {
	            m_curResult = (m_curResult - 1) % m_results.length;
	            if ( m_curResult < 0 )
	                m_curResult += m_results.length;
	        }
	        String label = "name"; // TODO: generalize labeling
	        matchL.setText((m_curResult+1)+"/"+m_results.length+": " +
	                m_results[m_curResult].getAttribute(label));
	        validate();
	        repaint();
        }
    }//

    public class CloseButton extends JComponent implements MouseListener {
        private Image normalI, hoverI;
        private boolean hover = false;
        
        public CloseButton() {
            URL nrmUrl = TextSearchPanel.class.getResource("close.png");
            URL hovUrl = TextSearchPanel.class.getResource("close_hover.png");
            normalI = Toolkit.getDefaultToolkit().getImage(nrmUrl);
            hoverI = Toolkit.getDefaultToolkit().getImage(hovUrl);
            
            // load images immediately
            MediaTracker tracker = new MediaTracker(this);
    		tracker.addImage(normalI, 0);
    		tracker.addImage(hoverI, 1);
    		try {
    			tracker.waitForID(0, 0);
    			tracker.waitForID(1, 0);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		tracker.removeImage(normalI, 0);
    		tracker.removeImage(hoverI, 1);
    		tracker = null;
            
    		// set button size
            Dimension d = new Dimension(10,10);
            this.setPreferredSize(d);
            this.setMinimumSize(d);
            this.setMaximumSize(d);
            
            // add callbacks
            this.addMouseListener(this);
        } //
        
        public void paintComponent(Graphics g) {
            Image img = (hover ? hoverI : normalI);
            g.drawImage(img,0,0,10,10,null);
        } //

        public void mouseClicked(MouseEvent arg0) {
            setQuery(null);
        } //

        public void mousePressed(MouseEvent arg0) {
        } //

        public void mouseReleased(MouseEvent arg0) {
        } //

        public void mouseEntered(MouseEvent arg0) {
            hover = true;
            repaint();
        } //

        public void mouseExited(MouseEvent arg0) {
            hover = false;
            repaint();
        } //
        
    } // end of class CloseButton
    
    public class IconButton extends JButton {
        public IconButton(Icon icon1, Icon icon2) {
            super(icon1);
            if ( icon1.getIconWidth() != icon2.getIconWidth() ||
                    icon2.getIconHeight() != icon2.getIconHeight() ) {
                throw new IllegalArgumentException("Icons must have "
                        + "matching dimensions");
            }
            setPressedIcon(icon2);
            setDisabledIcon(new ArrowIcon(ArrowIcon.DISABLED));
            setBorderPainted(false);
            setFocusPainted(false);
            setBackground(getBackground());
            Insets in = getMargin();
            in.left = 0; in.right = 0;
            setMargin(in);
        } //
    } // end of class IconButton

    public class ArrowIcon implements Icon {
        public static final int UP = 0, UP_DEPRESSED = 1;
        public static final int DOWN = 2, DOWN_DEPRESSED = 3;
        public static final int DISABLED = 4;
        private int type;
        public ArrowIcon(int type) {
            this.type = type;
        } //
        public int getIconHeight() {
            return 11;
        } //
        public int getIconWidth() {
            return 11;
        } //
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if ( type >= DISABLED ) return;
            Polygon p = new Polygon();
            int w = getIconWidth();
            int h = getIconHeight();
            if ( type < DOWN ) {
                p.addPoint(x,y+h-1);
                p.addPoint(x+w-1,y+h-1);
                p.addPoint(x+(w-1)/2,y);
                p.addPoint(x,y+h);
            } else {
                p.addPoint(x,y);
                p.addPoint(x+w-1,y);
                p.addPoint(x+(w-1)/2,y+h-1);
                p.addPoint(x,y);
            }
            g.setColor((type%2!=0 ? Color.LIGHT_GRAY : getForeground()));
            g.fillPolygon(p);
            g.setColor(Color.BLACK);
            g.drawPolygon(p);
        } //
    } // end of inner class ArrowIcon
        
    
} // end of class TextSearchPanel
