package edu.gatech.grits.puppetctrl.comm.bioloid;

import java.nio.ByteBuffer;

import edu.gatech.grits.puppetctrl.comm.serial.SerialPacket;

public class DataPacket extends SerialPacket {
	public DataPacket(){
		command = BioloidCommand.DATA;
		this.outputBuffer = ByteBuffer.allocate(3);
		outputBuffer.put(SerialPacket.START_BYTE);
		outputBuffer.put(command.getCmd());
		outputBuffer.put((byte)0x00);
	}

	@Override
	public byte[] generateTxPacket() {
		// TODO Auto-generated method stub
		return outputBuffer.array();
	}

}
