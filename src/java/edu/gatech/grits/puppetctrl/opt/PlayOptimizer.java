package edu.gatech.grits.puppetctrl.opt;

import javolution.util.FastList;
import javolution.util.FastMap;
import edu.gatech.grits.puppetctrl.mdl.util.Mode;
import edu.gatech.grits.puppetctrl.mdl.util.ModeString;
import edu.gatech.grits.puppetctrl.model.CostateModel;
import edu.gatech.grits.puppetctrl.model.PuppetModel;
import flanagan.math.ArrayMaths;
import flanagan.math.Fmath;
import flanagan.math.Matrix;

/**
 * A class that ports the Matlab optimization routine into Java.
 * @author pmartin
 *
 */
public class PlayOptimizer {

	private FastMap<String,ModeString> play;
	private FastList<double[]> playerTaus;
	private FastList<double[]> playerAlphas;
	private FastList<Double> playerTfs;
	private FastList<ModeString> modeStrings;
	private final FastList<ModeString> nomModeStrings;
	private FastList<PuppetCosts> playerCosts;
	private FastList<double[]> initConditions;
	

	private FastList<Matrix> costSolutions;
	private FastList<Matrix> trajSolutions;

	private double stepSize;
	private int maxSteps;

	public PlayOptimizer(FastMap<String, ModeString> inputPlay, FastList<double[]> X0, double stepSize, int maxIter){

		this.stepSize = stepSize;
		this.maxSteps = maxIter;

		this.play= inputPlay;
		this.playerTaus = new FastList<double[]>();
		this.playerAlphas = new FastList<double[]>();
		this.playerTfs = new FastList<Double>();
		
		this.modeStrings = new FastList<ModeString>();
		
		this.playerCosts = new FastList<PuppetCosts>();
		this.initConditions = X0;

		// extract information about play for each player
		int idx = 0;
		for(FastMap.Entry<String, ModeString> curr = play.head(), end = play.tail(); (curr = curr.getNext()) != end;){
			ModeString ms = (ModeString)curr.getValue();
			double[] aPenalties = new double[ms.getLength()];
			double[] tPenalties = new double[ms.getLength()];
			
			modeStrings.add(ms);
			double[] currTaus = new double[ms.getLength()];
			double[] currAlphas = new double[ms.getLength()];
			for(int i = 0; i < ms.getLength(); i++){
				currTaus[i] = ms.getModeAt(i).getTimeLength();
				currAlphas[i] = ms.getModeAt(i).getScale();
				// penalty factors
				aPenalties[i] = 1;
				tPenalties[i] = 1;
			}
			playerTaus.add(currTaus);
			playerAlphas.add(currAlphas);
			Matrix Q = Matrix.identityMatrix(initConditions.get(idx).length-1);
			Matrix P = Matrix.identityMatrix(initConditions.get(idx).length-1);
			Q.setElement(4, 4, 0);
			Q.setElement(5, 5, 0);
			// TODO: remember to change these values when needed!
			P.setElement(4, 4, 0);
			P.setElement(5, 5, 0);
			PuppetCosts pc = new PuppetCosts(Q, P, aPenalties, tPenalties);
			playerCosts.add(pc);
			
			
			// calculate final time
			ArrayMaths ams = new ArrayMaths(currTaus);
			double tf = ams.sum_as_double();
			playerTfs.add(tf);

			System.out.println(curr.getKey() + ": Play length: " + tf);
			idx++;

		}
		// store the nominal mode strings		
		nomModeStrings = new FastList<ModeString>(modeStrings);
		
		// initialize solution matrices
		costSolutions = new FastList<Matrix>();
		trajSolutions = new FastList<Matrix>();
		for(int i = 0; i < play.size(); i++){
			trajSolutions.add(null);
		}
	}

	/*
	 * Method that implements the Matlab optimization routine.
	 */
	public final void optimize(){

		int idx = 0;
//		while(idx < maxSteps){
			
			// for each puppet...
			for(int i = 0; i < this.play.size(); i++){
				
				// forward simulation
				double t0 = 0;
				double tf = playerTfs.get(i);
				Solution fwdSol = forward(i, t0, tf);
				trajSolutions.set(i, fwdSol.getTrajectory());
				double currJ = fwdSol.getJ();
				System.out.println("Current cost = " + currJ);

//				double[] t1 = fwdSol.getSolutionAt(0);
				
				// backward simulation
				Solution backSol = backward(i, t0, tf, fwdSol);
				
				// Armijo step calculation

				// gradient descent
				
			}
			
//			idx++;
//		}

	}

