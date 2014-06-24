package edu.gatech.grits.puppetctrl.gui;

import java.awt.Dimension;

import javax.swing.*;

public class WaitPanel extends JPanel {

	private JProgressBar waitBar;
	private JLabel wait;
	
	public WaitPanel(){
		wait = new JLabel("Waiting for compilation...");
		
		waitBar = new JProgressBar();
		waitBar.setIndeterminate(true);
		
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(wait);
		this.add(waitBar);
		
	}
	
	public static void main(String[] args){
		
		WaitPanel wf = new WaitPanel();
		JFrame frame = new JFrame();
		frame.setSize(new Dimension(250,100));
		frame.add(wf);
		frame.setVisible(true);
		
	}
	
}
