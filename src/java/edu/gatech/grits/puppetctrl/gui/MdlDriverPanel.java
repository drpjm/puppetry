package edu.gatech.grits.puppetctrl.gui;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.gatech.grits.puppetctrl.app.*;
import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidCommand;
import edu.gatech.grits.puppetctrl.comm.serial.SerialCommunicable;
import edu.gatech.grits.puppetctrl.mdl.util.ModeString;
import edu.gatech.grits.puppetctrl.util.Control;
import edu.gatech.grits.puppetctrl.util.PuppetDriver;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * JPanel which encapsulates the controls to start and stop MDL programs
 * on the puppet.
 * @author pmartin
 *
 */
public class MdlDriverPanel extends MyPanel {

	private JButton launchButton;
	private JButton stopButton;
	private JComboBox puppetBox;

	//TODO: make a list of puppet drivers for possible network control
	private PuppetDriver puppetDriver;
	private FastMap<String, ModeString> currPlayMap;

	public MdlDriverPanel(){
		observers = new FastList<PanelObservable>();
		currPlayMap = new FastMap<String, ModeString>();
		puppetDriver = new PuppetDriver(this);

		buildContent();
		buildLayout();
	}

	@Override
	protected void buildContent() {

		TitledBorder title;
		title = BorderFactory.createTitledBorder("Puppet Driver");
		this.setBorder(title);		

		launchButton = new JButton("Launch");
		launchButton.setEnabled(false);
		launchButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				//spawn a PuppetDriver to accept the current MDL play
				String selectedPuppet = (String) puppetBox.getSelectedItem();
				ModeString curr = currPlayMap.get(selectedPuppet);

				if(curr.getLength() != 0){
					puppetDriver.setCurrPlay(curr);
					//replace 'start' by launch command
					puppetDriver.launch();

					launchButton.setEnabled(false);
					stopButton.setEnabled(true);
					//notify observers that the play is running in the engine!
					for(PanelObservable po : observers){
						po.notifyChange(new ObserverPacket(MessageType.RUNNING, 
								new String("PuppetDriver")));
					}
					System.out.println("Play launched!");
				}
				else{
					System.out.println("Error: Puppet " + selectedPuppet + " has no script!");
				}
			}

		});
		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		stopButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				//tell puppet driver to stop timers completely.
				puppetDriver.stopPlay();
				launchButton.setEnabled(false);
				stopButton.setEnabled(false);
				puppetBox.setEnabled(false);
				puppetBox.removeAllItems();
				System.out.println("Stopping play.");
				for(PanelObservable po : observers){
					po.notifyChange(new ObserverPacket(MessageType.STOPPED, 
							new String("PuppetDriver")));
				}
			}

		});

		//		Vector<String> puppetNames = new Vector<String>();
		//		puppetNames.add("None");
		puppetBox = new JComboBox();
		puppetBox.setEnabled(false);

	}

	@Override
	protected void buildLayout() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
		puppetBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		controlPanel.add(puppetBox);		
		launchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		controlPanel.add(launchButton);
		stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		controlPanel.add(stopButton);

		this.add(controlPanel);
	}

	public void notifyChange(ObserverPacket message) {
		//enable only if new play was created
		MessageType mt = message.getMsgType();
		if(mt == MessageType.NEW_PLAY){

			System.out.println("Enable Puppet Driver!");
			this.currPlayMap = (FastMap<String, ModeString>)message.getData();
			this.enableControl();
			Vector<String> puppetNames = new Vector<String>();

			if(currPlayMap.size() != 0){			
				for (FastMap.Entry<String, ModeString> e = currPlayMap.head(), end = currPlayMap.tail(); (e = e.getNext()) != end;) {
					puppetNames.add(e.getKey());
					puppetBox.addItem(e.getKey());
				}			
			}

			puppetBox.setEnabled(true);

		}
		else if(mt == MessageType.PORT_OPEN){
			System.out.println("MDL Driver: Serial port open.");
		}
		else if(mt == MessageType.PORT_CLOSE){
			System.out.println("MDL Driver: Serial port closed.");
		}
	}

	public final void sendControl(Control ctrl){
//		System.out.println("MDLDriver received new control!");
		if(ctrl == null){
			//reset!
			for(PanelObservable po : observers){
				po.notifyChange(new ObserverPacket(MessageType.SEND_DATA,
						BioloidCommand.RESET));
			}
		}
		else{
			for(PanelObservable po : observers){
				po.notifyChange(new ObserverPacket(MessageType.SEND_DATA,
						ctrl.getMotion()));
			}
		}
	}

	public void printStatus(String statusString){
		System.out.println(statusString);
	}
	
	public final void enableControl(){
		launchButton.setEnabled(true);
	}
	public final void disableControl(){
		launchButton.setEnabled(false);
		stopButton.setEnabled(false);
	}

}
