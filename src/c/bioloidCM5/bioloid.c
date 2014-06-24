/*
 * contains functions that wrap communication to dynamixels
 */
#include "bioloid.h"
#include "dynamixel.h"		//for dynamixel instructions
#include "uart.h"

Byte regWrite(Byte id, Byte paramLen, Byte* dataParams);
Byte action(void);
Byte syncWrite(Byte paramLen, Byte* dataParams);

//pings the dynamixel with id; returns size of  
Byte ping(Byte id){
 	Byte params[1];
 	return rs485PacketTx(id, INST_PING, 0, params);
}
 
Byte read(Byte id, Byte reg){
}

//function used to move a *single* motor
Byte moveToGoalAtSpeed(Byte id, Byte posLow, Byte posHigh, Byte speedLow, Byte speedHigh){
	Byte params[5];
	params[0] = P_GOAL_POSITION_L;
	params[1] = posLow;
	params[2] = posHigh;
	params[3] = speedLow;
	params[4] = speedHigh;
	write(id, 5, params);
	return 1;
}

//function that writes to the target reg in the dynamixel. action occurs AT write
Byte write(Byte id, Byte paramLen, Byte* dataParams){
	return rs485PacketTx(id, INST_WRITE, paramLen, dataParams);
}

//same as above, however action will not occur until 'action' command is sent
Byte regWrite(Byte id, Byte paramLen, Byte* dataParams){
	return rs485PacketTx(id, INST_REG_WRITE, paramLen, dataParams);
}
 
Byte action(void){
}

Byte applyMotion(Byte dataLength, Byte* data){
	syncWrite(dataLength, data);
	return 1;
}

//broadcasts a sync write command to network 
Byte syncWrite(Byte dataLength, Byte* dataParams){
	return rs485PacketTx(BROADCASTING_ID, INST_SYNC_WRITE, dataLength, dataParams);
}
