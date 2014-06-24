package edu.gatech.grits.puppetctrl.gui;

public class ObserverPacket {

	private MessageType msgType;
	private Object data; 
	
	public ObserverPacket(MessageType dataType, Object data) {
		super();
		this.msgType = dataType;
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public MessageType getMsgType() {
		return msgType;
	}

	public void setDataType(MessageType dataType) {
		this.msgType = dataType;
	}
	
}
