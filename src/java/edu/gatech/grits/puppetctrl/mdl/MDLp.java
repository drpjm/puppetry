package edu.gatech.grits.puppetctrl.mdl;

import java.util.*;

import javolution.util.*;

import edu.gatech.grits.puppetctrl.mdl.analysis.*;
import edu.gatech.grits.puppetctrl.mdl.node.*;
import edu.gatech.grits.puppetctrl.mdl.util.*;

/**
 * This class performs the compilation of an MDLp script file. It outputs a Play object to be used
 * by the Puppet Control application.
 * @author pmartin
 *
 */
public class MDLp extends DepthFirstAdapter {

	private LinkedList<String> playerNames;
//	private FastMap<String, FastList<Mode>> playerModeStrings;
	private FastMap<String, ModeString> playerModeStrings;
	private int numberOfPlayers;
	
	private final String ACTION_LOCATION = "edu.gatech.grits.puppetctrl.mdl.action.";
	
	
	public MDLp() {
		playerNames = new LinkedList<String>();
		playerModeStrings = new FastMap<String, ModeString>();
	}

	
	
	@Override
	public void outAModelist(AModelist node) {
		// TODO Auto-generated method stub
		super.outAModelist(node);
	}



	@Override
	public void inAMode(AMode node) {
		String currAgentName = node.getAgentName().toString().trim();
		if(playerNames.contains(currAgentName)){
			
			//Assemble mode - time length
			float timeLength = Float.valueOf(node.getTimeLength().toString().trim());
			
			//Assemble mode - region
			String region = node.getRegionName().toString().trim();
			
			//Assemble mode - action lookup
			String actionName = node.getActionName().toString().trim();
			String className = ACTION_LOCATION + actionName;
			Object newAction = null;
			boolean actionFound = false;
			
			if(isValidAction(className)){
				System.out.println(actionName + " is valid.");
				//Create the action
				try {
					newAction = Class.forName(className).newInstance();
					actionFound = true;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			else{
				System.out.println("Error: " + actionName + " is NOT valid.");
			}
			
			if(actionFound){
				
				//Assemble mode - scaling frequency
				float scaleFreq = Float.valueOf(node.getScaling().toString().trim());

				//Create the mode
				Mode currMode = new Mode(timeLength, region, scaleFreq, (ActionAdapter)newAction);
				
				if(!playerModeStrings.containsKey(currAgentName)){
					//initialize value to current mode
					ModeString modeList = new ModeString();
					modeList.addNewMode(currMode);
					playerModeStrings.put(currAgentName, modeList);

				}
				else{
					//add new mode to the list
					playerModeStrings.get(currAgentName).addNewMode(currMode);
				}
			}
		}
		else{
			System.out.println(currAgentName + " NOT found!");
		}
//		System.out.println("Current Mapping: \n" + playerModes);
	}

	@Override
	public void inAPlayers(APlayers node) {
		//iterate through the agent list and store names
		Iterator it = node.getAgentList().iterator();
		while(it.hasNext()){
			playerNames.add(it.next().toString().trim());
		}
		this.numberOfPlayers = playerNames.size();
		System.out.println(numberOfPlayers + " player(s) expected: " + playerNames);
	}

	@Override
	public void outAMdlp(AMdlp node) {
		// TODO Auto-generated method stub
		super.outAMdlp(node);
	}

	@Override
	public void outAMode(AMode node) {
		// TODO Auto-generated method stub
		super.outAMode(node);
	}
	
	private boolean isValidAction(String actionName){
		System.out.println("Lookup " + actionName);
        try {
            Class.forName(actionName);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
		
	}

	public FastMap<String, ModeString> getPlayerModeStrings() {
		return playerModeStrings;
	}

	public LinkedList<String> getPlayerNames() {
		return playerNames;
	}

}
