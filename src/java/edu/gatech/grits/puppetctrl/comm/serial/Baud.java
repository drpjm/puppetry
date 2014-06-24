package edu.gatech.grits.puppetctrl.comm.serial;

public enum Baud {

	B9600(9600),
	B19200(19200),
	B57600(57600),
	B38400(38400),
	B115200(115200);

	private int baud;
	
	private Baud(int i){
		baud = i;
	}

	public int getBaud() {
		return baud;
	}
	
}