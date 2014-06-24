package edu.gatech.grits.puppetctrl.util;

import edu.gatech.grits.puppetctrl.comm.bioloid.MotionPacket;

public class Control {

	private MotionPacket motion;
	
	public Control(MotionPacket mp){
		motion = mp;
	}

	public MotionPacket getMotion() {
		return motion;
	}

	@Override
	public String toString() {
		return motion.toString();
	}
	
	
}
