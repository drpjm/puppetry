#include "uart.h"
#include "dynamixel.h"

//uart flags
static Byte serialDataReady = 0;
static Byte newPacketStarted = 0;

//uart1 data structures
static Byte serialInBuffer[MAX_SERIAL_SIZE];
static Byte serialInBufferHead = 0;
static Byte serialInBufferTail = 0;

//uart0 data structures
volatile Byte rs485InBuffer[MAX_RS485_SIZE];
volatile Byte rs485InBufferHead = 0;
Byte rs485InBufferTail = 0;
Byte txBuffer[MAX_RS485_SIZE];
#define CLEAR_BUFFER rs485InBufferTail = rs485InBufferHead

void serialTx(Byte bTxdData);
void rs485tx(Byte bTxdData);

/*
 * initializes the uart ports on the Atmega
 */
void uartInit(void)
{
  DDRA = DDRB = DDRC = DDRD = DDRE = DDRF = 0;  //Set all port to input direction first.
  PORTB = PORTC = PORTD = PORTE = PORTF = PORTG = 0x00; //PortData initialize to 0
  clearBit(SFIOR,2); //All Port Pull Up ready
  DDRE |= (BIT_RS485_DIRECTION0|BIT_RS485_DIRECTION1); //set output the bit RS485direction

  DDRD |= (BIT_ZIGBEE_RESET|BIT_ENABLE_RXD_LINK_PC|BIT_ENABLE_RXD_LINK_ZIGBEE);
  
  PORTD &= ~_BV(BIT_LINK_PLUGIN); // no pull up
  PORTD |= _BV(BIT_ZIGBEE_RESET);
  PORTD |= _BV(BIT_ENABLE_RXD_LINK_PC);
  PORTD |= _BV(BIT_ENABLE_RXD_LINK_ZIGBEE);
}

/*
 * sets up the serial ports for communication
 */
void uartCommInit(Byte bPort, Byte bBaudrate, Byte bInterrupt)
{
  if(bPort == SERIAL_PORT0)
  {
    RS485_RXD;
    UBRR0H = 0; UBRR0L = bBaudrate; 
    UCSR0A = 0x02;  UCSR0B = 0x18;
    if(bInterrupt&RX_INTERRUPT) setBit(UCSR0B,7); // RxD interrupt enable
    UCSR0C = 0x06; UDR0 = 0xFF;
    setBit(UCSR0A,6);//SET_TXD0_FINISH; // Note. set 1, then 0 is read
  }
  else if(bPort == SERIAL_PORT1)
  {
    UBRR1H = 0; UBRR1L = bBaudrate; 
    UCSR1A = 0x02;  UCSR1B = 0x18;
    if(bInterrupt&RX_INTERRUPT) setBit(UCSR1B,7); // RxD interrupt enable
    UCSR1C = 0x06; UDR1 = 0xFF;
    setBit(UCSR1A,6);//SET_TXD1_FINISH; // Note. set 1, then 0 is read
  }
}


/************************************************************
 * ISRs														*
 ************************************************************/

/*
 * UART0 Rx interrupt service routine
 */
ISR (SIG_UART0_RECV)
{
	rs485InBuffer[(rs485InBufferHead++)] = RXD0_DATA;
}

/*
 * UART1 Rx interrupt service routine
 * A serial packet from the master always has the form:
 * [START_BYTE] [COMMAND_BYTE] [LENGTH] [DATA(0)] ... [DATA(L)]
 * Hence, to see if a packet is complete subtract the total count by the length. If it
 * is equal to header length we are done.
 */
ISR (SIG_UART1_RECV){

	Byte newByte = RXD1_DATA;
	static Byte count;
	static Byte dataLength;
	
	//test for a new packet starting
	if(newByte == PACKET_START_BYTE && newPacketStarted == 0){
		serialDataReady = 0;
		count = 0;
		dataLength = 0;
		newPacketStarted = 1;
	}
	//new packet reading state
	else{
		if(serialDataReady == 0){
			//store in buffer
			serialInBuffer[serialInBufferHead] = newByte;
			serialInBufferHead++;
			serialInBufferHead &= 0x3F;
			count++;
			if(count == SERIAL_HDR_LENGTH){
				dataLength = newByte;
			}
		}
	}
	//finished - reset variables
	if(count-dataLength == SERIAL_HDR_LENGTH){
		newPacketStarted = 0;
		serialDataReady = 1;
	}
	
}

/************************************************************
 * UART 1 Functions											*
 ************************************************************/
 //check if the serial port received a new packet
Byte serialPacketRdy(void)
{
	return serialDataReady;
} 

//read data from serial port into buffer array
Byte readSerialPort(Byte *readBuffer)
{
	Word i = 0;
	cli();
	//while the tail is no longer == head and i < maximum length
	while(serialInBufferHead != serialInBufferTail && i < MAX_SERIAL_SIZE){
		readBuffer[i] = serialInBuffer[serialInBufferTail];
		serialInBufferTail++;
		serialInBufferTail &= 0x3F;
		i++;
	}
	
	sei();
	//set serial data rdy flag
	serialDataReady = 0;
	return i;
}

