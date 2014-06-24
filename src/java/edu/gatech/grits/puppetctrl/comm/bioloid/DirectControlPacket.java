package edu.gatech.grits.puppetctrl.comm.bioloid;

import java.nio.*;
import edu.gatech.grits.puppetctrl.comm.serial.SerialPacket;

public class DirectControlPacket extends SerialPacket {

	private Short speed;
	private Short range;
	private Byte id;
	
	public DirectControlPacket(){
		command = BioloidCommand.DIRECT;
		this.outputBuffer = ByteBuffer.allocate(8);
	}
	
	public Byte getId() {
		return id;
	}

	public void setId(Byte id) {
		this.id = id;
	}

	public Short getRange() {
		return range;
	}

	public void setRange(Short range) {
		this.range = range;
	}

	public Short getSpeed() {
		return speed;
	}

	public void setSpeed(Short speed) {
		this.speed = speed;
	}

	@Override
	public byte[] generateTxPacket() {
		outputBuffer.put(SerialPacket.START_BYTE);
		outputBuffer.put(command.getCmd());
		outputBuffer.put((byte)5);		//only sends 4 bytes
		outputBuffer.put(id.byteValue());
		outputBuffer.putShort(speed.shortValue());
		outputBuffer.putShort(range.shortValue());
		return outputBuffer.array();
	}

}
