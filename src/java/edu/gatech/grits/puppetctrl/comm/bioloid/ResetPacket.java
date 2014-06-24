package edu.gatech.grits.puppetctrl.comm.bioloid;

import edu.gatech.grits.puppetctrl.comm.serial.SerialPacket;

import java.nio.*;

public class ResetPacket extends SerialPacket {

	public ResetPacket(){
		command = BioloidCommand.RESET;
		outputBuffer = ByteBuffer.allocate(3);
		outputBuffer.put(SerialPacket.START_BYTE);
		outputBuffer.put(command.getCmd());
		outputBuffer.put((byte)0x00);
	}

	@Override
	public byte[] generateTxPacket() {
		return outputBuffer.array();
	}
}
