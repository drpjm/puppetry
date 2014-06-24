package edu.gatech.grits.puppetctrl.comm.bioloid;

import java.nio.*;
import javolution.util.FastList;
import edu.gatech.grits.puppetctrl.comm.serial.SerialPacket;

/**
 * Packet used to pass a sync_write command to the bioloid system. Primarily
 * used for coordinated motion.
 * @author pmartin
 *
 */
public class SyncMotorPacket extends SerialPacket {

	private FastList<BioloidMotorControl> bioloidControls;
	
	public SyncMotorPacket(){
		this.command = BioloidCommand.MOTION;
		bioloidControls = new FastList<BioloidMotorControl>();
	}
	
	public void addBioloidControl(BioloidMotorControl bc){
		bioloidControls.add(bc);
	}
	
	@Override
	public byte[] generateTxPacket() {
		this.outputBuffer = ByteBuffer.allocate(bioloidControls.size()*BioloidMotorControl.NUMBYTES + 3);
		outputBuffer.put(SerialPacket.START_BYTE);
		outputBuffer.put(command.getCmd());
		
		return null;
	}

	public static void main(String[] args){
		SyncMotorPacket smp = new SyncMotorPacket();
		
	}
}
