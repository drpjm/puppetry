package edu.gatech.grits.puppetctrl.mdl.action;

import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidMotorControl;
import edu.gatech.grits.puppetctrl.comm.bioloid.DynamixelParams;
import edu.gatech.grits.puppetctrl.comm.bioloid.MotionPacket;
import edu.gatech.grits.puppetctrl.mdl.util.ActionAdapter;
import edu.gatech.grits.puppetctrl.model.MotorModel;
import edu.gatech.grits.puppetctrl.model.PuppetMotorMap;
import edu.gatech.grits.puppetctrl.util.Control;

public class WaveLeft extends ActionAdapter {

	private String name;
	private final float MAX_LIFT_SPEED = 70.0f;
	private final double MAX_LIFT_SPEED_RAD = Math.toRadians(MAX_LIFT_SPEED);
	
	private final float MAX_HEIGHT = 190.0f;
	private final float MIN_HEIGHT = 100.0f;
	private final double MAX_HEIGHT_RAD = Math.toRadians(MAX_HEIGHT);
	private final double MIN_HEIGHT_RAD = Math.toRadians(MIN_HEIGHT);

	private final float MAX_ROT_SPEED = 45.0f;
	private final double MAX_ROT_SPEED_RAD = Math.toRadians(MAX_ROT_SPEED);
	
	private final float MAX_ROT_ANGLE = 100.0f;
	private final float MIN_ROT_ANGLE = 145.0f;
	private final double MAX_ROT_RAD = Math.toRadians(MAX_ROT_ANGLE);
	private final double MIN_ROT_RAD = Math.toRadians(MIN_ROT_ANGLE);

	private final float DELTA_T = 0.1f;	//0.1s = 100ms
	private float currHeight;
	private float currRotation;
	
	
	public WaveLeft() {
		System.out.println("WaveLeft created.");
		name = "waveLeft";
		currHeight = this.MIN_HEIGHT;
		currRotation = this.MIN_ROT_ANGLE;
	}

	@Override
	public String toString() {
		return name;
	}

	public final Control actOn(float scaleParam, long currTime) {
		// long stamp1 = System.nanoTime();

		float leftLiftGoal;
		float leftLiftSpeed;
		float leftRotateGoal;
		leftLiftSpeed = scaleParam * this.MAX_LIFT_SPEED;
		float leftRotateSpeed = scaleParam * this.MAX_ROT_SPEED;
		MotionPacket nextMotion = new MotionPacket();

		// convert period to long format
		long liftPeriod = (long) ((Math.abs(this.MAX_HEIGHT - this.MIN_HEIGHT) / (scaleParam * this.MAX_LIFT_SPEED)));
		long rotatePeriod = (long) ((Math.abs(this.MAX_ROT_ANGLE
				- this.MIN_ROT_ANGLE) / (scaleParam * this.MAX_ROT_SPEED)));

		// calculate what actions occur at this time
		BioloidMotorControl leftLifting;
		leftLifting = new BioloidMotorControl(PuppetMotorMap.LEFTARMLIFT);
		BioloidMotorControl leftRotating;
		leftRotating = new BioloidMotorControl(PuppetMotorMap.LEFTARMROTATE);

		if (currTime < liftPeriod) {
			// execute lift
//			leftLiftGoal = this.MAX_HEIGHT;
			currHeight += leftLiftSpeed * this.DELTA_T;
			leftLiftGoal = currHeight; // increment of angle
//			System.out.println("height = " + currHeight);

			leftLifting.setPosition(DynamixelParams.approxDegree(leftLiftGoal));
			leftLifting.setVelocity(DynamixelParams.approxDegPerSec(leftLiftSpeed));
			nextMotion.addBioloidControl(leftLifting);
		} else {
			// wave motions
			double t = ((double) currTime) / 1000.0 - ((double) liftPeriod)
			/ 1000.0;
			double tau_r = ((double) rotatePeriod) / 1000.0;
			double val = Math.sin(2 * Math.PI * (1 / tau_r) * t);
			if (val > 0) {
				if(currRotation > this.MAX_ROT_ANGLE){
					currRotation -= leftRotateSpeed * this.DELTA_T;
				}
				else{
					currRotation = this.MAX_ROT_ANGLE;
				}
			} else {
				if(currRotation < this.MIN_ROT_ANGLE){
					currRotation += leftRotateSpeed * this.DELTA_T;
				}
				else{
					currRotation = this.MIN_ROT_ANGLE;
				}
			}
//			System.out.println("rotation = " + currRotation);
			leftRotateGoal = currRotation;
			leftRotating.setPosition(DynamixelParams.approxDegree(leftRotateGoal));
			leftRotating.setVelocity(DynamixelParams.approxDegPerSec(leftRotateSpeed));
			nextMotion.addBioloidControl(leftRotating);

			// lower once two wave motions have occurred
			if (currTime > (liftPeriod + 2 * rotatePeriod)) {
				leftLiftSpeed = scaleParam * this.MAX_LIFT_SPEED;
				if(currHeight > this.MIN_HEIGHT){
					currHeight -= leftLiftSpeed * this.DELTA_T;
				}
				else{
					currHeight = this.MIN_HEIGHT;
				}
				leftLiftGoal = currHeight;
//				System.out.println("height = " + currHeight);
				
				leftLifting.setPosition(DynamixelParams.approxDegree(leftLiftGoal));
				leftLifting.setVelocity(DynamixelParams.approxDegPerSec(leftLiftSpeed));
				nextMotion.addBioloidControl(leftLifting);
			}
		}

		// long stamp2 = System.nanoTime();
		// System.out.println((stamp2-stamp1) + "ns");
		return new Control(nextMotion);
	}

