package edu.gatech.grits.puppetctrl.opt;

import javolution.util.FastList;
import javolution.util.FastMap;

public class Stage{

	// dimensions of stage -- "units"
	private int numWide;
	private int numHigh;
	
	private static final double LENGTH_PER_UNIT = 100; // CM!
	
	private final FastMap<String,double[]> stageMap;
	private final FastList<String> regions;
	
	public Stage(int w, int h){
		numWide = w;
		numHigh = h;
		stageMap = new FastMap<String,double[]>();
		
		int numRegions = w*h;
		regions = new FastList<String>(numRegions);
		for(int i = 0; i < numRegions; i++){
			regions.add("region" + (i+1));
		}
		
		
		double center = (Stage.LENGTH_PER_UNIT / 100) / (double)2; // convert to meters
		int k = 0;
		for(int i = 0; i < numWide; i++){
			for(int j = 0; j < numHigh; j++){
				double x = center + (Stage.LENGTH_PER_UNIT / 100)*j;
				double y = center + (Stage.LENGTH_PER_UNIT / 100)*i;
				System.out.println(regions.get(k) + ": " + x + "," + y);
				stageMap.put(regions.get(k), new double[]{x,y});
				k++;
			}
		}
		
	}
	
	public double[] getRegionCenter(String regionName){
		
		if(stageMap.containsKey(regionName)){
			return stageMap.get(regionName);
		}
		else{
			System.err.println("Error: not a valid region!");
			return null;
		}
		
	}
	
	public static void main(String[] args){
		Stage s = new Stage(2,2);
		s.getRegionCenter("region5");
		
	}
}
