package edu.gatech.grits.puppetctrl.mdl.action;

import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidMotorControl;
import edu.gatech.grits.puppetctrl.comm.bioloid.DynamixelParams;
import edu.gatech.grits.puppetctrl.comm.bioloid.MotionPacket;
import edu.gatech.grits.puppetctrl.mdl.util.ActionAdapter;
import edu.gatech.grits.puppetctrl.model.PuppetMotorMap;
import edu.gatech.grits.puppetctrl.util.Control;

public class WaveRight extends ActionAdapter {

    private String name;
    private final float MAX_LIFT_SPEED = 70.0f;
    private final float MAX_HEIGHT = 100.0f;
    private final float MIN_HEIGHT = 190.0f;

    private final float MAX_ROT_SPEED = 45.0f;
    private final float MAX_ROT_ANGLE = 200.0f;
    private final float MIN_ROT_ANGLE = 145.0f;

    private int numRotates;

    public WaveRight() {
	System.out.println("WaveRight created.");
	name = "waveRight";
	// set to drive out first!
	numRotates = 1;
    }

    @Override
    public String toString() {
	return name;
    }

    public final Control actOn(float scaleParam, long currTime) {
	// long stamp1 = System.nanoTime();

	float rightLiftGoal;
	float rightLiftSpeed;
	float rightRotateGoal;
	float rightRotateSpeed = scaleParam * this.MAX_ROT_SPEED;
	MotionPacket nextMotion = new MotionPacket();
//	System.out.println(currTime);

	// convert period to long format
	long liftPeriod = (long) ((Math.abs(this.MAX_HEIGHT - this.MIN_HEIGHT) / (scaleParam * this.MAX_LIFT_SPEED)) * 1000);
	long rotatePeriod = (long) ((Math.abs(this.MAX_ROT_ANGLE
		- this.MIN_ROT_ANGLE) / (scaleParam * this.MAX_ROT_SPEED)) * 1000);

	// calculate what actions occur at this time
	BioloidMotorControl rightLifting;
	rightLifting = new BioloidMotorControl(PuppetMotorMap.RIGHTARMLIFT);
	BioloidMotorControl rightRotating;
	rightRotating = new BioloidMotorControl(PuppetMotorMap.RIGHTARMROTATE);

	if (currTime < liftPeriod) {
	    // execute lift
	    // System.out.println("LIFT!");
	    rightLiftGoal = this.MAX_HEIGHT;
	    rightLiftSpeed = scaleParam * this.MAX_LIFT_SPEED;

	    rightLifting.setPosition(DynamixelParams
		    .approxDegree(rightLiftGoal));
	    rightLifting.setVelocity(DynamixelParams
		    .approxDegPerSec(rightLiftSpeed));
	    nextMotion.addBioloidControl(rightLifting);
	} else {
	    // wave motions
	    double t = ((double) currTime) / 1000.0 - ((double) liftPeriod)
		    / 1000.0;
	    double tau_r = ((double) rotatePeriod) / 1000.0;
	    double val = Math.sin(2 * Math.PI * (1 / tau_r) * t);
	    // System.out.print("ROTATE!");
	    if (val > 0) {
		// System.out.println("--OUT");
		rightRotateGoal = this.MAX_ROT_ANGLE;
	    } else {
		// System.out.println("--IN");
		rightRotateGoal = this.MIN_ROT_ANGLE;

	    }

	    rightRotating.setPosition(DynamixelParams.approxDegree(rightRotateGoal));
	    rightRotating.setVelocity(DynamixelParams.approxDegPerSec(rightRotateSpeed));
	    nextMotion.addBioloidControl(rightRotating);

	    // lower once two wave motions have occurred
	    if (currTime > (liftPeriod + 2 * rotatePeriod)) {
//		System.out.println("LOWER!");
		rightLiftSpeed = scaleParam * this.MAX_LIFT_SPEED;
		rightLiftGoal = this.MIN_HEIGHT;
		rightLifting.setPosition(DynamixelParams.approxDegree(rightLiftGoal));
		rightLifting.setVelocity(DynamixelParams.approxDegPerSec(rightLiftSpeed));
		nextMotion.addBioloidControl(rightLifting);
	    }
	}

	// long stamp2 = System.nanoTime();
	// System.out.println((stamp2-stamp1) + "ns");
	return new Control(nextMotion);
    }

	public double[] computeModel(double t, double[] x, double alpha,
			boolean isDeriv, double timeDyn) {
		// TODO Auto-generated method stub
		return null;
	}


}
