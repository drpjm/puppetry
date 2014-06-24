package edu.gatech.grits.puppetctrl.opt;

import flanagan.math.*;

/**
 * Class that holds the data and functionality for evaluating cost values and
 * the values of their derivatives.
 * @author pmartin
 *
 */
public class PuppetCosts {

	private final double[] alphaPenalties;
	private final double[] tauPenalties;
	private final Matrix Qpenalty;
	private final Matrix Ppenalty;
	
	public PuppetCosts(final Matrix Q, final Matrix P, final double[] alphaPenalties, final double[] tauPenalties){
		Qpenalty = Q;
		Ppenalty = P;
		this.alphaPenalties = alphaPenalties;
		this.tauPenalties = tauPenalties;
	}

	public double Lfunc(final double[] x, final double[] r){
		
		Matrix diff = Matrix.columnMatrix(x).minus(Matrix.columnMatrix(r));
		
		Matrix tmp = Matrix.transpose(diff).times(this.Ppenalty).times(diff);
		
		return tmp.getElement(0, 0);
		
	}
	
	public Matrix dLdx(final double[] x, final double[] r){
		
		Matrix diff = Matrix.columnMatrix(x).minus(Matrix.columnMatrix(r));
		
		Matrix tmp = Matrix.transpose(diff).times(this.Ppenalty).times(2);
		
		return tmp;
	}
	
	public double Psifunc(final double[] x, final double[] r){
		
		Matrix diff = Matrix.columnMatrix(x).minus(Matrix.columnMatrix(r));
		
		Matrix tmp = Matrix.transpose(diff).times(this.Qpenalty).times(diff);
		
		return tmp.getElement(0, 0);		
		
	}
	
	public Matrix dPsidx(final double[] x, final double[] r){

		Matrix diff = Matrix.columnMatrix(x).minus(Matrix.columnMatrix(r));
		
		Matrix tmp = Matrix.transpose(diff).times(this.Qpenalty).times(2);
		
		return tmp;

	}
	
	public double Cfunc(double alpha, int idx){
		
		return alphaPenalties[idx]*Math.pow(alpha, 2);
		
	}
	
	public double dCda(double alpha, int idx){
		return 2*alphaPenalties[idx]*alpha;
	}
	
	public double Dfunc(double tau, double tau_nom, int idx){
		return tauPenalties[idx]*Math.pow((tau_nom - tau), 2);
	}
	
	public double dDdtau(double tau, double tau_nom, int idx){
		return 2*tauPenalties[idx]*(tau_nom - tau);
	}
	
	public static void main(String[] args){
		
		double[] ap = new double[]{1};
		double[] tp = new double[]{1};
		
		Matrix Q = Matrix.identityMatrix(2);
		Matrix P = Matrix.identityMatrix(2);
		
		PuppetCosts pc = new PuppetCosts(Q,P,ap,tp);
		double[] x = new double[]{1, 2};
		double[] r = new double[]{0, 0};
		System.out.println(pc.Lfunc(x, r));
		System.out.println(pc.dLdx(x, r));
		
		System.out.println(pc.Cfunc(3, 0));
		System.out.println(pc.dCda(3, 0));
		
		System.out.println(pc.Dfunc(5.1, 6, 0));
		System.out.println(pc.dDdtau(5.1, 6, 0));
				
	}
}
