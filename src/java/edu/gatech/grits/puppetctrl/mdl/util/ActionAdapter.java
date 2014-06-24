package edu.gatech.grits.puppetctrl.mdl.util;

import java.io.Serializable;

import edu.gatech.grits.puppetctrl.util.Control;

/**
 * Interface that creates the structure for using control laws, called 'actions'. Implementing
 * classes create a control law that is inserted into actOn() method
 * @author pmartin
 *
 */
public abstract class ActionAdapter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -880805717876799842L;

	/**
	 * Contains the control law needed to perform an action.
	 */
//	public Control actOn(float scaleParam);
	public abstract Control actOn(float scaleParam, long currTime);
//	/**
//	 * Function that returns the vector of joint actions at time instant t. Can be
//	 * a feedback controller if x is provided.
//	 * @param t - time
//	 * @param x - state
//	 * @param alpha - scaling
//	 * @return
//	 */
//	public double[] computeModel(double t, double[] x, double alpha, boolean isDeriv, double timeDyn);
}
