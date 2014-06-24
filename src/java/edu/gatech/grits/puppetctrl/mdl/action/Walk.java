package edu.gatech.grits.puppetctrl.mdl.action;

import edu.gatech.grits.puppetctrl.comm.bioloid.*;
import edu.gatech.grits.puppetctrl.mdl.util.ActionAdapter;
import edu.gatech.grits.puppetctrl.model.MotorModel;
import edu.gatech.grits.puppetctrl.model.PuppetMotorMap;
import edu.gatech.grits.puppetctrl.util.Control;

public class Walk extends ActionAdapter {

	private String name;

	// fastest walk is 40 deg/sec
	private final float MAX_SPEED = 40.0f;
	private final double MAX_SPEED_RAD = Math.toRadians(MAX_SPEED);

	private final float MAX_LEFT_HEIGHT = 130.0f;
	private final float MIN_LEFT_HEIGHT = 60.0f;

	private final float MAX_RIGHT_HEIGHT = 170.0f;
	private final float MIN_RIGHT_HEIGHT = 240.0f;

	private final double MAX_HEIGHT_RAD = Math.toRadians(this.MAX_LEFT_HEIGHT);
	private final double MIN_HEIGHT_RAD = Math.toRadians(this.MIN_LEFT_HEIGHT);
	
	public Walk() {
		System.out.println("Walk created.");
		name = "walk";
	}

	@Override
	public String toString() {
		return name;
	}

	public Control actOn(float scaleParam, long currTime) {

		float liftSpeed = scaleParam * this.MAX_SPEED;
		MotionPacket nextMotion = new MotionPacket();

		float leftArmGoal, leftLegGoal;
		float rightArmGoal, rightLegGoal;

		long leftPeriod = (long) ((Math.abs(this.MAX_LEFT_HEIGHT
				- this.MIN_LEFT_HEIGHT) / (scaleParam * this.MAX_SPEED)) * 2);

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
			leftLegGoal = this.MAX_LEFT_HEIGHT;
			leftLegControl.setPosition(DynamixelParams
					.approxDegree(leftLegGoal));
			leftLegControl.setVelocity(DynamixelParams
					.approxDegPerSec(liftSpeed));

			rightArmGoal = this.MAX_RIGHT_HEIGHT;
			rightArmControl.setPosition(DynamixelParams
					.approxDegree(rightArmGoal));
			rightArmControl.setVelocity(DynamixelParams
					.approxDegPerSec(liftSpeed));

			nextMotion.addBioloidControl(leftLegControl);
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
			if (val1 < 0) {
				leftLegGoal = this.MIN_LEFT_HEIGHT;
				rightArmGoal = this.MIN_RIGHT_HEIGHT;
			} else {
				leftLegGoal = this.MAX_LEFT_HEIGHT;
				rightArmGoal = this.MAX_RIGHT_HEIGHT;
			}
			if (val2 > 0) {
				leftArmGoal = this.MAX_LEFT_HEIGHT;
				rightLegGoal = this.MAX_RIGHT_HEIGHT;
			} else {
				leftArmGoal = this.MIN_LEFT_HEIGHT;
				rightLegGoal = this.MIN_RIGHT_HEIGHT;
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
		}

		return new Control(nextMotion);
	}

	public double[] computeModel(double t, double[] x, double alpha, boolean isDeriv, double timeDyn) {

		double[] input = new double[x.length];
		double s = x[x.length-1];	// grabs time value
		double angleRange = this.MAX_HEIGHT_RAD - this.MIN_HEIGHT_RAD;
		double initPeriod = 2*angleRange/(alpha*this.MAX_SPEED_RAD);
		
		// theta does not move!
		input[0] = 0;
		input[2] = 0;

		// starts the opposite arm and leg initially
		if(s < initPeriod / 2){
			if(!isDeriv){
				input[1] = MotorModel.move(s, alpha, MAX_SPEED_RAD, angleRange);// Phi R
				input[5] = MotorModel.move(s, alpha, MAX_SPEED_RAD, angleRange);// Psi L
			}
			else{
				input[1] = MotorModel.diffMove(s, alpha, MAX_SPEED_RAD, angleRange);// Phi R
				input[5] = MotorModel.diffMove(s, alpha, MAX_SPEED_RAD, angleRange);// Psi L
			}
			
			input[3] = 0; // Phi L
			input[4] = 0; // Psi R
		}
		else{
			double sOffset = s - initPeriod / 2;
			
			if(!isDeriv){
			    input[1] = MotorModel.move(s, alpha, MAX_SPEED_RAD, angleRange);
			    input[5] = MotorModel.move(s, alpha, MAX_SPEED_RAD, angleRange);
				
				input[3] = MotorModel.move(sOffset, alpha, MAX_SPEED_RAD, angleRange); // Phi L
				input[4] = MotorModel.move(sOffset, alpha, MAX_SPEED_RAD, angleRange); // Psi R
			}
			else{
			    input[1] = MotorModel.diffMove(s, alpha, MAX_SPEED_RAD, angleRange);
			    input[5] = MotorModel.diffMove(s, alpha, MAX_SPEED_RAD, angleRange);
				
				input[3] = MotorModel.diffMove(sOffset, alpha, MAX_SPEED_RAD, angleRange); // Phi L
				input[4] = MotorModel.diffMove(sOffset, alpha, MAX_SPEED_RAD, angleRange); // Psi R
				
			}
		}
		
		// time dynamics
		input[input.length-1] = timeDyn;
		
		return input;
	}

}
