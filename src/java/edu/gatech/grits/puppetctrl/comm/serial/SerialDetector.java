package edu.gatech.grits.puppetctrl.comm.serial;

/**
 * 	@author pmartin
 *	This class detects all of the serial ports on a specific operating system and stores them in
 *	an ArrayList for access by higher level applications.
 *
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import gnu.io.*;

public class SerialDetector {

	private Vector<CommPortIdentifier> commPortList;
	
	public SerialDetector(){
		
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		commPortList = new Vector<CommPortIdentifier>();
		
		String os = System.getProperty("os.name");
		System.out.println("OS: " + os);
		
		while(portEnum.hasMoreElements()){
			CommPortIdentifier commPortId = (CommPortIdentifier) portEnum.nextElement();
			if(commPortId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				//Mac uses weird names for serial port - only using Keyspan
				if(os.contains("Mac")){
					if(commPortId.getName().contains("tty.KeySerial")){
						System.out.println(commPortId.getName() + " found!");
						commPortList.add(commPortId);
					}
				}
				else{
					System.out.println(commPortId.getName() + " found!");
					commPortList.add(commPortId);
				}
			}
		}
				
	}
	
	public Vector<CommPortIdentifier> getCommPortList() {
		return commPortList;
	}

	public static void main(String[] args){
		//Creates a serial port and tests the connection.
		SerialDetector sd = new SerialDetector();
				
	}
}