	public double[] computeModel(double t, double[] x, double alpha, boolean isDeriv, double timeDyn) {

		double[] input = new double[x.length];
		double s = x[x.length-1];
		double liftRange = Math.abs(this.MAX_HEIGHT_RAD - this.MIN_HEIGHT_RAD);
		double rotateRange = Math.abs(this.MAX_ROT_RAD - this.MIN_ROT_RAD);
		
		double liftPeriod = 2*liftRange / (alpha * this.MAX_LIFT_SPEED_RAD);
		double rotatePeriod =  2*rotateRange / (alpha * this.MAX_ROT_SPEED_RAD);
		
		if(s < liftPeriod / 2){
			// only lifting!
			input[2] = 0;	//ThetaL
			if(!isDeriv){
				input[3] = MotorModel.move(s, alpha, MAX_LIFT_SPEED_RAD, liftRange);	//PhiL
			}
			else{
				input[3] = MotorModel.diffMove(s, alpha, MAX_LIFT_SPEED_RAD, liftRange);	//PhiL
			}
		}
		else{
			double rotate_t = s - liftPeriod/2;
			if(!isDeriv){
				input[2] = MotorModel.move(rotate_t, alpha, -MAX_ROT_SPEED_RAD, rotateRange);	//ThetaL
			}
			else{
				input[2] = MotorModel.diffMove(rotate_t, alpha, -MAX_ROT_SPEED_RAD, rotateRange);	//ThetaL
			}
			input[3] = 0;	//PhiL
			
			// lower arm after the rotation
			double time = (liftPeriod/2) + rotatePeriod;
			if(s > time){
				double lower_t = s - time;
				input[2] = 0;
				if(!isDeriv){
					input[3] = MotorModel.move(lower_t, alpha, -MAX_LIFT_SPEED_RAD, liftRange);
				}
				else{
					input[3] = MotorModel.diffMove(lower_t, alpha, -MAX_LIFT_SPEED_RAD, liftRange);
				}
			}
		}
		
		// freeze right arm
		input[0] = 0;
		input[1] = 0;
		// freeze legs
		input[4] = 0;
		input[5] = 0;		
		// time dynamics
		input[input.length-1] = timeDyn;
		
		return input;
		
	}

}
