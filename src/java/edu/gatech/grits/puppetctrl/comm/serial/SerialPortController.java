package edu.gatech.grits.puppetctrl.comm.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;
import java.nio.*;

import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidData;
import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidParser;
import edu.gatech.grits.puppetctrl.gui.MessageType;
import edu.gatech.grits.puppetctrl.gui.ObserverPacket;
import edu.gatech.grits.puppetctrl.gui.PanelObservable;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class SerialPortController implements SerialCommunicable, 
												SerialPortEventListener,
												Runnable {

	private SerialPort serialPort = null;
	private CommPortIdentifier portId;
	private Baud baudRate;
	private InputStream serialInput;
	private OutputStream serialOutput;
	private SerialParsable parser;
	
	private Thread serialThread;
	private volatile boolean stopRequested;
	private PanelObservable observer;
	
	public SerialPortController(SerialPortParameters params, PanelObservable po, SerialParsable sp){
		portId = params.getCommId();
		baudRate = params.getBaud();
		observer = po;
		
		try {
			serialPort = (SerialPort) portId.open("SerialPortController", 2000);
			serialPort.setSerialPortParams(baudRate.getBaud(), 
											SerialPort.DATABITS_8, 
											SerialPort.STOPBITS_1, 
											SerialPort.PARITY_NONE);
			//set this object to listen for serial events
			serialPort.addEventListener(this);
			//activate data available notification
			serialPort.notifyOnDataAvailable(true);
			serialInput = serialPort.getInputStream();
			serialOutput = serialPort.getOutputStream();
			System.out.println("Serial port attached: " + serialPort.getName());
			
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
		
		parser = sp;
		
		serialThread = new Thread(this);
		serialThread.setName("Thread-" + portId.getName());
		stopRequested = false;
		serialThread.start();
	}
	
	public boolean sendData(String data) {
		try {
			serialOutput.write(data.getBytes());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean sendData(byte[] data) {
		try {
			serialOutput.write(data);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void serialEvent(SerialPortEvent spe) {
		switch(spe.getEventType()){
		
			case SerialPortEvent.DATA_AVAILABLE:
				try {
					
					int packetSize = serialInput.available();
					ByteBuffer bb = ByteBuffer.allocate(packetSize);
					if(packetSize > 0){
						packetSize = serialInput.read(bb.array());
					}
					//parse data
					BioloidData bd = (BioloidData)parser.parseData(bb.array());
					if(bd != null){
					  if(parser instanceof BioloidParser)	
						observer.notifyChange(new ObserverPacket(MessageType.NEW_DATA,
																	bd));
					  else
						  observer.notifyChange(new ObserverPacket(MessageType.NEW_BSDATA,
									bd));  
					}
					else{
						//not a regular data packet; just print it!
						System.out.println(new String(bb.array()));
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
		}
	}

	public void closePortRequest() {
		stopRequested = true;
		if(this.serialThread != null)
			serialThread.interrupt();
	}

	public void run() {
		while(!stopRequested){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				serialPort.close();
				Thread.currentThread().interrupt();
			}
		}
	}

	public boolean isReady() {
		if(this.serialThread.isAlive()){
			return true;
		}
		else
			return false;
	}

}
