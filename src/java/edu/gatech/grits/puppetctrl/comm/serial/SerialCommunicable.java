package edu.gatech.grits.puppetctrl.comm.serial;

public interface SerialCommunicable {

	public boolean sendData(String data);
	public boolean sendData(byte[] data);
	public void closePortRequest();
	public boolean isReady();
	
}
