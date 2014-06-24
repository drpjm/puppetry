package edu.gatech.grits.puppetctrl.mdl.util;

import java.io.Serializable;

import javolution.util.FastList;

/**
 * Class which encapsulates a set of mode strings for one puppet agent.
 * 
 * @author pmartin
 *
 */
public class ModeString implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3811512770798037477L;
	private FastList<Mode> modes;
	
	public ModeString(){
		modes = new FastList<Mode>();
	}
	
	public final void addNewMode(Mode newMode){
		modes.add(newMode);
	}

	public final int getLength(){
		return modes.size();
	}
	
	public final Mode getModeAt(int index){
		return modes.get(index);
	}
	
	public FastList<Mode> getModes() {
		return modes;
	}

	@Override
	public String toString() {
		return modes.toString();
	}
}
