package edu.gatech.grits.puppetctrl.comm.serial;

import gnu.io.*;

public class SerialPortParameters {
	
	private String portName;
	private CommPortIdentifier commId;
	private Baud baud;
	
	public SerialPortParameters(CommPortIdentifier id, Baud b){
		this.baud = b;
		this.commId = id;
		this.portName = id.getName();
	}
	
	public Baud getBaud() {
		return baud;
	}
	public String getPortName() {
		return portName;
	}

	public CommPortIdentifier getCommId() {
		return commId;
	}
	

}
