package enronic.util;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
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
    static final String GMAP = "Grayscale";
    static final String HMAP = "Hot";
    static final String CMAP = "Cool";
    static final String ECOM = "Enable Community Analysis";
    static final String DCOM = "Disable Community Analysis";
    
    private Enronic enronic;
    
    public EnronicMenuBar(Enronic enronic) {
        this.enronic = enronic;
        initUI();
    } //
    
    private void initUI() {
        JMenu fileM = new JMenu("File");
        JMenu laytM = new JMenu("Layout");
        JMenu toolM = new JMenu("Tools");
        JMenu cmapM = new JMenu("ColorMaps");
        JMenu commM = new JMenu("Community");
        
        JMenuItem dbugI = new JMenuItem(DBUG);
        JMenuItem loadI = new JMenuItem(LOAD);
        JMenuItem connI = new JMenuItem(CONN);
        JMenuItem saveI = new JMenuItem(SAVE);
        JMenuItem exitI = new JMenuItem(EXIT);
        JMenuItem gotoI = new JMenuItem(GOTO);
        JMenuItem fsimI = new JMenuItem(FSIM);
        JMenuItem animI = new JMenuItem(ANIM);
        JMenuItem laytI = new JMenuItem(LAYT);
        JMenuItem gmapI = new JCheckBoxMenuItem(GMAP);
        JMenuItem hmapI = new JCheckBoxMenuItem(HMAP);
        JMenuItem cmapI = new JCheckBoxMenuItem(CMAP);
        JMenuItem ecomI = new JMenuItem(ECOM);
        JMenuItem dcomI = new JMenuItem(DCOM);
        
        ButtonGroup buttG = new ButtonGroup();
        buttG.add(gmapI); gmapI.setSelected(true);
        buttG.add(hmapI);
        buttG.add(cmapI);
        
        dbugI.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        saveI.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        gotoI.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));
        fsimI.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
        animI.setAccelerator(KeyStroke.getKeyStroke("ctrl K"));
        laytI.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
        gmapI.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
        hmapI.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
        cmapI.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
        
        exitI.setActionCommand(EXIT);
        loadI.setActionCommand(LOAD);
        connI.setActionCommand(CONN);
        saveI.setActionCommand(SAVE);
        gotoI.setActionCommand(GOTO);
        fsimI.setActionCommand(FSIM);
        animI.setActionCommand(ANIM);
        laytI.setActionCommand(LAYT);
        gmapI.setActionCommand(GMAP);
        hmapI.setActionCommand(HMAP);
        cmapI.setActionCommand(CMAP);
        ecomI.setActionCommand(ECOM);
        dcomI.setActionCommand(DCOM);
        
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
        
        ColorMapAction cmapA = new ColorMapAction(enronic);
        gmapI.addActionListener(cmapA);
        hmapI.addActionListener(cmapA);
        cmapI.addActionListener(cmapA);
        
        ecomI.addActionListener(new CommunityEnableAction(enronic));
        dcomI.addActionListener(new CommunityDisableAction(enronic));
        
        fileM.add(dbugI);
        fileM.add(loadI);
        fileM.add(connI);
        fileM.add(saveI);
        fileM.add(exitI);
        laytM.add(animI);
        laytM.add(laytI);
        toolM.add(gotoI);
        toolM.add(fsimI);
        cmapM.add(gmapI);
        cmapM.add(hmapI);
        cmapM.add(cmapI);
        commM.add(ecomI);
        commM.add(dcomI);
        
        add(fileM);
        add(laytM);
        add(toolM);
        add(cmapM);
        add(commM);
    } //
    
} // end of class enronicMenuBar
