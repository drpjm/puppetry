package edu.gatech.grits.puppetctrl.comm.bioloid;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import edu.gatech.grits.puppetctrl.comm.serial.SerialPacket;

import javolution.util.FastList;

/**
 * Class that contains a group of AppendagePackets for Bioloid control.
 * 
 * MotionPacket.java
 * @author pmartin
 * Apr 18, 2007
 */
public class MotionPacket extends SerialPacket {

	private FastList<BioloidMotorControl> bioloidControls;
	
	public MotionPacket(){
		command = BioloidCommand.MOTION;
		bioloidControls = new FastList<BioloidMotorControl>();
	}
	
	public void addBioloidControl(BioloidMotorControl bc){
		bioloidControls.add(bc);
	}

	public void setBioloidControls(FastList<BioloidMotorControl> bioloidControls) {
		this.bioloidControls = bioloidControls;
	}

	@Override
	public byte[] generateTxPacket() {
		
		this.outputBuffer = ByteBuffer.allocate(bioloidControls.size()*BioloidMotorControl.NUMBYTES + 3);
		outputBuffer.put(SerialPacket.START_BYTE);
		outputBuffer.put(command.getCmd());
		outputBuffer.put((byte)(bioloidControls.size()*BioloidMotorControl.NUMBYTES));
		for(BioloidMotorControl bmc : bioloidControls){
			outputBuffer.put(bmc.generateTxArray());
		}
		return outputBuffer.array();
	}

	@Override
	public String toString() {
		String out = "";
		out += "Packet[" + command + "] - Size = " + bioloidControls.size() + "\r\n";
		for(BioloidMotorControl bmc : bioloidControls){
			out += "	" + bmc + "\r\n";
		}
		return out;
	}

	public static void main(String[] args){
		BioloidMotorControl bmc1 = new BioloidMotorControl(1);
		BioloidMotorControl bmc2 = new BioloidMotorControl(2);
		BioloidMotorControl bmc3 = new BioloidMotorControl(3);
		
		bmc1.setPosition((short)270);
		bmc1.setVelocity((short)90);
		bmc2.setPosition((short)150);
		bmc2.setVelocity((short)30);
		bmc3.setPosition((short)150);
		bmc3.setVelocity((short)90);
		
		MotionPacket mp = new MotionPacket();
		mp.addBioloidControl(bmc1);
		mp.addBioloidControl(bmc2);
		mp.addBioloidControl(bmc3);
		
		byte[] output = mp.generateTxPacket();
		for(byte b : output){
			System.out.print(b + "|");
		}
	}
}