	private final Solution forward(final int puppetIdx, final double t0, final double tf){
		
		// initialize cost values
		double Ltotal = 0;
		double Ctotal = 0;
		double Dtotal = 0;
		// initial conditions
		double currT0 = t0;
		double currTf = 0;
		double[] currX0 = initConditions.get(puppetIdx);
		double ns = (tf-t0) / stepSize;
		ns = Math.rint(ns);
		int nsteps = (int)ns;
		// create solution matrix
		Matrix finalSolMtrx = new Matrix(currX0.length, nsteps+1);
		int startIdx = 0;
		
		// iterate through modes
		ModeString currModes = modeStrings.get(puppetIdx);
		// load initial value into solution matrix
		for(int i = 0; i < currX0.length; i++){
			finalSolMtrx.setElement(i, 0, currX0[i]);
		}
		startIdx++;
		
		for(int k = 0; k < currModes.getLength(); k++){
			
			// current mode initialization
			Mode currMode = currModes.getModeAt(k);
			currTf = currMode.getTimeLength() + currT0;
			
			PuppetModel currModel = new PuppetModel(currMode.getAction(), currMode.getScale());
			
			// integrate diff eq.
			Matrix currSolMtrx = MyRungeKutta.fourthOrderTraj(currModel, currT0, currX0, currTf, stepSize);
			
			// reset initial conditions
			int len = currSolMtrx.getNumberOfColumns();
			currX0 = currSolMtrx.getColumnCopy(len-1);
			currX0[currX0.length-1] = 0;
			currT0 = currTf;
			
			// store current solution
			finalSolMtrx.setSubMatrix(0, startIdx, currSolMtrx.getNrow(), currSolMtrx.getNcol()+startIdx, currSolMtrx.getArrayCopy());
			startIdx = len;

			// calculate costs
			int numJoint = currSolMtrx.getNrow()-1;
			double[] r = new double[numJoint];
			PuppetCosts currCosts = playerCosts.get(puppetIdx);
			for(int n = 0; n < currSolMtrx.getNumberOfColumns(); n++){
				Matrix m = currSolMtrx.getSubMatrix(0, n, numJoint-1, n);	// hack! needed to subtract 1 again!
				double[] c = m.getColumnCopy(0);
				double Lcurr = currCosts.Lfunc(c, r);
				Ltotal += Lcurr*stepSize;
			}
			
			Ctotal += currCosts.Cfunc(currMode.getScale(), k);
			if(k < currModes.getModes().size() - 1){
				Dtotal += currCosts.Dfunc(currMode.getTimeLength(), nomModeStrings.get(puppetIdx).getModeAt(k).getTimeLength(), k);
			}
			
		}
		
		double J = Ltotal + Ctotal + Dtotal;
		// strip out the time trajectory...we don't care
		int rowEnd = finalSolMtrx.getNrow()-2;
		int colEnd = finalSolMtrx.getNcol()-1;
		// store time
		double[] time = new double[finalSolMtrx.getNcol()];
		double t = t0;
		for(int k = 0; k < time.length; k++){
			// need to truncate to prevent precision errors later
			time[k] = Fmath.truncate(t, 6);
			t += stepSize;
		}
		
		return new Solution(finalSolMtrx.getSubMatrix(0, 0, rowEnd, colEnd), time, J);
		
	}
	
	private final Solution backward(final int puppetIdx, final double t0, final double tf, final Solution fwd){
		
		double ns = Math.abs(tf-t0) / stepSize;
		ns = Math.rint(ns);
		int nsteps = (int)ns;

		double currT0 = t0;
		double currTf = tf;
		
		double[] cs_f = new double[8];
		Matrix finalSolMatrix = new Matrix(cs_f.length, nsteps+1);
		
		ModeString currModes = this.modeStrings.get(puppetIdx);
		for(int k = currModes.getLength()-1; k >= 0; k--){

			Mode currMode = currModes.getModeAt(k);
			// initial conditions - 6 dim for lambda, 1 dim for mu, 1 dim for time
			double[] x_f = fwd.getSolutionAt(currTf);
			double[] r = new double[x_f.length];
			double[] lam_f  = playerCosts.get(puppetIdx).dPsidx(x_f, r).getRowCopy(0);
			double mu_f = playerCosts.get(puppetIdx).dCda(currMode.getScale(), k);
			for(int j = 0; j < lam_f.length; j++){
				cs_f[j] = lam_f[j];
			}
			cs_f[cs_f.length-2] = mu_f;
			cs_f[cs_f.length-1] = currTf;

			CostateModel csm = new CostateModel(playerCosts.get(puppetIdx), fwd, currMode.getAction(), currMode.getScale());
			// integrate the diff. eq.
			Matrix currSolMatrix = MyRungeKutta.fourthOrderTraj(csm, currTf, cs_f, currT0, stepSize);
			
			// adjust indices and times for next final conditions
			
		}
		
		return null;
	}
	
	private final FastList<double[]> armijoStep(int puppdetIdx, double[] gammas, final double[] dJdt, final double[] dJdalpha, double Jcurr){
		
		double[] currTaus = this.playerTaus.get(puppdetIdx);
		double[] currAlphas = this.playerAlphas.get(puppdetIdx);
		
		return null;
		
	}
	
	public FastList<Matrix> getTrajSolutions() {
		return trajSolutions;
	}

	public Matrix getSolutionOfPlayer(int i){
		return this.trajSolutions.get(i);
	}
	
	public FastList<double[]> getPlayerTaus() {
		return playerTaus;
	}

	public FastList<double[]> getPlayerAlphas() {
		return playerAlphas;
	}

	public FastMap<String, ModeString> getPlay() {
		return play;
	}

	public double getStepSize() {
		return stepSize;
	}


}
