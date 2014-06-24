package edu.gatech.grits.puppetctrl.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ConsolePanel extends MyPanel {

	private Console console;
	private PrintStream out;
	
	public ConsolePanel(){
		buildContent();
		buildLayout();
	}

	@Override
	protected final void buildContent() {
		try {
			console = new Console();
		} catch (IOException e) {
			System.err.println("Could not grab IO stream.");
			e.printStackTrace();
		}
	}

	@Override
	protected final void buildLayout() {
		this.setLayout(new BorderLayout());
		this.add(console, BorderLayout.CENTER);
	}

	public void notifyChange(ObserverPacket message) {
		// TODO Auto-generated method stub
		
	}

}
