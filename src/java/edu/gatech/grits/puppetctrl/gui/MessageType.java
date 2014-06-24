/**
 * 
 */
package edu.gatech.grits.puppetctrl.gui;

public enum MessageType {
	
	PORT_OPEN(0),
	PORT_CLOSE(1),
	SEND_DATA(2),
	NEW_DATA(3),
	PRINT(4),
	RUNNING(5),
	STOPPED(6),
	NEW_PLAY(7),
	SOLUTION(8),
	NEW_BSDATA(9);
	
	private int type;
	private MessageType(int t){
		type = t;
	}
	public int getType() {
		return type;
	}
	
}