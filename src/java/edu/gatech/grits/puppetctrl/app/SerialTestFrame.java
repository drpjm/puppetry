package edu.gatech.grits.puppetctrl.app;

import java.awt.*;
import java.nio.ByteBuffer;

import javax.swing.*;

import edu.gatech.grits.puppetctrl.comm.*;
import edu.gatech.grits.puppetctrl.comm.bioloid.*;
import edu.gatech.grits.puppetctrl.comm.serial.*;
import edu.gatech.grits.puppetctrl.gui.MessageType;
import edu.gatech.grits.puppetctrl.gui.ObserverPacket;
import edu.gatech.grits.puppetctrl.gui.PacketPanel;
import edu.gatech.grits.puppetctrl.gui.PanelObservable;
import edu.gatech.grits.puppetctrl.gui.SerialControlPanel;
import gnu.io.CommPortIdentifier;

/**
 * This class is used to startup the serial port to test with the bioloid controller.
 * 
 * SerialTestFrame.java
 * @author pmartin
 * Apr 16, 2007
 */
public class SerialTestFrame extends JFrame implements PanelObservable {

	private SerialControlPanel scp;
	private PacketPanel pp;
	
	private SerialCommunicable serialController;
	
	public SerialTestFrame(){
		buildContent();
		buildLayout();
		
		this.setTitle("Serial Test Application");
		this.setVisible(true);
		this.setEnabled(true);
		this.setSize(new Dimension(300,300));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	private final void buildContent(){
		scp = new SerialControlPanel();
		pp = new PacketPanel();
		scp.addObserver(this);
		pp.addObserver(this);
	}
	
	private final void buildLayout(){

		JPanel serialPanel = new JPanel();
		BoxLayout bl = new BoxLayout(serialPanel, BoxLayout.Y_AXIS);
		serialPanel.setLayout(bl);
		serialPanel.add(scp);
		serialPanel.add(pp);
		add(serialPanel);
		
	}
	
	public void notifyChange(ObserverPacket message) {
		if(message.getMsgType() == MessageType.PORT_OPEN){
			//create serial controller for port
			CommPortIdentifier portId = (CommPortIdentifier)message.getData();
			System.out.println("Create serial controller on port " + portId.getName());
			
			//default params for talking to the bioloid
			SerialPortParameters spp = new SerialPortParameters(portId, Baud.B57600);
			serialController = new SerialPortController(spp, this, new BioloidParser());
		}
		else if(message.getMsgType() == MessageType.PORT_CLOSE){
			
			String portId = (String)message.getData();
			System.out.println("Close port " + portId);
			serialController.closePortRequest();
			
		}
		else if(message.getMsgType() == MessageType.SEND_DATA){
			String msgToSend = (String)message.getData();
			SerialPacket sp;
			
			Character command = msgToSend.charAt(0);
			System.out.println("Command: " + command);
			if(command.compareTo('S') == 0){
				sp = new StatusPacket();
				System.out.println("Request Bioloid status.");
			}
			else if(command.compareTo('M') == 0){
				sp = new MotionPacket();
				System.out.println("Send motion packet.");
				//TODO: TEST CODE
//				((MotionPacket)sp).addAppendageData(createTestAppendage(10, 0, 0, 0));
			}
			else{
				sp = new StatusPacket();
			}
			
			serialController.sendData(sp.generateTxPacket());
		}
	}

//	private final AppendageData createTestAppendage(float liftSp, float rotSp, float liftRng, float rotRng){
//		AppendageData ad = new AppendageData(Appendage.LEFT_ARM, (byte)1);
//		
//		ad.setLiftSpeed(DynamixelParams.approxRPM(liftSp));
//		ad.setLiftRange(DynamixelParams.approxDegree(liftRng));
//		ad.setRotateRange((short)0);
//		ad.setRotateSpeed((short)0);
//		
//		return ad;
//	}
	

	public static void main(String[] args){
		float goalDeg = 300;
		short approxPos = DynamixelParams.approxDegree(goalDeg);
		System.out.println(Integer.toHexString((int)approxPos));
		
		float goalRad = (float)(300 * (Math.PI / 180));
		short approxPosRad = DynamixelParams.approxRadian(goalRad);
		System.out.println(Integer.toHexString((int)approxPosRad));
		
		float testrps = 11.938057f;
		short approxVal = DynamixelParams.approxRadPerSec(testrps);
		System.out.println(Integer.toHexString((int)approxVal));
		
		float testrpm = 114;
		short a = DynamixelParams.approxRPM(testrpm);
		System.out.println(Integer.toHexString((int)a));
		
		
		SerialTestFrame stf = new SerialTestFrame();
	}

}
