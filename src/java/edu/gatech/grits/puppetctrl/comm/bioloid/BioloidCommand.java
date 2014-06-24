package edu.gatech.grits.puppetctrl.comm.bioloid;

public enum BioloidCommand {

	MOTION((byte)0x4D),
	DIRECT((byte)0x44),
	STATUS((byte)0x53),
	RESET((byte)0x52),
	ERROR((byte)0x45),
	DATA((byte)0x4F),
	INVALID((byte)0x00),
	CONTMO ((byte)0x4E),
	ENCDATA((byte)0x4A);
	
	private byte cmd;
	
	private BioloidCommand(byte b){
		cmd = b;
	}

	public byte getCmd() {
		return cmd;
	}
	
	public static final boolean isValidCommand(byte in){
		switch(in){
		case (byte)0x44:
			return true;
		case (byte)0x45:
			return true;
		case (byte)0x4D:
			return true;
		case (byte)0x53:
			return true;
		case (byte)0x52:
			return true;
		case (byte)0x4F:
			return true;
		case (byte) 0x4E:
			return true;
		default:
			return false;
		}
	}
	
}
