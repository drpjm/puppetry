package edu.gatech.grits.puppetctrl.comm.serial;

/**
 * Class used to handle byte/int/char conversion.
 * 
 * DataHandler.java
 * @author pmartin
 * May 16, 2007
 */
public class RawDataHandler {
	/**
	 * To prevent the byte value from being chopped off at 0x7F in java
	 * calculation we must convert to an unsigned int.
	 * @param b
	 * @return
	 */
	public final static int byteToUnsignedInt(byte b){
		return (int) (b & 0xFF);
	}

	/**
	 * Breaks an int into two an array: [highByte lowByte]
	 * @param i
	 * @return
	 */
	public final static byte[] partitionInt(int i){
		byte[] ret = new byte[2];
		ret[0] = (byte)(i >> 8);
		ret[1] = (byte)(i & 0xFF);
		return ret;
	}
}
