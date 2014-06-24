package edu.gatech.grits.puppetctrl.gui;

import java.awt.*;
import java.awt.event.*;
import java.nio.ByteBuffer;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.*;

import javolution.util.FastList;

import edu.gatech.grits.puppetctrl.comm.bioloid.*;
import edu.gatech.grits.puppetctrl.comm.serial.*;
import gnu.io.CommPortIdentifier;

/**
 * A gui panel used to control what port to control puppets.
 * @author pmartin
 *
 */
public class SerialControlPanel extends MyPanel {
	
	private JComboBox serialPortsBox;
	private JButton openButton;
	private JButton closeButton;
	
	private String selPort;
	private Vector<CommPortIdentifier> portsList;
	//access to serial port
	private SerialCommunicable serialComms;
	private boolean isSerialPortOpen;
	
	public SerialControlPanel(){
		observers = new FastList<PanelObservable>();
		isSerialPortOpen = false;
		buildContent();
		buildLayout();
	}
	
	protected final void buildContent(){
		
		//panel properties
		TitledBorder serialTitle;
		serialTitle = BorderFactory.createTitledBorder("Serial Port");
		this.setBorder(serialTitle);
		
		SerialDetector sd = new SerialDetector();
		portsList = sd.getCommPortList();
		Vector<String> portNames = new Vector<String>();
		if(portsList.size() != 0){
			for(CommPortIdentifier commId : portsList){
				portNames.add(commId.getName());
			}
			serialPortsBox = new JComboBox(portNames);
		}
		else{
			portNames.add("NONE");
			serialPortsBox = new JComboBox(portNames);
			serialPortsBox.setEnabled(false);
		}

		openButton = new JButton("Open");
		openButton.setEnabled(false);
		
		closeButton = new JButton("Close");
		closeButton.setEnabled(false);
		
		//if there is only one port, enable open button
		if(portsList.size() == 1){
			selPort = (String)serialPortsBox.getSelectedItem();
			openButton.setEnabled(true);
		}
		
		//add action listeners to objects
		serialPortsBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent ie) {
				if(ie.getStateChange() == ItemEvent.SELECTED){
					selPort = (String)serialPortsBox.getSelectedItem();
					openButton.setEnabled(true);
				}
			}
		});
		
		openButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				openButton.setEnabled(false);
				closeButton.setEnabled(true);
				serialPortsBox.setEnabled(false);
				//create serial thread
				CommPortIdentifier commId = findPort(selPort);
				attach(commId);
				
				while(!serialComms.isReady());	//wait until thread is running
				//send message to observers
				for(PanelObservable po : observers){
					po.notifyChange(new ObserverPacket(MessageType.PORT_OPEN, selPort));
				}
				
			}
		});
		
		closeButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				serialPortsBox.setEnabled(true);
				if(portsList.size() == 1){
					openButton.setEnabled(true);
				}
				closeButton.setEnabled(false);
				System.out.println("Close port " + selPort);
				serialComms.closePortRequest();
				for(PanelObservable po : observers){
					po.notifyChange(new ObserverPacket(MessageType.PORT_CLOSE, selPort));
				}
			}
			
		});
	}

	private final void attach(CommPortIdentifier commId){
		System.out.println("Create serial controller on port " + commId.getName());
		//default params for talking to the bioloid
		SerialPortParameters spp = new SerialPortParameters(commId, Baud.B57600);
		serialComms = new SerialPortController(spp, this, new BioloidParser());
		this.isSerialPortOpen = true;
	}
	
	private final CommPortIdentifier findPort(String name){
		CommPortIdentifier outId = null;
		for(CommPortIdentifier id : portsList){
			if(id.getName().equals(name)){
				outId = id;
			}
			else
				outId = null;
		}
		return outId;
	}
	
	protected final void buildLayout(){
		//arrange combo box
		JPanel portSelector = new JPanel();
		portSelector.setLayout(new BoxLayout(portSelector, BoxLayout.PAGE_AXIS));
		portSelector.add(serialPortsBox);
		serialPortsBox.setAlignmentX(Component.CENTER_ALIGNMENT);
		portSelector.add(Box.createRigidArea(new Dimension(0,5)));
		portSelector.add(openButton);
		openButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		portSelector.add(Box.createRigidArea(new Dimension(0,2)));
		portSelector.add(closeButton);
		closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.add(portSelector);
		
		this.setMaximumSize(new Dimension(150,125));
	}

	public void notifyChange(ObserverPacket message) {
		if(message.getMsgType() == MessageType.SEND_DATA){
			if(this.isSerialPortOpen){
				if(message.getData() instanceof BioloidCommand){
					BioloidCommand bc = (BioloidCommand)message.getData();
					if(bc == BioloidCommand.STATUS){
						//status command
						StatusPacket sp = new StatusPacket();
						serialComms.sendData(sp.generateTxPacket());
					}
					else if(bc == BioloidCommand.RESET){
						System.err.println("Reset the puppet's motors.");
						//reset command
						ResetPacket rp = new ResetPacket();
						serialComms.sendData(rp.generateTxPacket());
					}
				}
				else{
					//we have a packet to send
					System.out.println("SCP: Send data!");
					SerialPacket sp = (SerialPacket)message.getData();
					byte[] tester = sp.generateTxPacket();
					for(byte b : tester){
						System.out.print(b + "|");
					}
					serialComms.sendData(tester);
				}
			}
		}
		else if(message.getMsgType() == MessageType.PRINT){
			System.out.println(message.getData());
		}
		//Pass message UP
		else if(message.getMsgType() == MessageType.NEW_DATA){
			for(PanelObservable po : observers){
				po.notifyChange(message);
			}
		}
	}

}
