package edu.gatech.grits.puppetctrl.comm.bioloid;

import javolution.util.FastList;

/**
 * Class which packages Bioloid relevant data into a Java object.
 * @author pmartin
 *
 */
public class BioloidData {

	BioloidCommand command;
	public FastList<Integer> motorIds;
	public FastList<Float> poss;
	public FastList<Float> vels;
	

	public BioloidData(){
		command = BioloidCommand.INVALID;
		motorIds = new FastList<Integer>();
		poss = new FastList<Float>();
		vels = new FastList<Float>();
	}

	public BioloidCommand getCommand() {
		return command;
	}

	public void setCommand(BioloidCommand command) {
		this.command = command;
	}

	public FastList<Integer> getMotorIds() {
		return motorIds;
	}

	public void setMotorIds(FastList<Integer> motorIds) {
		this.motorIds = motorIds;
	}
    public void setVels(FastList<Float> vels){
    	this.vels = vels;
    }
    public FastList<Float> getVels(){
    	return vels;
    }
    public void setPoss(FastList<Float> poss){
    	this.poss = poss;
    }
    public FastList<Float> getPoss(){
    	return poss;
    }
	@Override
	public String toString() {
		String tmp = "Command: ";
		
		tmp += command;
		tmp += "\nMotors: ";
		if(motorIds.isEmpty()){
			tmp += "none";
		}
		else{
			for(Integer i : motorIds){
				tmp += i.toString() + " ";
			}
		}
		
		return tmp;
	}
	
	
}
