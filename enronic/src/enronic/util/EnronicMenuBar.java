package enronic.util;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import enronic.Enronic;


/**
 * The menubar for the enronic application
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class EnronicMenuBar extends JMenuBar {

    static final String DBUG = "Toggle Debug Display";
    static final String LOAD = "Load Network from File...";
    static final String CONN = "Connect to Network Database...";
    static final String SAVE = "Save Visible Network...";
    static final String EXIT = "Exit";
    static final String GOTO = "Go To Profile...";
    static final String FSIM = "Configure Force Simulator...";
    static final String ANIM = "Toggle Animation";
    static final String LAYT = "Re-Compute Layout";
    
    private Enronic enronic;
    
    public EnronicMenuBar(Enronic enronic) {
        this.enronic = enronic;
        initUI();
    } //
    
    private void initUI() {
        JMenu fileM = new JMenu("File");
        JMenu laytM = new JMenu("Layout");
        JMenu toolM = new JMenu("Tools");
        
        JMenuItem dbugI = new JMenuItem(DBUG);
        JMenuItem loadI = new JMenuItem(LOAD);
        JMenuItem connI = new JMenuItem(CONN);
        JMenuItem saveI = new JMenuItem(SAVE);
        JMenuItem exitI = new JMenuItem(EXIT);
        JMenuItem gotoI = new JMenuItem(GOTO);
        JMenuItem fsimI = new JMenuItem(FSIM);
        JMenuItem animI = new JMenuItem(ANIM);
        JMenuItem laytI = new JMenuItem(LAYT);
        
        dbugI.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        saveI.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        gotoI.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));
        fsimI.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
        animI.setAccelerator(KeyStroke.getKeyStroke("ctrl K"));
        laytI.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
        
        exitI.setActionCommand(EXIT);
        loadI.setActionCommand(LOAD);
        connI.setActionCommand(CONN);
        saveI.setActionCommand(SAVE);
        gotoI.setActionCommand(GOTO);
        fsimI.setActionCommand(FSIM);
        animI.setActionCommand(ANIM);
        laytI.setActionCommand(LAYT);
        
        dbugI.addActionListener(new DebugInfoAction(enronic));
        
        Action loadAction = new LoadNetworkAction(enronic);
        loadI.addActionListener(loadAction);
        connI.addActionListener(loadAction);
        
        saveI.addActionListener(new SaveVisibleNetworkAction(enronic));
        exitI.addActionListener(new ExitAction());
        gotoI.addActionListener(new GotoAction(enronic));
        fsimI.addActionListener(new ForceConfigAction(enronic));
        
        animI.addActionListener(new ToggleAnimationAction(enronic));
        laytI.addActionListener(new StaticLayoutAction(enronic));
        
        fileM.add(dbugI);
        fileM.add(loadI);
        fileM.add(connI);
        fileM.add(saveI);
        fileM.add(exitI);
        laytM.add(animI);
        laytM.add(laytI);
        toolM.add(gotoI);
        toolM.add(fsimI);
        
        add(fileM);
        add(laytM);
        add(toolM);
    } //
    
} // end of class enronicMenuBar
