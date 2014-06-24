package edu.gatech.grits.puppetctrl.mdl.action;

import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidMotorControl;
import edu.gatech.grits.puppetctrl.comm.bioloid.DynamixelParams;
import edu.gatech.grits.puppetctrl.comm.bioloid.MotionPacket;
import edu.gatech.grits.puppetctrl.mdl.util.ActionAdapter;
import edu.gatech.grits.puppetctrl.model.PuppetMotorMap;
import edu.gatech.grits.puppetctrl.util.Control;

public class Jump extends ActionAdapter {

    private String name;
    private boolean isUpStroke; // upstroke means left arm is moving up
    private boolean isIntialStroke; // start with left arm first

    // fastest walk is 40 deg/sec
    private final float MAX_SPEED = 40.0f;

    private final float MAX_LEFT_HEIGHT = 130.0f;
    private final float MIN_LEFT_HEIGHT = 60.0f;

    private final float MAX_RIGHT_HEIGHT = 170.0f;
    private final float MIN_RIGHT_HEIGHT = 240.0f;

    public Jump() {
	System.out.println("Jump created.");
	name = "jump";
	isIntialStroke = true;
    }

    @Override
    public String toString() {
	return name;
    }

    public Control actOn(float scaleParam, long currTime) {

	float liftSpeed = scaleParam * this.MAX_SPEED;
	System.out.println(name.toUpperCase() + "(" + scaleParam + ")");
	MotionPacket nextMotion = new MotionPacket();

	float leftArmGoal, leftLegGoal;
	float rightArmGoal, rightLegGoal;

	long leftPeriod = (long) ((Math.abs(this.MAX_LEFT_HEIGHT
		- this.MIN_LEFT_HEIGHT) / (scaleParam * this.MAX_SPEED)) * 1000 * 2);
	long rightPeriod = (long) ((Math.abs(this.MAX_RIGHT_HEIGHT
		- this.MIN_RIGHT_HEIGHT) / (scaleParam * this.MAX_SPEED)) * 1000 * 2);

	BioloidMotorControl leftLegControl = new BioloidMotorControl(
		PuppetMotorMap.LEFTLEGLIFT);
	BioloidMotorControl leftArmControl = new BioloidMotorControl(
		PuppetMotorMap.LEFTARMLIFT);
	BioloidMotorControl rightArmControl = new BioloidMotorControl(
		PuppetMotorMap.RIGHTARMLIFT);
	BioloidMotorControl rightLegControl = new BioloidMotorControl(
		PuppetMotorMap.RIGHTLEGLIFT);

	// walk is driven by an offset sinusoid
	// starts initial phase
	if (currTime < leftPeriod / 2) {
	    leftArmGoal = this.MAX_RIGHT_HEIGHT;
	    leftArmControl.setPosition(DynamixelParams
		    .approxDegree(leftArmGoal));
	    leftArmControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    rightArmGoal = this.MAX_RIGHT_HEIGHT;
	    rightArmControl.setPosition(DynamixelParams
		    .approxDegree(rightArmGoal));
	    rightArmControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    nextMotion.addBioloidControl(leftArmControl);
	    nextMotion.addBioloidControl(rightArmControl);
	}
	// continue the motions on alternating limbs
	else {
	    // time for initial limbs
	    double tau_lift = ((double) leftPeriod) / 1000.0;
	    double t1 = ((double) currTime) / 1000.0;
	    // time for phase shifted limbs - shifted by half of the period
	    double t2 = ((double) currTime) / 1000.0 - (tau_lift / 2.0);

	    // calculate sin of t1
	    double val1 = Math.sin(2 * Math.PI * (1 / tau_lift) * t1);
	    double val2 = Math.sin(2 * Math.PI * (1 / tau_lift) * t2);
	    
	    leftLegGoal = this.MAX_LEFT_HEIGHT;
	    rightLegGoal = this.MAX_RIGHT_HEIGHT;

	    // assemble Bioloid commands
	    leftLegControl.setPosition(DynamixelParams
		    .approxDegree(leftLegGoal));
	    leftLegControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    rightLegControl.setPosition(DynamixelParams
		    .approxDegree(rightLegGoal));
	    rightLegControl.setVelocity(DynamixelParams
		    .approxDegPerSec(liftSpeed));

	    nextMotion.addBioloidControl(leftLegControl);
	    nextMotion.addBioloidControl(rightLegControl);
	    
	    // lower once two wave motions have occurred
	    if (currTime > (leftPeriod)) {
		System.out.println("LOWER!");
		liftSpeed = scaleParam * this.MAX_SPEED;
		leftArmGoal = this.MIN_LEFT_HEIGHT;

		leftArmControl.setPosition(DynamixelParams.approxDegree(100));
		leftArmControl.setVelocity(DynamixelParams
			.approxDegPerSec(liftSpeed));
		nextMotion.addBioloidControl(leftArmControl);

		liftSpeed = scaleParam * this.MAX_SPEED;
		leftArmGoal = this.MIN_RIGHT_HEIGHT;

		rightArmControl
			.setPosition(DynamixelParams.approxDegree(190));
		rightArmControl.setVelocity(DynamixelParams
			.approxDegPerSec(liftSpeed));
		nextMotion.addBioloidControl(rightArmControl);
		
		liftSpeed = scaleParam * this.MAX_SPEED;
		leftLegGoal = this.MIN_LEFT_HEIGHT;

		leftLegControl.setPosition(DynamixelParams.approxDegree(100));
		leftLegControl.setVelocity(DynamixelParams
			.approxDegPerSec(liftSpeed));
		nextMotion.addBioloidControl(leftLegControl);

		liftSpeed = scaleParam * this.MAX_SPEED;
		leftLegGoal = this.MIN_RIGHT_HEIGHT;

		rightLegControl
			.setPosition(DynamixelParams.approxDegree(190));
		rightLegControl.setVelocity(DynamixelParams
			.approxDegPerSec(liftSpeed));
		nextMotion.addBioloidControl(rightLegControl);
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
