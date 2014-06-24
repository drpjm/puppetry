package edu.gatech.grits.puppetctrl.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import javolution.util.FastList;

import edu.gatech.grits.puppetctrl.comm.serial.SerialCommunicable;

public class PacketPanel extends MyPanel {

	private JButton send;
	private JTextField input;
	private JTextField liftSpeedIn;
	private JTextField rotSpeedIn;
	private JTextField liftRangeIn;
	private JTextField rotRangeIn;
	
	public PacketPanel(){
		this.observers = new FastList<PanelObservable>();
		buildContent();
		buildLayout();
	}

	protected final void buildContent() {
		send = new JButton("Send");
		input = new JTextField("");
		input.setColumns(1);
		
		liftSpeedIn = new JTextField("0");
		rotSpeedIn = new JTextField("0");
		liftRangeIn = new JTextField("0");
		rotRangeIn = new JTextField("0");
		
		liftSpeedIn.setColumns(5);
		rotSpeedIn.setColumns(5);
		liftRangeIn.setColumns(5);
		rotRangeIn.setColumns(5);
				
		send.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String cmd = input.getText();
				ArrayList<Integer> data = new ArrayList<Integer>();
				data.add(Integer.valueOf(liftSpeedIn.getText()));
				data.add(Integer.valueOf(rotSpeedIn.getText()));
				data.add(Integer.valueOf(liftRangeIn.getText()));
				data.add(Integer.valueOf(rotRangeIn.getText()));
				for(PanelObservable po : observers){
					po.notifyChange(new ObserverPacket(MessageType.SEND_DATA, cmd));
				}
			}
		});
		
	}

	protected final void buildLayout() {
		JPanel panel = new JPanel();
		BoxLayout bl = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(bl);
		panel.add(new JLabel("Command:"));
		panel.add(input);
		
		panel.add(new JLabel("Appendage Params:"));
		panel.add(liftSpeedIn);
		panel.add(rotSpeedIn);
		panel.add(liftRangeIn);
		panel.add(rotRangeIn);
		
		panel.add(send);
		this.add(panel);
	}

	public void notifyChange(ObserverPacket message) {
		// TODO Auto-generated method stub
		
	}

}
