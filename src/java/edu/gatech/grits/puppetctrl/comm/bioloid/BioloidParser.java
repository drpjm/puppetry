package edu.gatech.grits.puppetctrl.comm.bioloid;

import java.nio.ByteBuffer;

import edu.gatech.grits.puppetctrl.comm.serial.RawDataHandler;
import edu.gatech.grits.puppetctrl.comm.serial.SerialPacket;
import edu.gatech.grits.puppetctrl.comm.serial.SerialParsable;

import javolution.util.FastList;

/**
 * Class which will be used to pull out important data from a serial stream.
 * 
 * BioloidParser.java
 * @author pmartin
 * May 4, 2007
 */
public class BioloidParser implements SerialParsable {

	private ParserState state;
	private ByteBuffer buffer;
	private boolean headerReceived;
	private int currMsgLen;

	public BioloidParser(){
		buffer = ByteBuffer.allocate(SerialPacket.MAXSIZE);
		state = ParserState.INIT;
		headerReceived = false;
		currMsgLen = 0;
	}

	/**
	 * Method parses a byte array into a new BioloidData object.
	 * @param newData
	 * @return
	 */
	public final BioloidData parseData(byte[] newData){

		BioloidData bd = null;
		int dataLength = newData.length;
		int i = 0;
//        System.out.println("datalenth is");  //somehow wait for larger packet: see SErial Port COntroller
//        System.out.println(dataLength);
        
		//read in data and organize into a single buffer for processing
		while(i < dataLength){
			byte curr = newData[i];
			switch(state){

			case INIT:
				if(curr == SerialPacket.START_BYTE){
					state = ParserState.PARTIAL;
					this.buffer.put(newData[i]);
				}
				break;

			case PARTIAL:
				// debug print out
				//System.out.println("partial state in parser");
				//check this is a command byte
				if(BioloidCommand.isValidCommand(curr) && buffer.position() == 1 && !headerReceived){
					this.buffer.put(curr);
					//special case for RESET command!
					if(curr == BioloidCommand.RESET.getCmd()){
						//complete!
						this.state = ParserState.COMPLETE;
					}
				}
				//check that this is a length byte
				else if(buffer.position() == 2 && !headerReceived){
					this.buffer.put(curr);
					this.currMsgLen = SerialPacket.HEADER_SIZE;
					headerReceived = true;
				}
				//header received
				else{
					if(currMsgLen < (SerialPacket.HEADER_SIZE + buffer.get(2))){
						buffer.put(curr);
						currMsgLen++;
						//System.out.println("Cur Len:"+currMsgLen);						
						//System.out.println(currMsgLen);
					}
					else{
						//finished
						state = ParserState.COMPLETE;
					}
				}
				
				break;
			}
			i++;
		}

		if(state == ParserState.COMPLETE){
			//determine command to build from:
			byte testByte = buffer.get(1);	//command is 2nd byte of the array
			bd = new BioloidData();

			//System.out.println("TestByte is:");
			//System.out.println(testByte);
			
			
			if(testByte == BioloidCommand.STATUS.getCmd()){
				
				bd.setCommand(BioloidCommand.STATUS);
				int length = buffer.position();
				FastList<Integer> motors = new FastList<Integer>();
				for(int j = SerialPacket.HEADER_SIZE; j < length; j++){
					motors.add(new Integer(buffer.get(j)));
				}
				bd.setMotorIds(motors);
				
			}
			else if(testByte == BioloidCommand.MOTION.getCmd()){
				
			}
			else if(testByte == BioloidCommand.RESET.getCmd()){
				bd.setCommand(BioloidCommand.RESET);
				bd.setMotorIds(new FastList<Integer>());
			}
			else if(testByte == BioloidCommand.ERROR.getCmd()){
				
			}
			else if(testByte == BioloidCommand.DIRECT.getCmd()){
				
			}
			else if(testByte == BioloidCommand.DATA.getCmd()){
				//System.out.println("im parsing");
				bd.setCommand(BioloidCommand.DATA);
				int length = buffer.position();
				//System.out.println(length);
                int motornum = buffer.get(2);
				//System.out.println("Length:"+motornum);

				FastList<Integer> motors = new FastList<Integer>();
				FastList<Float> pos = new FastList<Float>();
				FastList<Float> vel = new FastList<Float>();
				
             /* for(int j = SerialPacket.HEADER_SIZE; j < length; j+=4){  //wo IDs <
					
					int mot = buffer.get(j);
            	    if (j==7) {mot=13; }
					motors.add(mot);
					//convert form HEx to float!!???
					float posadd;
					float veladd;
					byte lb = buffer.get(j);
					int posL = RawDataHandler.byteToUnsignedInt(lb);
					byte hb = buffer.get(j+1);
					int posH = RawDataHandler.byteToUnsignedInt(hb);
					lb = buffer.get(j+2);
					int velL = RawDataHandler.byteToUnsignedInt(lb);
					hb = buffer.get(j+3);
					int velH = RawDataHandler.byteToUnsignedInt(hb); */
					
				for(int j = SerialPacket.HEADER_SIZE; j < length; j+=5){  //oringally <
					
					int mot = buffer.get(j);
					motors.add(new Integer(buffer.get(j)));
					//convert form HEx to float!!???
					float posadd;
					float veladd;
					byte lb = buffer.get(j+1);
					int posL = RawDataHandler.byteToUnsignedInt(lb);
					byte hb = buffer.get(j+2);
					int posH = RawDataHandler.byteToUnsignedInt(hb);
					lb = buffer.get(j+3);
					int velL = RawDataHandler.byteToUnsignedInt(lb);
					hb = buffer.get(j+4);
					int velH = RawDataHandler.byteToUnsignedInt(hb); //*/
					
					
					posadd =DynamixelParams.apprxHex2Deg(posL+ 256*posH);
					//convert to meters
					  //// !!!!! INVERT THE POS and VEL sign for motor 14!!! ist backwards
					if (mot==14){
						posadd = (float) (((300-posadd)-150)*(Math.PI/180)*0.029972/2); //diam=1.19in
					}else{
					   posadd = (float) ((posadd-150)*(Math.PI/180)*0.029972/2); //meters
					}
					
					pos.add(posadd);
					
					//veladd =DynamixelParams.apprxHex2DegPS(velL+ 256*velH);
					veladd =(float) ((1*velL+ 256*velH)); 
					
					if (veladd>1023){
					 veladd =(float) -((1*velL+ 256*velH-1023)*0.111437); //RPM  //DOESNT WORK FOR negative!!!
					  }
					else{
						 veladd =(float) ((1*velL+ 256*velH)*0.111437);
					     }
					
					//veladd = veladd * 1 // turnform RPM to deg/s 
					veladd =DynamixelParams.apprxRPM2MetPS(veladd);
					
				    if (mot==14){veladd=-veladd;}
					vel.add(veladd);
					
					//pos.add(new Float(buffer.get(j+1)));
					//pos.add(new Float(buffer.get(j+2)));
					//vel.add(new Float(buffer.get(j+3)));
					//vel.add(new Float(buffer.get(j+4)));	                
				}
				bd.setMotorIds(motors);
				bd.setVels(vel);
				bd.setPoss(pos);
			}
			else if(testByte == BioloidCommand.ENCDATA.getCmd()) {
				
			}
			else{
				//NOT VALID!
				bd = null;
			}

			//reset parser
			state = ParserState.INIT;
			buffer.clear();
			currMsgLen = 0;
			headerReceived = false;
		}

		return bd;

	}

	/**
	 * Test program.
	 * @param args
	 */
	public static void main(String[] args){
		byte[] array1 = {(byte)1, (byte)23, (byte)0xFF, BioloidCommand.STATUS.getCmd()};
		byte[] array2 = {(byte)4, (byte)1, (byte)2, (byte)3};
		byte[] array3 = {(byte)4, (byte)66, (byte)2};

		BioloidParser bp = new BioloidParser();
		BioloidData bd;
		bd = bp.parseData(array1);
		bd = bp.parseData(array2);
		bd = bp.parseData(array3);
		
		if(bd != null){
			System.out.println(bd.toString());
		}

	}

	private enum ParserState {
		INIT(0),
		PARTIAL(1),
		COMPLETE(2);

		private int state;
		private ParserState(int i){
			state = i;
		}
		public int getState() {
			return state;
		}

	}
}
