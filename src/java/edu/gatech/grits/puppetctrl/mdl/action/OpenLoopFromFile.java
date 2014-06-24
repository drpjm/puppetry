package edu.gatech.grits.puppetctrl.mdl.action;

import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidMotorControl;
import edu.gatech.grits.puppetctrl.comm.bioloid.DynamixelParams;
import edu.gatech.grits.puppetctrl.comm.bioloid.MotionPacket;
import edu.gatech.grits.puppetctrl.mdl.util.ActionAdapter;
import edu.gatech.grits.puppetctrl.model.PuppetMotorMap;
import edu.gatech.grits.puppetctrl.util.Control;

import java.io.*;
import java.util.Scanner;
import java.util.Vector;

public class OpenLoopFromFile extends ActionAdapter {

    private String name;

    // fastest walk is 40 deg/sec
    private final float MAX_SPEED = 40.0f;

    private final float MAX_LEFT_HEIGHT = 130.0f;
    private final float MIN_LEFT_HEIGHT = 60.0f;

    private final float MAX_RIGHT_HEIGHT = 170.0f;
    private final float MIN_RIGHT_HEIGHT = 240.0f;

    //private final float delT = 100e-3f;
    private final float delT = 1e-3f;
    //private final float delT = 100;
    
    private PuppetJointTrajectory trajectory = null;
    
    
    
    public OpenLoopFromFile()
    {
    	System.out.println("OpenLoopFromFile ActionAdapter created.");
    	name = "openloopfromfile";
    	
    	trajectory =
			new PuppetJointTrajectory("C:\\Documents and Settings\\LabUser\\My Documents\\grits_puppets\\puppets\\data\\trajectories\\Phis_out.txt");
    }

    @Override
    public String toString() {
    	return name;
    }

    public Control actOn(float scaleParam, long currTime) {
    	
	MotionPacket nextMotion = new MotionPacket();

	BioloidMotorControl ctrl1 = new BioloidMotorControl(
		PuppetMotorMap.RIGHTARMLIFT);
	BioloidMotorControl ctrl2 = new BioloidMotorControl(
			PuppetMotorMap.RIGHTARMROTATE);
	BioloidMotorControl ctrl3 = new BioloidMotorControl(
			PuppetMotorMap.LEFTARMLIFT);
	BioloidMotorControl ctrl4 = new BioloidMotorControl(
				PuppetMotorMap.LEFTARMROTATE);
	BioloidMotorControl ctrl5 = new BioloidMotorControl(
			PuppetMotorMap.RIGHTLEGLIFT);
	BioloidMotorControl ctrl6 = new BioloidMotorControl(
			PuppetMotorMap.LEFTLEGLIFT);
	
	
	float[] angles1 = trajectory.getJointAngles(currTime-1);
	float[] angles2 = trajectory.getJointAngles(currTime);
	
	System.out.print("currTime="+currTime+"\n");
	System.out.print("  angles1: ");
	for(int j=0; j < angles1.length; ++j)
	{
		System.out.print(angles1[j]+"  ");
	}
	System.out.print("\n  angles2: ");
	for(int j=0; j < angles1.length; ++j)
	{
		System.out.print(angles2[j]+"  ");
	}
	System.out.print("\n  speeds=");
	for(int j=0; j < angles1.length; ++j)
	{
		System.out.print(Math.abs((angles2[j]-angles1[j])/delT)+"  ");
	}
	System.out.print("\n\n");
	
	
    // assemble Bioloid commands
	ctrl1.setPosition(DynamixelParams.approxDegree(angles2[0]));
	ctrl2.setPosition(DynamixelParams.approxDegree(angles2[1]));
	ctrl3.setPosition(DynamixelParams.approxDegree(angles2[2]));
	ctrl4.setPosition(DynamixelParams.approxDegree(angles2[3]));
	ctrl5.setPosition(DynamixelParams.approxDegree(angles2[4]));
	ctrl6.setPosition(DynamixelParams.approxDegree(angles2[5]));

	float vel;
	
	vel = Math.abs((angles2[0]-angles1[0])/delT);
	ctrl1.setVelocity(DynamixelParams.approxDegPerSec(vel));
	if(vel > 1e-9)
	{
		nextMotion.addBioloidControl(ctrl1);
	}
	
	vel = Math.abs((angles2[1]-angles1[1])/delT);
	ctrl2.setVelocity(DynamixelParams.approxDegPerSec(vel));
	if(vel > 1e-9)
	{
		nextMotion.addBioloidControl(ctrl2);
	}
	
	vel = Math.abs((angles2[2]-angles1[2])/delT);
	ctrl3.setVelocity(DynamixelParams.approxDegPerSec(vel));
	if(vel > 1e-9)
	{
		nextMotion.addBioloidControl(ctrl3);
	}
	
	vel = Math.abs((angles2[3]-angles1[3])/delT);
	ctrl4.setVelocity(DynamixelParams.approxDegPerSec(vel));
	if(vel > 1e-9)
	{
		nextMotion.addBioloidControl(ctrl4);
	}
	
	vel = Math.abs((angles2[4]-angles1[4])/delT);
	ctrl5.setVelocity(DynamixelParams.approxDegPerSec(vel));
	if(vel > 1e-9)
	{
		nextMotion.addBioloidControl(ctrl5);
	}
	
	vel = Math.abs((angles2[5]-angles1[5])/delT);
	ctrl6.setVelocity(DynamixelParams.approxDegPerSec(vel));
	if(vel > 1e-9)
	{
		nextMotion.addBioloidControl(ctrl6);
	}
	    
	return new Control(nextMotion);
    }

	public double[] computeModel(double t, double[] x, double alpha,
			boolean isDeriv, double timeDyn) {
		// TODO Auto-generated method stub
		return null;
	}

}
