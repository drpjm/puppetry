package edu.gatech.grits.puppetctrl.model;

import edu.gatech.grits.puppetctrl.mdl.util.ActionAdapter;
import edu.gatech.grits.puppetctrl.opt.PuppetCosts;
import edu.gatech.grits.puppetctrl.opt.Solution;
import flanagan.integration.DerivnFunction;
import flanagan.math.*;

/**
 * Class that implements the costate dynamics for the puppet:
 * lambda and mu.
 * @author pmartin
 *
 */
public class CostateModel implements DerivnFunction {

	private final PuppetCosts costs;
	private final Solution forwardSolution;
	private final ActionAdapter action;
	private final float alpha;
	
	public CostateModel(final PuppetCosts pc, final Solution s, final ActionAdapter aa, float a){
		costs = pc;
		forwardSolution = s;
		action = aa;
		alpha = a;
	}
	
	public double[] derivn(double t, double[] cs) {

		ArrayMaths costate = new ArrayMaths(cs);
		//TODO: this hack is kind of important...I have no automatic numerical error checking.
		if(t < 0){
			t = 0;
		}
		double[] x = forwardSolution.getSolutionAt(t);
		double[] r = new double[x.length];
		
		// lambda - row vector
		// TODO: Watch out for "-0" terms! Make sure they do not cause numerical problems.
		double[] curr = new double[cs.length];
		double[] lambda = costs.dLdx(x, r).times(-1).getRowCopy(0);
		int lamIdx = lambda.length-1;
		for(int i = 0; i < lambda.length; i++){
			curr[i] = lambda[i];
		}
		
		// mu
		Matrix lCurr = Matrix.columnMatrix(costate.subarray_as_double(0, lamIdx));
//		double[] input = action.computeModel(t, cs, alpha, true, -1);
//		ArrayMaths dfda = new ArrayMaths(input);
//		Matrix sol = lCurr.transpose().times(Matrix.columnMatrix(dfda.subarray_as_double(0, lamIdx)));
//		
//		double mu = sol.getElement(0, 0); 
//		curr[curr.length-2] = mu;
//		curr[curr.length-1] = input[input.length-1];
		
		return cs;
	}

}
