package edu.gatech.grits.puppetctrl.mdl.action;

import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidMotorControl;
import edu.gatech.grits.puppetctrl.comm.bioloid.DynamixelParams;
import edu.gatech.grits.puppetctrl.comm.bioloid.MotionPacket;
import edu.gatech.grits.puppetctrl.mdl.util.ActionAdapter;
import edu.gatech.grits.puppetctrl.model.PuppetMotorMap;
import edu.gatech.grits.puppetctrl.util.Control;

public class Run extends ActionAdapter {

    private String name;
    private boolean isUpStroke; // upstroke means left arm is moving up
    private boolean isIntialStroke; // start with left arm first

    // fastest run is 60 deg/sec
    private final float MAXSPEED = 70;
    private final float MAX_SPEED = 70.0f;

    private final float MAX_LEFT_HEIGHT_LEG = 150.0f;
    private final float MIN_LEFT_HEIGHT_LEG = 60.0f;
    private final float MAX_LEFT_HEIGHT_ARM = 210.0f;
    private final float MIN_LEFT_HEIGHT_ARM = 120.0f;

    private final float MAX_RIGHT_HEIGHT_LEG = 150.0f;
    private final float MIN_RIGHT_HEIGHT_LEG = 240.0f;
    private final float MAX_RIGHT_HEIGHT_ARM = 110.0f;
    private final float MIN_RIGHT_HEIGHT_ARM = 180.0f;

    private float currLeftHeight;
    private float currRightHeight;
    private int fl = 0; // A flag is used to start off the puppet into the running motion
    
    public Run() {
    	System.out.println("Run created.");
    	name = "run";
    }

    @Override
    public String toString() {
    	return name;
    }

    public Control actOn(float scaleParam, long currTime) {

	float liftSpeed = scaleParam * this.MAX_SPEED;
	float liftArmSpeed = liftSpeed * ((MAX_LEFT_HEIGHT_ARM - MIN_LEFT_HEIGHT_ARM)/(MAX_LEFT_HEIGHT_ARM - MIN_LEFT_HEIGHT_ARM));
//	System.out.println(name.toUpperCase() + "(" + scaleParam + ")");
	MotionPacket nextMotion = new MotionPacket();

	float leftArmGoal, leftLegGoal;
	float rightArmGoal, rightLegGoal;

	long leftPeriod = (long) ((Math.abs(this.MAX_LEFT_HEIGHT_LEG
		- this.MIN_LEFT_HEIGHT_LEG) / (scaleParam * this.MAX_SPEED)) * 1000 * 2);
	long rightPeriod = (long) ((Math.abs(this.MAX_RIGHT_HEIGHT_LEG
		- this.MIN_RIGHT_HEIGHT_LEG) / (scaleParam * this.MAX_SPEED)) * 1000 * 2);

	BioloidMotorControl leftLegControl = new BioloidMotorControl(
		PuppetMotorMap.LEFTLEGLIFT);
	BioloidMotorControl leftArmControl = new BioloidMotorControl(
		PuppetMotorMap.LEFTARMLIFT);
	BioloidMotorControl rightArmControl = new BioloidMotorControl(
		PuppetMotorMap.RIGHTARMLIFT);
	BioloidMotorControl rightLegControl = new BioloidMotorControl(
		PuppetMotorMap.RIGHTLEGLIFT);

	fl = fl + 1;
	
	if (fl <= 0){
		if (currTime < leftPeriod) {
			
		leftLegGoal = this.MAX_LEFT_HEIGHT_LEG;
	    leftLegControl.setPosition(DynamixelParams
		    .approxDegree(leftLegGoal));
	    leftLegControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    rightArmGoal = this.MAX_RIGHT_HEIGHT_ARM;
	    rightArmControl.setPosition(DynamixelParams
		    .approxDegree(rightArmGoal));
	    rightArmControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftArmSpeed));
		}		
	}
		
	if (fl > 0){
	// walk is driven by an offset sinusoid
	// starts initial phase
	if (currTime < leftPeriod / 2) {
	    leftLegGoal = this.MAX_LEFT_HEIGHT_LEG;
	    leftLegControl.setPosition(DynamixelParams
		    .approxDegree(leftLegGoal));
	    leftLegControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    rightArmGoal = this.MAX_RIGHT_HEIGHT_ARM;
	    rightArmControl.setPosition(DynamixelParams
		    .approxDegree(rightArmGoal));
	    rightArmControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    nextMotion.addBioloidControl(leftLegControl);
	    nextMotion.addBioloidControl(rightArmControl);
	    System.out.println(nextMotion);
	}
	// continue the motions on alternating limbs
	else {
	    // time for initial limbs
	    double tau_lift = ((double) leftPeriod) / 1000.0;
	    double t1 = ((double) currTime) / 1000.0;
	    
	    //used in an earlier version of the code for the phase shift.   
	    //double t2 = ((double) currTime) / 1000.0 - (tau_lift / 2.0);

	    // calculate sin of t1
	    double val1 = Math.sin(2 * Math.PI * (1 / tau_lift) * t1);
	    double val2 = Math.sin((2 * Math.PI * (1 / tau_lift) * t1) + (5 * Math.PI / 6));
	    if (val1 < 0) {
		leftLegGoal = this.MIN_LEFT_HEIGHT_LEG;
		rightArmGoal = this.MIN_RIGHT_HEIGHT_ARM;
	    } else {
		leftLegGoal = this.MAX_LEFT_HEIGHT_LEG;
		rightArmGoal = this.MAX_RIGHT_HEIGHT_ARM;
	    }
	    if (val2 > 0) {
		leftArmGoal = this.MAX_LEFT_HEIGHT_ARM;
		rightLegGoal = this.MAX_RIGHT_HEIGHT_LEG;
	    } else {
		leftArmGoal = this.MIN_LEFT_HEIGHT_ARM;
		rightLegGoal = this.MIN_RIGHT_HEIGHT_LEG;
	    }
	    // assemble Bioloid commands
	    leftLegControl.setPosition(DynamixelParams
		    .approxDegree(leftLegGoal));
	    leftLegControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));
	    rightArmControl.setPosition(DynamixelParams
		    .approxDegree(rightArmGoal));
	    rightArmControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    leftArmControl.setPosition(DynamixelParams
		    .approxDegree(leftArmGoal));
	    leftArmControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));
	    rightLegControl.setPosition(DynamixelParams
		    .approxDegree(rightLegGoal));
	    rightLegControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    nextMotion.addBioloidControl(leftLegControl);
	    nextMotion.addBioloidControl(rightArmControl);
	    nextMotion.addBioloidControl(leftArmControl);
	    nextMotion.addBioloidControl(rightLegControl);
	    System.out.println(nextMotion);
	}
	}
	return new Control(nextMotion);
    }

	public double[] computeModel(double t, double[] x, double alpha,
			boolean isDeriv, double timeDyn) {
		// TODO Auto-generated method stub
		return null;
	}
}
