#ifndef UART_H_
#define UART_H_

#include "global.h"

//bit setting macros
#define clearBit(REG8,BITNUM) REG8 &= ~(_BV(BITNUM))
#define setBit(REG8,BITNUM) REG8 |= _BV(BITNUM)

/*
	UART register macros - configuration
*/
#define SET_TxD0_FINISH   setBit(UCSR0A,6)
#define RESET_TXD0_FINISH clearBit(UCSR0A,6)
#define CHECK_TXD0_FINISH bit_is_set(UCSR0A,6)
#define SET_TxD1_FINISH  setBit(UCSR1A,6)
#define RESET_TXD1_FINISH clearBit(UCSR1A,6)
#define CHECK_TXD1_FINISH bit_is_set(UCSR1A,6)

/*
	UART register macros - data and interrupt
*/
#define TXD1_READY			bit_is_set(UCSR1A,5) //(UCSR1A_Bit5)
#define TXD1_DATA			(UDR1)	//data register for uart1
#define RXD1_READY			bit_is_set(UCSR1A,7)
#define RXD1_DATA			(UDR1)

#define TXD0_READY			bit_is_set(UCSR0A,5)
#define TXD0_DATA			(UDR0)	//data register for uart0
#define RXD0_READY			bit_is_set(UCSR0A,7)
#define RXD0_DATA			(UDR0)

//enable/disable macros for UART0
#define RS485_TXD PORTE &= ~_BV(PE3),PORTE |= _BV(PE2)  //_485_DIRECTION = 1
#define RS485_RXD PORTE &= ~_BV(PE2),PORTE |= _BV(PE3)  //PORT_485_DIRECTION = 0

#define SERIAL_HDR_LENGTH	2

//externally accessable uart functions
void uartInit(void);
void uartCommInit(Byte bPort, Byte bBaudrate, Byte bInterrupt);
//serial port functions
Byte serialPacketRdy(void);
Byte readSerialPort(Byte *readBuffer);
void serialStringTx(Byte *bData);
void serialHexTx(Byte bSentData);
//rs485 functions
Byte rs485PacketTx(Byte id, Byte instr, Byte paramLength, Byte* params);
ErrorState rs485PacketRx(Byte len, Byte* inBuffer);

#endif /*UART_H_*/
