package edu.gatech.grits.puppetctrl.gui;

import java.util.ArrayList;

public interface PanelObservable {

	public void notifyChange(ObserverPacket message);
	
}
