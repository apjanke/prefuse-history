package vizster.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * 
 * Apr 13, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class LoginDialog extends JDialog {

    private static final int LABELW = 75;
    private static final int FIELDW = 200;
    
    JTextField curFields[];
    
    public LoginDialog(JFrame owner) {
        super(owner, "Vizster Login", true);
        curFields = null;
        initUI();
        
        // set starting screen location in center
        Dimension screenSize =
            Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = this.getPreferredSize();
        setLocation(screenSize.width/2-(dialogSize.width/2),
                    screenSize.height/2-(dialogSize.height/2));
        screenSize = dialogSize = null;
    } //
    
    private void initUI() {
        getContentPane().setBackground(Color.WHITE);
        
        // set up Vizster logo
        URL logoU = LoginDialog.class.getResource("logo.png");
        ImageIcon logoI = new ImageIcon(logoU, "Vizster Logo");
        Dimension d = new Dimension(LABELW+FIELDW, 
                                    logoI.getIconHeight());
        JLabel logoL = new JLabel(logoI);
        logoL.setIconTextGap(0);
        logoL.setPreferredSize(d);
        logoL.setMaximumSize(d);
        d = null;
        
        Box l = new Box(BoxLayout.X_AXIS);
        l.add(logoL);
        l.setPreferredSize(d);
        l.setMaximumSize(d);
        
        // set up input fields
        JLabel inputL = new JLabel("Login:", SwingConstants.RIGHT);
        JLabel passwL = new JLabel("Password:", SwingConstants.RIGHT);
        JLabel dbhostL = new JLabel("DB URL:", SwingConstants.RIGHT);
        JLabel dbnameL = new JLabel("DB Name:", SwingConstants.RIGHT);
        
        final JTextField inputF = new JTextField("");
        final JTextField passwF = new JPasswordField("");
        final JTextField dbhostF = new JTextField("localhost");
        final JTextField dbnameF = new JTextField("friendster");
        
        final JButton loginB = new JButton("Login");
        final JButton cancelB = new JButton("Cancel");
        
        ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( e.getSource() == loginB )
                    curFields = new JTextField[] {
                        inputF, passwF, dbhostF, dbnameF};
                LoginDialog.this.hide();
            } //
        };
        loginB.addActionListener(al);
        cancelB.addActionListener(al);
        
        Box b = new Box(BoxLayout.X_AXIS);
        b.add(Box.createHorizontalGlue());
        b.add(loginB);
        b.add(Box.createHorizontalStrut(5));
        b.add(cancelB);
        b.add(Box.createHorizontalStrut(5));
        b.setPreferredSize(new Dimension(LABELW+FIELDW,30));
        b.setMaximumSize(new Dimension(LABELW+FIELDW,30));
        
        Box y = new Box(BoxLayout.Y_AXIS);
        y.add(Box.createVerticalStrut(15));
        y.add(Box.createVerticalGlue());
        y.add(l);
        y.add(Box.createVerticalGlue());
        y.add(Box.createVerticalStrut(15));
        y.add(getBox(inputL, inputF));
        y.add(getBox(passwL, passwF));
        y.add(getBox(dbhostL, dbhostF));
        y.add(getBox(dbnameL, dbnameF));
        y.add(Box.createVerticalStrut(5));
        y.add(b);
        y.add(Box.createVerticalGlue());
        y.add(Box.createVerticalStrut(10));
        
        Box x = new Box(BoxLayout.X_AXIS);
        x.add(Box.createHorizontalStrut(10));
        x.add(y);
        x.add(Box.createHorizontalStrut(10));
        
        getContentPane().add(x);
        pack();
    } //
    
    private Box getBox(JComponent c1, JComponent c2) {
        c1.setPreferredSize(new Dimension(LABELW,20));
        c1.setMaximumSize(new Dimension(LABELW,20));
        c2.setPreferredSize(new Dimension(FIELDW,20));
        c2.setMaximumSize(new Dimension(FIELDW,20));
        
        Box b = new Box(BoxLayout.X_AXIS);
        b.add(Box.createHorizontalGlue());
        b.add(c1);
        b.add(Box.createHorizontalStrut(10));
        b.add(c2);
        b.add(Box.createHorizontalGlue());
        
        b.setPreferredSize(new Dimension(LABELW+FIELDW,30));
        b.setMaximumSize(new Dimension(LABELW+FIELDW,30));
        return b;
    } //
    
    public String[] getLoginInfo() {
        if ( curFields == null )
            return null;
        else
            return new String[] { 
                curFields[2].getText(),
                curFields[3].getText(), 
                curFields[0].getText(), 
                curFields[1].getText()        
            };
    } //
    
} // end of class LoginDialog
