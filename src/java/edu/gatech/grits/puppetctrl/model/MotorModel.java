package edu.gatech.grits.puppetctrl.model;

public class MotorModel {

	/**
	 * Motor model.
	 * @param t
	 * @param alpha
	 * @param maxSpeed
	 * @param angleRange
	 * @return
	 */
	public static final double move(final double t, final double alpha, final double maxSpeed, final double angleRange){
		
		double period = 2*angleRange / (alpha*Math.abs(maxSpeed));
		double f = 1 / period;
		
		double u = alpha*maxSpeed*(4/Math.PI)*(Math.sin(2*Math.PI*f*t) + (1/3)*Math.sin(6*Math.PI*f*t));
		
		return u;
	}
	/**
	 * Function that implements the derivative of the motor model w.r.t. alpha.
	 * @param t
	 * @param alpha
	 * @param maxSpeed
	 * @param angleRange
	 * @return
	 */
	public static final double diffMove(final double t, final double alpha, final double maxSpeed, final double angleRange){

		double period = 2*angleRange / (alpha*Math.abs(maxSpeed));
		double f = 1 / period;
		
		double t1 = (4 / Math.PI)*maxSpeed*(Math.sin(2*Math.PI*f*t) + (1/3)*Math.sin(6*Math.PI*f*t));
		double t2 = 8*maxSpeed*f*(Math.cos(2*Math.PI*f*t) + Math.cos(6*Math.PI*f*t));
		
		double u = t1 + t2;
		
		return u;
	}
}
