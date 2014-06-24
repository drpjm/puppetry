package edu.gatech.grits.puppetctrl.comm.bioloid;

/**
 * Static class meant to manage hardware details of the Bioloid motors (Dynamixels).
 * DynamixelParameters.java
 * @author pmartin
 * May 16, 2007
 */
public class DynamixelParams {

	public final static short MAX_ANGLE = (short)0x3FF;
	public final static short MIN_ANGLE = (short)0x0;
	public final static short MAX_ANGULAR_SPEED = (short)0x3ff;
	public final static short MIN_ANGULAR_SPEED = (short)0x1;
	
	private final static float DEG_PER_STEP = 0.293255f;
	private final static float RAD_PER_STEP = 0.005118f;
	
	private final static float RadPS_TO_RPM = (float)(60 / (2*Math.PI));
	private final static float DegPS_TO_RPM = (float)(60f / 360f);
	private final static float RPM_PER_STEP = 0.111437f;
    public final static float  Sprock = (float)0.029972f/2f;
//    public final static float  RPM_PER_STEP_EXP = (float)68f/1023f;
    public final static float  RPM_PER_STEP_EXP = (float)68f/(1023f-85f);
    
	/**
	 * Approximate a raw float degree value between 0 and 1023 based on
	 * the Dynamixel's conversion factor.
	 * @param f
	 * @return
	 */
	public final static short approxDegree(float f){
		//apply conversion factor
		float conv = f * (1 / DEG_PER_STEP);
		short ret = (short)Math.floor(conv);
		return ret;
	}
	
	/**
	 * Approximate a raw float radian value between 0 and 1023 based on
	 * the Dynamixel's conversion factor.
	 * @param f
	 * @return
	 */
	public final static short approxRadian(float f){
		//apply conversion factor
		float conv = f * (1 / RAD_PER_STEP);
		short ret = (short)Math.floor(conv);
		return ret;
	}
	
	/**
	 * Approximates a given rotational speed in radians per second as a discrete value.
	 * @param rps
	 * @return
	 */
	public final static short approxRadPerSec(float rps){
		float conv = (RadPS_TO_RPM * rps) / RPM_PER_STEP;
		short ret = (short)Math.ceil(conv);
		return ret;
	}
	/**
	 * Approximates a given rotational speed in degrees per second as a discrete value.
	 * @param dps
	 * @return
	 */
	public final static short approxDegPerSec(float dps){
		float conv = (DegPS_TO_RPM * dps) / RPM_PER_STEP;
		short ret = (short)Math.ceil(conv);
		return ret;
	}
	
	/**
	 * Approximates a given rpm in discrete value from 0 to 1023.
	 * @param rpm
	 * @return
	 */
	public final static short approxRPM(float rpm){
		float conv = rpm / RPM_PER_STEP;
		short ret = (short)Math.ceil(conv);
		return ret;
	}
	/**
	 * Approximates a given position from discrete value from 0 to 1023.
	 * to degrees/position   @param rpm
	 * @return
	 */
	public final static float apprxHex2Deg(int f){
		//apply conversion factor
		//float conv = (float) f * (1 / DEG_PER_STEP);
		float conv = (float) f * (DEG_PER_STEP);		
		float ret = conv;
		return ret;
	}
	/**
	 * Approximates a given rotational speed in degrees per second as a discrete value.
	 * @param dps
	 * @return
	 */
	public final static float apprxHex2DegPS(int vel){
		//float conv = (DegPS_TO_RPM * dps) / RPM_PER_STEP;
		//short ret = (short)Math.ceil(conv);
		//return ret;
		//float vel = (float)velL + (float)velH + (float)1024.0; 
		float conv = (vel * RPM_PER_STEP) / DegPS_TO_RPM; //original
		//float conv = (vel / RPM_PER_STEP) * DegPS_TO_RPM;
		return conv;
	}
	public final static float apprxRPM2MetPS(float vel){
		// takes RPM and converts to Deg/s
		float conv = (vel) / DegPS_TO_RPM;
		//convert to rad/s then into m/s w/ radius = 0.029972/2
		conv=(float) (conv*(Math.PI/180)*Sprock);
		return conv;
	}
	public final static float scaleu (double u){
		//signal comes in as m/s change to deg/s then to rpm then to hex
		//2047 hex is about 1064dps  
	double un = (u/Sprock)*180/Math.PI; //turn m/s into deg/s
	double ur = un*DegPS_TO_RPM; // turn deg/s to rpm
	//System.out.println("u:"+ur);
	double us;
	float uf;
	// RPM_PER_STEP doesn t work cause 1023 ~= 114rpm!!  scalle this properly!!!
	if (u < 0.0){
	   us=(Math.abs(ur)/RPM_PER_STEP_EXP+85) +1023; //1024?? use to be 700
	   // saturations limit 68rpm(max rpm)
	    if (us>2047) {us=2047;} 
	  }else {
		     us = ur/RPM_PER_STEP_EXP+85;
            // saturation limit 68rpm(max rpm)		    
		     if (us>1023){us = 1023;}
	        }
	 uf = (float) us;
	return uf;
	}
}
