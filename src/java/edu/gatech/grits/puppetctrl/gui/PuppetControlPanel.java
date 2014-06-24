package edu.gatech.grits.puppetctrl.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.gatech.grits.puppetctrl.comm.bioloid.*;
import edu.gatech.grits.puppetctrl.util.*;

import javolution.util.FastList;

/**
 * This class contains a tabbed pane with both single motor and motion control panels.
 * @author pmartin
 *
 */
public class PuppetControlPanel extends MyPanel {

	//	private JTabbedPane tabbedPane;
//	private DirectControlPanel dcp;

	//controls subpanel
	private JButton statusButton;
	private JButton resetButton;	
	private JButton actionButton;

	public PuppetControlPanel(){
		this.observers = new FastList<PanelObservable>();
		buildContent();
		buildLayout();
	}

	@Override
	protected void buildContent() {

		TitledBorder border = BorderFactory.createTitledBorder("Puppet Controls");
		this.setBorder(border);

		statusButton = new JButton("Get Status");
		statusButton.setEnabled(false);
		statusButton.setToolTipText("Get Bioloid's current status.");
		statusButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				for(PanelObservable po : observers){
					po.notifyChange(new ObserverPacket(MessageType.SEND_DATA,
							BioloidCommand.STATUS));
				}
			}

		});

		resetButton = new JButton("Reset");
		resetButton.setEnabled(false);
		resetButton.setToolTipText("Reset the Bioloid system to start mode.");
		resetButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				for(PanelObservable po : observers){
					po.notifyChange(new ObserverPacket(MessageType.SEND_DATA,
							BioloidCommand.RESET));
				}
			}

		});

//		actionButton = new JButton("Action!");
//		actionButton.setEnabled(false);
//		actionButton.setToolTipText("Send action command to Bioloid");
//		actionButton.addActionListener(new ActionListener(){
//
//			public void actionPerformed(ActionEvent arg0) {
//
//				//hardwired: 0 = direct control, 1 = motion control
//				System.out.println("Sending command direct to motor!");
//				Float speed = dcp.getSpeed();
//				Float range = dcp.getRange();
//				short convertedSpeed = 0;
//				short convertedRange = 0;
//				if(dcp.getConversionSelected() == Conversion.DEG){
//					convertedSpeed = DynamixelParams.approxDegPerSec(speed);
//					convertedRange = DynamixelParams.approxDegree(range);
//					System.out.println("Deg: Move motor to " + convertedRange + " @ " + convertedSpeed);
//				}
//				else if(dcp.getConversionSelected() == Conversion.RAD){
//					convertedSpeed = DynamixelParams.approxRadPerSec(speed);
//					convertedRange = DynamixelParams.approxRadian(range);
//					System.out.println("Rad: Move motor to " + convertedRange + " @ " + convertedSpeed);
//				}
//				//make serial packet to send
//				DirectControlPacket packet = new DirectControlPacket();
//				packet.setId((byte)dcp.getSelectedMotor());
//				packet.setRange(convertedRange);
//				packet.setSpeed(convertedSpeed);
//				for(PanelObservable po : observers){
//					po.notifyChange(new ObserverPacket(MessageType.SEND_DATA, packet));
//				}
//
//			}
//
//		});
//
//		dcp = new DirectControlPanel();
	}

	@Override
	protected void buildLayout() {
		JPanel mainPanel = new JPanel();
		BoxLayout mainManager = new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS);
		mainPanel.setLayout(mainManager);

//		mainPanel.add(dcp);

		JPanel controlSub = new JPanel();
		BoxLayout bl = new BoxLayout(controlSub, BoxLayout.LINE_AXIS);
		controlSub.setLayout(bl);
		controlSub.add(statusButton);
		controlSub.add(resetButton);
//		controlSub.add(actionButton);
		mainPanel.add(controlSub);

		add(mainPanel);
	}

	//user availability methods
	public void enableControl(){
		this.statusButton.setEnabled(true);
		this.resetButton.setEnabled(true);
	}
	public void disableControl(){
		this.statusButton.setEnabled(false);
		this.resetButton.setEnabled(false);
	}

	public void notifyChange(ObserverPacket message) {
		Object data = message.getData();
		if(message.getMsgType() == MessageType.PORT_CLOSE){
			statusButton.setEnabled(false);
			resetButton.setEnabled(false);
//			dcp.disableMotion();
//			actionButton.setEnabled(false);
		}
		else if(message.getMsgType() == MessageType.PORT_OPEN){
			statusButton.setEnabled(true);
			resetButton.setEnabled(true);
//			dcp.enableMotion();
//			actionButton.setEnabled(true);
		}
		else if(message.getMsgType() == MessageType.NEW_DATA){
			//return from bioloid commands
			if(data instanceof BioloidData){
				BioloidCommand cmd = ((BioloidData)data).getCommand();
				switch(cmd){
				//if we have a status return, change display
				case STATUS:
//					FastList<Integer> list = ((BioloidData)data).getMotorIds();
//					dcp.setMotorList(list);
					break;
				case RESET:
					System.out.println("Motors reset!");
					break;
				default:
					break;
				}
			}
		}
	}

//	public static void main(String[] args){
//		JFrame test = new JFrame();
//		test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		test.setVisible(true);
//		test.add(new PuppetControlPanel());
//	}
}
