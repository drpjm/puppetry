package edu.gatech.grits.puppetctrl.opt;

import flanagan.interpolation.CubicSpline;
import flanagan.math.ArrayMaths;
import flanagan.math.Matrix;

/**
 * Object that holds the data structures that represent the solution
 * to a differential equation and its cost functional value, J.
 * @author pmartin
 *
 */
public class Solution {

	private Matrix trajectory;
	private double[] time;
	private double J;
	
	public Solution(Matrix m, double[] t, double j){
		trajectory = m;
		this.time = t;
		J = j;
	}

	public double[] getTrajectoryOf(final int idx){
		return trajectory.getRowCopy(idx);
	}
	
	public Matrix getTrajectory() {
		return trajectory;
	}

	public double getJ() {
		return J;
	}

	public double[] getTime() {
		return time;
	}

	/**
	 * This function interpolates the solution to determine the value of the
	 * trajectory at the specified time, t.
	 * @param t
	 * @return
	 */
	public final double[] getSolutionAt(double t){
		
		double[] ret = new double[trajectory.getNrow()];
		
		if(t >= this.time[0] && t <= this.time[this.time.length-1]){
			
			ArrayMaths ams = new ArrayMaths(this.time);
			int nearIdx = ams.nearestIndex(t);
			int idx1;
			int idx2;
			idx1 = nearIdx - 1;
			idx2 = nearIdx + 1;
			// check the indices to make sure there will not be an array overrrun
			if(nearIdx == 0){
				idx1 = 0;
				idx2 = idx1+2;
			}
			if(nearIdx == this.time.length-1){
				idx2 = this.time.length-1;
				idx1 = idx2 - 2;
			}
			
			double[] x = ams.subarray_as_double(idx1, idx2);
			// for each row of the trajectory, interpolate
			for(int k = 0; k < trajectory.getNrow(); k++){
				ArrayMaths ams2 = new ArrayMaths(trajectory.getRowCopy(k));
				double[] y = ams2.subarray_as_double(idx1, idx2);
				CubicSpline cs = new CubicSpline(x,y);
				ret[k] = cs.interpolate(t);
			}
		}
		else{
			throw new IllegalArgumentException("Requested time out of bounds!");
		}
		return ret;
		
	}
	
	@Override
	public String toString() {
		String str = "";
		
		int nRows = trajectory.getNrow();
		int nCols = trajectory.getNcol();
		
		str += "Trajectory dim: [" + nRows + "," + nCols + "]\n";
		str += "Cost: " + J;
		
		return str;
	}
	
}
