#include "global.h"
#include "uart.h"
#include "bioloid.h"
#include "dynamixel.h"

//function declarations
Byte gatherMotorInfo(Byte* dataBuffer, Byte* cacheBuffer);
Byte controlMotor(Byte* inputBuf);
Byte generateMotion(Byte* inputBuf, Byte* returnBuf);
Byte resetSystem(Byte* dataBuffer);
void handleError(ErrorState value);
void resetState(void);

State currState;
Byte readBuf[MAX_SERIAL_SIZE];
Byte numOfMotors;
Byte motorListBuf[MAX_NUM_MOTORS];

int main(void)
{

	uartInit();

	uartCommInit(SERIAL_PORT0, 1, RX_INTERRUPT);
	uartCommInit(SERIAL_PORT1, DEFAULT_BAUD_RATE, RX_INTERRUPT);

	sei();	//enable interrupts

	while(1){
			//read packet
			if(serialPacketRdy()){
				Byte packetSize = readSerialPort(readBuf);

				//check command from host
				Byte returnBuf[MAX_SERIAL_SIZE];
				switch(readBuf[0]){
					case STATUS:
						numOfMotors = gatherMotorInfo(returnBuf, motorListBuf);
						//transmit data to MASTER
						serialStringTx(returnBuf);
						break;
					case MOTION:
						generateMotion(readBuf, returnBuf);
						break;
					case DIRECT:
						controlMotor(readBuf);
						break;
					case RESET:
						resetSystem(returnBuf);
						//send back an ACK to MASTER
						serialStringTx(returnBuf);
						break;
					default:
						break;
				}

			}
	};
}

/*
 * pings for a list of dynamixel IDs and reports them to the master
 */
Byte gatherMotorInfo(Byte* dataBuffer, Byte* cacheBuffer){
	dataBuffer[0] = PACKET_START_BYTE;
	dataBuffer[1] = STATUS;
	Byte motorCount = 0;
	Byte i;

	//ping all possible motor ids
	for(i = 1; i <= MAX_NUM_MOTORS; i++){

		Byte txMsgSize = ping(i);
		Byte commandBuf[txMsgSize];
		ErrorState rxState = rs485PacketRx(txMsgSize, commandBuf);
		//if ping received record motor id
		if(rxState.msgLength == STATUS_RETURN_PACKET_SIZE){
			dataBuffer[motorCount+3] = commandBuf[2];
			cacheBuffer[motorCount] = commandBuf[2];
			motorCount++;
		}

		//TODO: error handling
		//handleError(rxMsgSize);

	}
	dataBuffer[2] = motorCount;

	//return available and their status
	return motorCount;
}

/*
 * handles the direct control of one motor
 */
Byte controlMotor(Byte* inputBuf){
	//inputBuf[2] = motor id
	moveToGoalAtSpeed(inputBuf[2], inputBuf[6], inputBuf[5], inputBuf[4], inputBuf[3]);
}

/*
 * takes master's commanded motion, error checks and transmits to dynamixels
 */
Byte generateMotion(Byte* inputBuf, Byte* returnBuf){

	//create buffers for sync write command
	Byte inParamLength = inputBuf[1];

	//allocate Byte array: # input params + 4
	Byte params[inParamLength + 4];
	params[0] = P_GOAL_POSITION_L;
	params[1] = 4;	//length to write to address
	//TODO: pull parameters from the input buffer
	int i;
	for(i = 0; i < inParamLength; i++){
		params[i+2] = inputBuf[i+2];
	}

	applyMotion(inParamLength + 4, params);
}

/*
 * resets controller to original state - uses fixed values
 */
Byte resetSystem(Byte* returnBuf){
	returnBuf[0] = PACKET_START_BYTE;
	returnBuf[1] = RESET;
	returnBuf[2] = 0x00;

	//send zeroing commands to motors
	Byte L = 4;	//amount of data per motor
	//fix speed for safety - will be more flexible in future
	Byte speedLow = 0x60;
	Byte speedHigh = 0x00;
	Byte posLow = 0xff;
	Byte posHigh = 0x01;

	Byte pos_l_liftlow = 0xCC;
	Byte pos_l_lifthigh = 0x00;
	Byte pos_r_liftlow = 0x32;
	Byte pos_r_lifthigh = 0x03;

	Byte length = ((L + 1)*numOfMotors) + 4;	//total length of packet
	Byte params[] = {P_GOAL_POSITION_L, 4,
					LEFTARMLIFT, pos_l_liftlow, pos_l_lifthigh, speedLow, speedHigh,
					LEFTARMROT, posLow, posHigh, speedLow, speedHigh,
					LEFTLEGLIFT, pos_l_liftlow, pos_l_lifthigh, speedLow, speedHigh,
					RIGHTARMLIFT, pos_r_liftlow, pos_r_lifthigh, speedLow, speedHigh,
					RIGHTARMROT, posLow, posHigh, speedLow, speedHigh,
					RIGHTLEGLIFT, pos_r_liftlow, pos_r_lifthigh, speedLow, speedHigh};

	applyMotion(length, params);

	return 1;
}
/*
 * function that produces an error packet for sending back to master application
 */
void handleError(ErrorState es){

}

void resetState(void){
	currState.ready = 0;
	currState.serialRdDone = 0;
	currState.rs485RdDone = 0;
}
