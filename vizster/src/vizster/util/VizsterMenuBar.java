package vizster.util;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import edu.berkeley.guir.prefuse.util.display.ExportDisplayAction;

import vizster.Vizster;

/**
 * The menubar for the Vizster application
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterMenuBar extends JMenuBar {

    static final String DBUG = "Toggle Debug Display";
    static final String LOAD = "Open Network File...";
    static final String CONN = "Connect to Network Database...";
    static final String SAVE = "Save Visible Network to File...";
    static final String EXPT = "Export Display Image...";
    static final String EXIT = "Exit";
    static final String GOTO = "Go To Profile...";
    static final String FSIM = "Configure Force Simulator...";
    static final String ANIM = "Toggle Animation";
    static final String LAYT = "Re-Compute Layout";
    static final String GMAP = "Grayscale";
    static final String HMAP = "Hot";
    static final String CMAP = "Cool";
    
    private Vizster vizster;
    
    public VizsterMenuBar(Vizster vizster) {
        this.vizster = vizster;
        initUI();
    } //
    
    private void initUI() {
        JMenu fileM = new JMenu("File");
        JMenu laytM = new JMenu("Layout");
        JMenu toolM = new JMenu("Tools");
        JMenu cmapM = new JMenu("ColorMaps");
        
        JMenuItem dbugI = new JMenuItem(DBUG);
        JMenuItem loadI = new JMenuItem(LOAD);
        JMenuItem connI = new JMenuItem(CONN);
        JMenuItem saveI = new JMenuItem(SAVE);
        JMenuItem exptI = new JMenuItem(EXPT);
        JMenuItem exitI = new JMenuItem(EXIT);
        JMenuItem gotoI = new JMenuItem(GOTO);
        JMenuItem fsimI = new JMenuItem(FSIM);
        JMenuItem animI = new JMenuItem(ANIM);
        JMenuItem laytI = new JMenuItem(LAYT);
        JMenuItem gmapI = new JCheckBoxMenuItem(GMAP);
        JMenuItem hmapI = new JCheckBoxMenuItem(HMAP);
        JMenuItem cmapI = new JCheckBoxMenuItem(CMAP);
        
        ButtonGroup buttG = new ButtonGroup();
        buttG.add(gmapI); gmapI.setSelected(true);
        buttG.add(hmapI);
        buttG.add(cmapI);
        
        dbugI.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        loadI.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        saveI.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        exptI.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
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
        exptI.setActionCommand(EXPT);
        gotoI.setActionCommand(GOTO);
        fsimI.setActionCommand(FSIM);
        animI.setActionCommand(ANIM);
        laytI.setActionCommand(LAYT);
        gmapI.setActionCommand(GMAP);
        hmapI.setActionCommand(HMAP);
        cmapI.setActionCommand(CMAP);
        
        dbugI.addActionListener(new DebugInfoAction(vizster));
        
        Action loadAction = new LoadNetworkAction(vizster);
        loadI.addActionListener(loadAction);
        connI.addActionListener(loadAction);
        
        saveI.addActionListener(new SaveVisibleNetworkAction(vizster));
        exptI.addActionListener(new ExportDisplayAction(vizster.getDisplay()));
        exitI.addActionListener(new ExitAction());
        gotoI.addActionListener(new GotoAction(vizster));
        fsimI.addActionListener(new ForceConfigAction(vizster));
        
        animI.addActionListener(new ToggleAnimationAction(vizster));
        laytI.addActionListener(new StaticLayoutAction(vizster));
        
        ColorMapAction cmapA = new ColorMapAction(vizster);
        gmapI.addActionListener(cmapA);
        hmapI.addActionListener(cmapA);
        cmapI.addActionListener(cmapA);
        
        fileM.add(dbugI);
        fileM.add(loadI);
        fileM.add(connI);
        fileM.add(saveI);
        fileM.add(exptI);
        fileM.add(exitI);
        laytM.add(animI);
        laytM.add(laytI);
        toolM.add(gotoI);
        toolM.add(fsimI);
        cmapM.add(gmapI);
        cmapM.add(hmapI);
        cmapM.add(cmapI);
        
        add(fileM);
        add(laytM);
        add(toolM);
        add(cmapM);
    } //
    
} // end of class VizsterMenuBar
