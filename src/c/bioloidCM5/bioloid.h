#ifndef BIOLOID_H_
#define BIOLOID_H_

#include "global.h"			//for typedef's, etc.

Byte ping(Byte id);
Byte read(Byte id, Byte reg);
Byte write(Byte id, Byte paramLen, Byte* dataParams);

//courtesy command functions
Byte moveToGoalAtSpeed(Byte id, Byte posLow, Byte posHigh, Byte speedLow, Byte speedHigh);
Byte moveAppendage(void);
Byte applyMotion(Byte paramLen, Byte* data);

#endif /*BIOLOID_H_*/
