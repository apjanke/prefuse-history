package vizster.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import vizster.Vizster;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.util.KeywordSearchFocusSet;

/**
 * 
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class ProfilePanel extends JPanel {

    private JLabel[]    labels;
    private JTextArea[] values;
    
    private Entity curProfile;
    private KeywordSearchFocusSet searcher;
    private HighlightPainter hlp = new DefaultHighlighter
                                       .DefaultHighlightPainter(Color.YELLOW);
    
    public ProfilePanel(Vizster vizster) {
        setBackground(Color.WHITE);
        initUI();
        
        FocusManager fmanager = vizster.getItemRegistry().getFocusManager();
        searcher = (KeywordSearchFocusSet)fmanager.getFocusSet(Vizster.SEARCH_KEY);
        searcher.addFocusListener(new FocusListener() {
            public void focusChanged(FocusEvent e) {
                updateTextHighlight();
            } //
        });
    } //
    
    private void initUI() {
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        
        Font f = new Font("SansSerif",Font.BOLD,12);
        Color bgcolor = new Color(200,200,230);
        
        labels = new JLabel[LABEL.length];
        for ( int i=1; i < LABEL.length; i++ ) {
            labels[i] = new JLabel(LABEL[i]);
            labels[i].setHorizontalAlignment(SwingConstants.RIGHT);
            labels[i].setVerticalAlignment(SwingConstants.TOP);
            labels[i].setFont(f);
            if ( i%2 != 0 )
                labels[i].setBackground(bgcolor);
        }
        
        f = new Font("SansSerif",Font.PLAIN,12);
        
        values = new JTextArea[ATTR.length];
        for ( int i=1; i < ATTR.length; i++ ) {
            values[i] = new JTextArea();
            values[i].setColumns(15);
            values[i].setLineWrap(true);
            values[i].setWrapStyleWord(true);
            values[i].setFont(f);
            values[i].setEditable(false);
            if ( i%2 != 0 )
                values[i].setBackground(bgcolor);
        }
        
        values[0] = new JTextArea();
        values[0].setEditable(false);
        values[0].setFont(new Font("SansSerif",Font.BOLD,32));
        Box b = new Box(BoxLayout.X_AXIS);
        b.add(Box.createHorizontalGlue());
        b.add(values[0]);
        b.add(Box.createHorizontalGlue());
        
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gbl);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.SOUTH;
        gbl.setConstraints(b, c);
        add(b);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTH;
        for ( int i=1; i < LABEL.length; i++ ) {
            addAttribute(i,gbl,c);
        }
    } //
    
    private void addAttribute(int i, GridBagLayout gbl, GridBagConstraints c) {
        c.gridwidth = 1;
        gbl.setConstraints(labels[i], c);
        add(labels[i]);
        
        Component space = Box.createHorizontalStrut(10);
        gbl.setConstraints(space, c);
        add(space);
        
        c.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(values[i], c);
        add(values[i]);
    } //
    
    public void updatePanel(Entity e) {
        curProfile = e;
        for ( int i=ATTR.length-1; i >= 0; i-- ) {
            String val = e.getAttribute(ATTR[i]);
            if ( val == null ) val = "??";
            values[i].setText(val);
        }
        updateTextHighlight();
    } //
    
    private void updateTextHighlight() {
        for ( int i=ATTR.length-1; i >= 0; i--)
            values[i].getHighlighter().removeAllHighlights();
        if ( curProfile != null && searcher.contains(curProfile) ) {
            String query = searcher.getQuery().toLowerCase();
            for ( int i=ATTR.length-1; i >= 0; i--)
                updateTextHighlight(values[i], query);
        }
    } //
    
    private void updateTextHighlight(JTextArea value, String query) {
        String text = value.getText().toLowerCase();
        Highlighter hl = value.getHighlighter();
        int idx = 0, len = query.length();
        while ( (idx=text.indexOf(query, idx)) != -1 ) {
            try {
                hl.addHighlight(idx, idx+len, hlp);
            } catch ( Exception e ) {}
            idx += len;
        }
    } //
    
    public static final String[] ATTR = {
        "name",
        "uid",
        "age",
        "gender",
        "status",
/*        "interested_in",
        "preference",*/
        "location",
        "hometown",
        "occupation",
        "interests",
        "music",
        "books",
        "tvshows",
        "movies",
        "membersince",
        "lastlogin",
        "lastmod",
        "about",
        "want_to_meet"
    };
    
    public static final String[] LABEL = {
        "Name",
        "User ID",
        "Age",
        "Gender",
        "Status",
/*        "interested_in",
        "preference",*/
        "Location",
        "Hometown",
        "Occupation",
        "Interests",
        "Music",
        "Books",
        "TV Shows",
        "Movies",
        "Member Since",
        "Last Login",
        "Last Updated",
        "About",
        "Want to Meet"
    };
    
// Attributes are...
//    "uid",
//    "name",
//    "age",
//    "gender",
//    "status",
//    "interested_in",
//    "preference",
//    "location",
//    "hometown",
//    "occupation",
//    "interests",
//    "music",
//    "books",
//    "tvshows",
//    "movies",
//    "membersince",
//    "lastlogin",
//    "lastmod",
//    "about",
//    "want_to_meet",
//    "photourl"
    
} // end of class ProfilePanel
