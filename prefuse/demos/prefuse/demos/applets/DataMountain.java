package prefuse.demos.applets;

import prefuse.util.ui.JPrefuseApplet;


public class DataMountain extends JPrefuseApplet {

    @Override
	public void init() {
        this.setContentPane(prefuse.demos.DataMountain.demo());
    }

} // end of class DataMountain
