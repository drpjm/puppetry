package edu.gatech.grits.puppetctrl.model;

import edu.gatech.grits.puppetctrl.mdl.util.ActionAdapter;
import flanagan.integration.DerivnFunction;

public class PuppetModel implements DerivnFunction {

	private ActionAdapter action;
	private float alpha;
	
	/**
	 * The puppet model is kinematic, so the dynamics only has an input:
	 * i.e. xdot = G*u
	 * @param m
	 */
	public PuppetModel(ActionAdapter action, float a){
		this.action = action;
		this.alpha = a;
	}
	
	public double[] derivn(double t, double[] x) {
		
		// forward time dynamics, not a derivative model
		double[] curr = new double[1]; 
//			action.computeModel(t, x, alpha, false, 1);
		
		return curr;
	}

}
