package edu.gatech.grits.puppetctrl.opt;

import flanagan.integration.DerivFunction;
import flanagan.integration.DerivnFunction;
import flanagan.integration.RungeKutta;
import flanagan.math.Matrix;

/**
 * This class extends the RungeKutta by returning the total trajectory calculated
 * during the integration routine.
 * @author pmartin
 *
 */
public class MyRungeKutta extends RungeKutta {

	public MyRungeKutta(){
		
	}
	
	public static double[] fourthOrderTraj(DerivFunction g, double x0, double y0, double xn, double h){
		double  k1 = 0.0D, k2 = 0.0D, k3 = 0.0D, k4 = 0.0D;
		double  x = 0.0D, y = y0;

		// Calculate nsteps
		double ns = (xn - x0)/h;
		ns = Math.rint(ns);
		int nsteps = (int) ns;  // number of steps
		h = (xn - x0)/ns;
		double[] output = new double[nsteps];

		for(int i=0; i<nsteps; i++){
			x = x0 + i*h;

			k1 = h*g.deriv(x, y);
			k2 = h*g.deriv(x + h/2, y + k1/2);
			k3 = h*g.deriv(x + h/2, y + k2/2);
			k4 = h*g.deriv(x + h, y + k3);

			y += k1/6 + k2/3 + k3/3 + k4/6;
			output[i] = y;
		}
		return output;
	}


	/**
	 * This method returns the trajectory calculated by Runge-Kutta.
	 * @author pmartin
	 * @param g
	 * @param x0
	 * @param y0
	 * @param xn
	 * @param h
	 * @return
	 */
	public static Matrix fourthOrderTraj(DerivnFunction g, double x0, double[] y0, double xn, double h){
		int nequ = y0.length;
		double[] k1 =new double[nequ];
		double[] k2 =new double[nequ];
		double[] k3 =new double[nequ];
		double[] k4 =new double[nequ];
		double[] y =new double[nequ];
		double[] yd =new double[nequ];
		double[] dydx =new double[nequ];
		double x = 0.0D;

		boolean isBackwards = false;
		// check for backwards integration
		if(xn - x0 < 0){
			isBackwards = true;
		}

		// Calculate nsteps
		double ns = Math.abs(xn - x0)/h;
		ns = Math.rint(ns);
		int nsteps = (int) ns;
		h = Math.abs(xn - x0)/ns;

		// added new trajectory
		Matrix traj = new Matrix(new double[nequ][nsteps]);

		// initialise
		for(int i=0; i<nequ; i++)
			y[i] = y0[i];

		// iteration over allowed steps
		if(isBackwards){
			for(int j=0; j<nsteps; j++){
				x  = x0 - j*h;
				dydx = g.derivn(x, y);
				for(int i=0; i<nequ; i++)k1[i] = h*dydx[i];

				for(int i=0; i<nequ; i++)yd[i] = y[i] + k1[i]/2;
				dydx = g.derivn(x - h/2, yd);
				for(int i=0; i<nequ; i++)k2[i] = h*dydx[i];

				for(int i=0; i<nequ; i++)yd[i] = y[i] + k2[i]/2;
				dydx = g.derivn(x - h/2, yd);
				for(int i=0; i<nequ; i++)k3[i] = h*dydx[i];

				for(int i=0; i<nequ; i++)yd[i] = y[i] + k3[i];
				dydx = g.derivn(x - h, yd);
				for(int i=0; i<nequ; i++)k4[i] = h*dydx[i];

				for(int i=0; i<nequ; i++){
					y[i] += k1[i]/6 + k2[i]/3 + k3[i]/3 + k4[i]/6;
					traj.setElement(i, (nsteps-1)-j, y[i]);
				}
			}
		}
		else{
			for(int j=0; j<nsteps; j++){
				x  = x0 + j*h;
				dydx = g.derivn(x, y);
				for(int i=0; i<nequ; i++)k1[i] = h*dydx[i];

				for(int i=0; i<nequ; i++)yd[i] = y[i] + k1[i]/2;
				dydx = g.derivn(x + h/2, yd);
				for(int i=0; i<nequ; i++)k2[i] = h*dydx[i];

				for(int i=0; i<nequ; i++)yd[i] = y[i] + k2[i]/2;
				dydx = g.derivn(x + h/2, yd);
				for(int i=0; i<nequ; i++)k3[i] = h*dydx[i];

				for(int i=0; i<nequ; i++)yd[i] = y[i] + k3[i];
				dydx = g.derivn(x + h, yd);
				for(int i=0; i<nequ; i++)k4[i] = h*dydx[i];

				for(int i=0; i<nequ; i++){
					y[i] += k1[i]/6 + k2[i]/3 + k3[i]/3 + k4[i]/6;
					traj.setElement(i, j, y[i]);
				}
			}

		}

		return traj;
	}

}
