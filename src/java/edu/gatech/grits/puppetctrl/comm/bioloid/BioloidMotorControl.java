package edu.gatech.grits.puppetctrl.comm.bioloid;

import java.nio.*;
/**
 * Holds the data necessary to control a bioloid motor's position and velocity.
 * 
 * @author pmartin
 *
 */
public class BioloidMotorControl {

	private Short velocity;
	private Short position;
	private Byte id;
	public final static int NUMBYTES = 5;
	
	public BioloidMotorControl(int id){
		this.id = (byte)id;
		velocity = 0;
		position = 0;
	}

	public BioloidMotorControl(int id, Short position, Short velocity){
		this.id = (byte)id;
		this.position = position;
		this.velocity = velocity;
	}
	
	public Byte getId() {
		return id;
	}

	public void setId(Byte id) {
		this.id = id;
	}

	public Short getPosition() {
		return position;
	}

	public void setPosition(Short position) {
		this.position = position;
	}

	public Short getVelocity() {
		return velocity;
	}

	public void setVelocity(Short velocity) {
		this.velocity = velocity;
	}
	
	public byte[] generateTxArray(){
		ByteBuffer bb = ByteBuffer.allocate(NUMBYTES);
		bb.put(id);
		//Java short -- [HIGH Byte] [LOW Byte]
		bb.putShort(position);	
		bb.putShort(velocity);
		//Bioloid requires the bytes to be flipped...
		for(int i = 0; i < NUMBYTES-1; ){
			byte tmpLow = bb.get(i + 2);
			byte tmpHigh = bb.get(i + 1);
			bb.put(i+1, tmpLow);
			bb.put(i+2, tmpHigh);
			i+=2;
		}
		
		return bb.array();
	}
	
	@Override
	public String toString() {
		String tmp = "Motor ";
		tmp += this.id;
		tmp += "-> Pos[";
		tmp += position + "] ";
		tmp += " Vel: [";
		tmp += velocity + "] ";
		return tmp;
	}

	public static void main(String[] args){
		BioloidMotorControl bmc = new BioloidMotorControl((byte)1);
		bmc.setPosition((short)0x302);
		bmc.setVelocity((short)0x3fa);
		byte[] test = bmc.generateTxArray();
		for(byte b : test){
			System.out.print(b + "|");
		}
		System.out.println(bmc);
		
	}
}
