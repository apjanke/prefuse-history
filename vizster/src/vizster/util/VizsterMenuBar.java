package vizster.util;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import vizster.Vizster;

/**
 * 
 * Apr 13, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterMenuBar extends JMenuBar {

    private static final String EXIT = "Exit";
    private static final String GOTO = "Go To Profile...";
    
    private Vizster vizster;
    
    public VizsterMenuBar(Vizster vizster) {
        this.vizster = vizster;
        initUI();
    } //
    
    private void initUI() {
        JMenu fileM = new JMenu("File");
        JMenu toolM = new JMenu("Tools");
        
        JMenuItem exitI = new JMenuItem(EXIT);
        JMenuItem gotoI = new JMenuItem(GOTO);
        
        gotoI.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));
        
        exitI.setActionCommand(EXIT);
        gotoI.setActionCommand(GOTO);
        
        exitI.addActionListener(new ExitAction());
        gotoI.addActionListener(new GotoAction(vizster));
        
        fileM.add(exitI);
        toolM.add(gotoI);
        
        add(fileM);
        add(toolM);
    } //
    
} // end of class VizsterMenuBar
