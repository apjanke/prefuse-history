package vizster.util;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import vizster.Vizster;

/**
 * The menubar for the Vizster application
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> vizster(AT)jheer.org
 */
public class VizsterMenuBar extends JMenuBar {

    static final String DBUG = "Toggle Debug Display";
    static final String EXIT = "Exit";
    static final String GOTO = "Go To Profile...";
    static final String FSIM = "Configure Force Simulator...";
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
        JMenu toolM = new JMenu("Tools");
        JMenu cmapM = new JMenu("ColorMaps");
        
        JMenuItem dbugI = new JMenuItem(DBUG);
        JMenuItem exitI = new JMenuItem(EXIT);
        JMenuItem gotoI = new JMenuItem(GOTO);
        JMenuItem fsimI = new JMenuItem(FSIM);
        JMenuItem gmapI = new JCheckBoxMenuItem(GMAP);
        JMenuItem hmapI = new JCheckBoxMenuItem(HMAP);
        JMenuItem cmapI = new JCheckBoxMenuItem(CMAP);
        
        ButtonGroup buttG = new ButtonGroup();
        buttG.add(gmapI); gmapI.setSelected(true);
        buttG.add(hmapI);
        buttG.add(cmapI);
        
        dbugI.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        gotoI.setAccelerator(KeyStroke.getKeyStroke("ctrl G"));
        fsimI.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
        
        gmapI.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
        hmapI.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
        cmapI.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
        
        exitI.setActionCommand(EXIT);
        gotoI.setActionCommand(GOTO);
        fsimI.setActionCommand(FSIM);
        gmapI.setActionCommand(GMAP);
        hmapI.setActionCommand(HMAP);
        cmapI.setActionCommand(CMAP);
        
        dbugI.addActionListener(new DebugInfoAction(vizster));
        exitI.addActionListener(new ExitAction());
        gotoI.addActionListener(new GotoAction(vizster));
        fsimI.addActionListener(new ForceConfigAction(vizster));
        
        ColorMapAction cmapA = new ColorMapAction(vizster);
        gmapI.addActionListener(cmapA);
        hmapI.addActionListener(cmapA);
        cmapI.addActionListener(cmapA);
        
        fileM.add(dbugI);
        fileM.add(exitI);
        toolM.add(gotoI);
        toolM.add(fsimI);
        cmapM.add(gmapI);
        cmapM.add(hmapI);
        cmapM.add(cmapI);
        
        add(fileM);
        add(toolM);
        add(cmapM);
    } //
    
} // end of class VizsterMenuBar
