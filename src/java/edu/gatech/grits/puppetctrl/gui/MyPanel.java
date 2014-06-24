package edu.gatech.grits.puppetctrl.gui;

import javax.swing.JPanel;

import javolution.util.FastList;

public abstract class MyPanel extends JPanel implements PanelObservable {

	protected FastList<PanelObservable> observers;
	
	protected abstract void buildContent();
	protected abstract void buildLayout();
	
	public final void addObserver(PanelObservable po){
		if(observers != null && !observers.contains(po)){
			observers.add(po);
		}
	}
}
