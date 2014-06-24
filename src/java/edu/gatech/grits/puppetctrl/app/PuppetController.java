package edu.gatech.grits.puppetctrl.app;

import java.awt.*;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import edu.gatech.grits.puppetctrl.comm.serial.Baud;
import edu.gatech.grits.puppetctrl.comm.serial.SerialCommunicable;
import edu.gatech.grits.puppetctrl.comm.serial.SerialPortController;
import edu.gatech.grits.puppetctrl.comm.serial.SerialPortParameters;
import edu.gatech.grits.puppetctrl.gui.*;
import gnu.io.CommPortIdentifier;

/**
 * This class creates the high level gui that will be used to launch various elements of the Puppet Control
 * application.
 * 
 * @author pmartin
 *
 */
public class PuppetController extends JFrame implements PanelObservable {

	private TopPanel topPanel;
	private ConsolePanel consolePanel;
	private JSplitPane splitPane;
	
	public PuppetController(){
		buildContent();
		buildLayout();
	}
	
	private final void buildContent(){
		//frame properties
		this.setTitle("Puppet Controller");
		this.setEnabled(true);
		this.setVisible(true);
		this.setSize(new Dimension(640,550));
		this.setMinimumSize(new Dimension(640, 550));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		topPanel =  new TopPanel();
		topPanel.addObserver(this);
		consolePanel = new ConsolePanel();
		
	}

	private final void buildLayout(){
		this.add(topPanel);
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, consolePanel);
		splitPane.setDividerLocation(300);
		this.add(splitPane);
	}

	public void notifyChange(ObserverPacket message) {
	}

	private static void createGui(){
		PuppetController pcf = new PuppetController();
	}
	
	public static void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable(){

			public void run() {
				createGui();
			}
			
		});
		
	}

}