//prints string to serial port 
void serialStringTx(Byte *bData)
{
  while(*bData)
  {
    serialTx(*bData++);
  }
}

/*
TxD8Hex() print data seperatly.
ex> 0x1a -> '1' 'a'.
*/
void serialHexTx(Byte bSentData)
{
  Byte bTmp;

  bTmp =((Byte)(bSentData>>4)&0x0f) + (Byte)'0';
  if(bTmp > '9') bTmp += 7;
  serialTx(bTmp);
  bTmp =(Byte)(bSentData & 0x0f) + (Byte)'0';
  if(bTmp > '9') bTmp += 7;
  serialTx(bTmp);
}


//transmit a byte out uart 1
void serialTx(Byte bTxdData)
{
  while(!TXD1_READY);
  TXD1_DATA = bTxdData;
}

/************************************************************
 * UART 0 Functions											*
 ************************************************************/
//Byte rs485DataRdy(void){
//	return rs485DataReady;
//}

/*
 * transmits a packet over rs485
 */
Byte rs485PacketTx(Byte id, Byte instr, Byte paramLength, Byte* params){
	int count;
	//header information
	txBuffer[0] = PACKET_START_BYTE;
	txBuffer[1] = PACKET_START_BYTE;
	txBuffer[2] = id;
	txBuffer[3] = paramLength + 2;
	txBuffer[4] = instr;
	
	if(paramLength > 0){
		//load parameters
		for(count = 0; count < paramLength; count++){
			txBuffer[count+5] = params[count];
		}
	}
	
	//calculate check sum
	Byte checkSum = 0;
	Byte packetLength = paramLength + 4 + 2;
	for(count = 2; count < packetLength-1; count++){
		checkSum += txBuffer[count];
	}
	//invert and return
	txBuffer[count] = ~checkSum;

	//enable transmit
	RS485_TXD;
	for(count = 0; count < packetLength; count++){
		setBit(UCSR0A, 6); //set TXD0 finish
		rs485tx(txBuffer[count]);
	}
	
	//wait for transmit to complete
	while(!CHECK_TXD0_FINISH);
	RS485_RXD;
	
	return packetLength;
}

/*
 * read the incoming rs485 packet -- returns 0 if an error occurred
 * Should only be called if a packet is going to return something
 */
ErrorState rs485PacketRx(Byte len, Byte* inBuffer){
	
#define RX_TIMEOUT_COUNT2   3000L  
#define RX_TIMEOUT_COUNT1  (RX_TIMEOUT_COUNT2*10L)  
  unsigned long ulCounter;
  Byte count, checkSum;
  Byte timedOut;
  ErrorState err;
  //initialize error state
  err.checksum = 0;
  err.idMismatch = 0;
  err.msgLength = 0;
  err.timeout = 0;
  err.wrongHeader = 0;
  err.wrongLength = 0;
  
  timedOut = 0;
  for(count = 0; count < len; count++)
  {
    ulCounter = 0;
    while(rs485InBufferTail == rs485InBufferHead)
    {
      if(ulCounter++ > RX_TIMEOUT_COUNT2)
      {
        timedOut = 1;
        break;
      }
    }
    if(timedOut) break;
    inBuffer[count] = rs485InBuffer[rs485InBufferTail++];
  }
  err.msgLength = count;
  checkSum = 0;
  
  if(txBuffer[2] != BROADCASTING_ID)
  {
    if(timedOut && len != 255) 
    {
//      serialStringTx("\r\n [Error:RxD Timeout]");
      CLEAR_BUFFER;
	  err.timeout = 1;
    }
    
    if(err.msgLength > 3) //checking is available.
    {
      if(inBuffer[0] != 0xff || inBuffer[1] != 0xff ) 
      {
//        serialStringTx("\r\n [Error:Wrong Header]");
        CLEAR_BUFFER;
        err.wrongHeader = 1;
      }
      if(inBuffer[2] != txBuffer[2] )
      {
//        serialStringTx("\r\n [Error:TxID != RxID]");
        CLEAR_BUFFER;
        err.idMismatch = 1;
      }  
      if(inBuffer[3] != err.msgLength-4) 
      {
//        serialStringTx("\r\n [Error:Wrong Length]");
        CLEAR_BUFFER;
        err.wrongLength = 1;
      }  
      for(count = 2; count < err.msgLength; count++) checkSum += inBuffer[count];
      if(checkSum != 0xff) 
      {
//        serialStringTx("\r\n [Error:Wrong CheckSum]");
        CLEAR_BUFFER;
        err.checksum = 1;
      }
    }
  }
  return err;
	
}

//transmit a byte out uart 0
void rs485tx(Byte bTxdData)
{
  while(!TXD0_READY);
  TXD0_DATA = bTxdData;
}
