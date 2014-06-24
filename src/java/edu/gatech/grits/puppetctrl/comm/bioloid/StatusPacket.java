package edu.gatech.grits.puppetctrl.comm.bioloid;

import java.nio.*;

import edu.gatech.grits.puppetctrl.comm.serial.SerialPacket;

public class StatusPacket extends SerialPacket {

	public StatusPacket(){
		command = BioloidCommand.STATUS;
		this.outputBuffer = ByteBuffer.allocate(3);
		outputBuffer.put(SerialPacket.START_BYTE);
		outputBuffer.put(command.getCmd());
		outputBuffer.put((byte)0x00);
	}

	@Override
	public byte[] generateTxPacket() {
		return outputBuffer.array();
	}
}
