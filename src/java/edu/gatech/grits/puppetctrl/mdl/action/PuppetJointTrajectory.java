package edu.gatech.grits.puppetctrl.mdl.action;

import java.lang.String;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
//import java.util.Iterator;

public class PuppetJointTrajectory
{
	private static final int N = 6;
	//private static final float delT = 100e-3;
	private List<float[]> samples = null;

	public PuppetJointTrajectory(String filename)
	{
		
		System.out.print("Attempting to read '" + filename + "'\n");
		
		Scanner s = null;
        try
        {
            s = new Scanner(new BufferedReader(new FileReader(filename)));
            System.out.print("File seems to be accessible.  Reading samples:\n");
            samples = new ArrayList<float[]>();

            int i = 0;
            int t = 0;
            float[] thisSample = new float[N];
            while (s.hasNext())
            {
            	if(s.hasNextDouble())
            	{
            		thisSample[i] = (float)((180/Math.PI)*s.nextDouble());
            		//thisSample[i] = 0.0f;
            		//thisSample[i] = (float)(30.0*Math.sin(2.0*Math.PI*((double)t)/30.0));
            		System.out.print(thisSample[i]);
            		
            		// TODO: Replace this horrid kludge.
            		switch(i)
            		{
            		case 0: // Right arm lift
            			thisSample[i] = Math.min(Math.max(240.0f - thisSample[i], 60.0f), 253.0f);
            			break;
            		case 1: // Right arm rotate
            			thisSample[i] = Math.min(Math.max(thisSample[i] + 150.0f, 150.0f), 240.0f);
            			break;
            		case 2: // Left arm lift
            			thisSample[i] = Math.min(Math.max(thisSample[i] + 63.0f, 48.0f), 240.0f);
            			break;
            		case 3: // Left arm rotate
            			thisSample[i] = Math.min(Math.max(thisSample[i] + 150.0f, 55.0f), 150.0f);
            			break;
            		case 4: // Right leg lift
            			thisSample[i] = Math.min(Math.max(242.0f - 8.0f*thisSample[i], 47.0f), 250.0f);
            			break;
            		case 5: // Left leg lift
            			thisSample[i] = Math.min(Math.max(8.0f*thisSample[i] + 62.0f, 51.0f), 250.0f);
            			break;
            		}
            		
            		++i;
            		if(i == N)
            		{
            			System.out.print("\n");
            			samples.add(thisSample);
            			thisSample = new float[N];
            			i = 0;
            			++t;
            		}
            		else
            		{
            			System.out.print(", ");
            		}
            	}
            	else
            	{
            		System.out.print("'" + s.next() + "' is not a double.\n");
            	}
            }
            if(i != 0)
            {
            	System.out.println("Warning: Did not read in a multiple of N="+N+" floats.");
            }
            System.out.print("Done reading.\n\n");
        }
        catch(IOException e)
        {
        	System.out.println("Can't read file '"+filename+"': "+e.getMessage());
        }
        finally
        {
            if (s != null)
            {
                s.close();
            }
        }		
	}
	
	/*
	public float[] getJointAngles(float t)
	{
		float[] out = new float[N];
		for(int i=0; i < out.length; ++i)
		{
			out[i] = 0.0f;
		}
		
		out[0] = (float)(20.0*Math.sin(2.0*Math.PI*((double)t)/2.0) +
			(145.0+90.0)/2.0);
		
		return out;
		
	}
	*/
	
	///*
	public float[] getJointAngles(long t)
	{	
		if(t < 0.0f)
		{
			//System.out.print("t=0 (SPECIAL CASE 1)\n");
			return samples.get(0);
		}
		
		if(t >= (float)(samples.size()-1)*100.0f)
		{
			//System.out.print("t="+t+" (SPECIAL CASE 2)\n");
			return samples.get(samples.size()-1);
		}
		
		//float i_m = t/delT;
		
		// index (100s of ms)/(100 ms);
		float i_m = ((float)t)/100.0f;
		
		int i_lo = (int)Math.floor(i_m);
		int i_hi = i_lo+1;
		
		float alpha = i_m - (float)i_lo;
		
		float[] ret = interpolateVectors(samples.get(i_lo), samples.get(i_hi), alpha);
		
		//System.out.print("t="+t+"  i_lo="+i_lo+"  i_hi="+i_hi+"  alpha="+alpha+"\n");
		
		//System.out.print("t="+t+"  i_lo="+i_lo+"  i_hi="+i_hi+"  alpha="+alpha+".  Angles: ");
		//for(int i=0; i < ret.length; ++i)
		//{
		//	System.out.print(ret[i]+"   ");
		//}
		//System.out.print("\n");
		
		return ret;
				
	}
	//*/
	
	//private float[] clampJointAnglesInRange(float[] angles)
	//{
	//	
	//}
	
	private float[] interpolateVectors(float[] v1, float[] v2, float alpha)
	{
		float[] out = new float[N];
		
		for(int i=0; i < N; ++i)
		{
			out[i] = (1-alpha)*v1[i] + alpha*v2[i];
		}
		
		return out;
	}
}
