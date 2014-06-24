#ifndef GLOBAL_H_
#define GLOBAL_H_

#include <math.h>
#include <avr/interrupt.h>


#define ENABLE_BIT_DEFINITIONS

typedef unsigned char Byte;
typedef unsigned int Word;

typedef struct state {
	Byte ready;
	Byte serialRdDone;
	Byte rs485RdDone;
} State;

//a value of 1 in anything besides length is an error
typedef struct errstate {
	Byte msgLength;
	Byte wrongLength;
	Byte timeout;
	Byte checksum;
	Byte idMismatch;
	Byte wrongHeader;
} ErrorState;

//MOTOR MAPPING
#define LEFTARMLIFT 0x04
#define LEFTARMROT 0x03
#define LEFTLEGLIFT 0x06
#define RIGHTARMLIFT 0x02
#define RIGHTARMROT 0x01
#define RIGHTLEGLIFT 0x05

//byte defines for commands from master
#define MOTION	0x4D
#define RESET	0x52
#define CONFIG	0x43
#define STATUS	0x53
#define ERROR	0x45
#define DIRECT	0x44

#define MAX_NUM_MOTORS	16
#define NUM_MOTORS		6

//misc defines
#define RX_INTERRUPT 0x01
#define TX_INTERRUPT 0x02
#define OVERFLOW_INTERRUPT 0x01
#define SERIAL_PORT0 0
#define SERIAL_PORT1 1
#define BIT_RS485_DIRECTION0  0x08  //Port E
#define BIT_RS485_DIRECTION1  0x04  //Port E

#define BIT_ZIGBEE_RESET               PD4  //out : default 1 //PORTD
#define BIT_ENABLE_RXD_LINK_PC         PD5  //out : default 1
#define BIT_ENABLE_RXD_LINK_ZIGBEE     PD6  //out : default 0
#define BIT_LINK_PLUGIN                PD7  //in, no pull up

//UART defines
#define DEFAULT_BAUD_RATE 34   	//57600bps at 16MHz
#define MAX_SERIAL_SIZE		64
#define MAX_RS485_SIZE		255
#define PACKET_START_BYTE	0xFF

#define STATUS_RETURN_PACKET_SIZE 6

#endif /*GLOBAL_H_*/
