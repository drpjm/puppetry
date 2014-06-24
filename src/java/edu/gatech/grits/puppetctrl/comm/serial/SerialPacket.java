package edu.gatech.grits.puppetctrl.comm.serial;

import java.nio.ByteBuffer;

import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidCommand;

/**
 * Abstract class to apply structure to serial based data packets.
 * @author pmartin
 *
 */
public abstract class SerialPacket {
	
	public static final int MAXSIZE = 64;
	public static final int HEADER_SIZE = 3;
	public static final byte START_BYTE = (byte)0xFF;
	public static final byte ESTART_BYTE = (byte)0x4B;

	
	protected ByteBuffer outputBuffer;
	protected BioloidCommand command;


	public ByteBuffer getOutputBuffer() {
		return outputBuffer;
	}

	public BioloidCommand getCommand() {
		return command;
	}

	public abstract byte[] generateTxPacket();
}
