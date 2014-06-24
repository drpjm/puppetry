package edu.gatech.grits.puppetctrl.util;

import java.util.*;

import javolution.util.*;
import edu.gatech.grits.puppetctrl.comm.bioloid.BioloidCommand;
import edu.gatech.grits.puppetctrl.comm.serial.SerialCommunicable;
import edu.gatech.grits.puppetctrl.gui.MdlDriverPanel;
import edu.gatech.grits.puppetctrl.gui.MyPanel;
import edu.gatech.grits.puppetctrl.gui.ObserverPacket;
import edu.gatech.grits.puppetctrl.mdl.util.*;

/**
 * A class for prototyping driving the puppet via an MDLp mode string. 
 * @author pmartin
 *
 */
public class PuppetDriver implements TimerListener {

	//java.util Timer class for driving MDLp string.
	private Timer mdlEngineTimer;
	private boolean isPlaySet;
	private boolean isPlayOver;
	
	//MDLp variables
	private ModeString currPlay;
	private Mode currMode;
	private int modeIndex;

	private boolean neverStarted = true;
	
	private final String MODE = "MODE";
	private final String CRTL = "CONTROL";
	
	private final long ctrlPeriod = 100;
	private long startTime;				//starting time when play started
	private long lastModeTime;
//	private long lastCtrlTime;
	private long totalModeTime;
	
	private MdlDriverPanel parentPanel;
	
	public PuppetDriver(MdlDriverPanel mdp){
		parentPanel = mdp;
		
		modeIndex = 0;
		currMode = new Mode();
		currPlay = new ModeString();
		isPlaySet = false;
		isPlayOver = false;
				
	}
	
	
	public void launch(){
		//create timer thread
		mdlEngineTimer = new Timer("MDLp Engine Timer");
		
		System.out.println(this.getClass().getCanonicalName()  + ": I just got launched!");
		if(isPlaySet){
			if(neverStarted){
				neverStarted = false;
			}
			isPlayOver = false;
			
			//Schedule timers
			//1. mode timers
			FastList<Mode> modes = currPlay.getModes();
			long total = 0;
			for(FastList.Node<Mode> n = modes.head(), end = modes.tail(); (n = n.getNext())!=end;){
				long delay = (long) Math.floor(n.getValue().getTimeLength()*1000);
//				mdlEngineTimer.schedule(new SimpleTask(this.MODE, this), delay+total);
				mdlEngineTimer.schedule(new SimpleTask(this.MODE, this), delay+total);
				total += delay;
			}
			//2. control timer
//			mdlEngineTimer.scheduleAtFixedRate(new SimpleTask(this.CRTL, this), 0, this.ctrlPeriod);
			mdlEngineTimer.scheduleAtFixedRate(new SimpleTask(this.CRTL, this), 0, this.ctrlPeriod);
			
			this.startTime = System.currentTimeMillis();
			this.lastModeTime = this.startTime;
//			this.lastCtrlTime = this.startTime;
			this.totalModeTime = 0;

			//get first mode
			currMode = currPlay.getModeAt(this.modeIndex);
			System.out.println(currMode.toString() + " running.");
			
		}
		else{
//			System.out.println("Error: No play has been set!");
		}

	}
	public void stopPlay(){
		if(!neverStarted){
			mdlEngineTimer.cancel();
			isPlaySet = false;
		}
	}
	
	public void onTimer(TimerEvent te) {
		String timerId = te.getTimerId();
		if(timerId.compareTo(this.MODE) == 0){
			
			//check to see if the play is over
//			System.out.println(Long.toString((te.getTime() - this.lastModeTime)));
			this.lastModeTime = System.currentTimeMillis();
			
			//manage index of current mode
			modeIndex++;

			if(modeIndex < currPlay.getLength()){
				//change total mode time elapsed
				this.totalModeTime += currMode.getTimeLength()*1000;
				//set next mode
				currMode = currPlay.getModeAt(modeIndex);
//				System.out.println(currMode.toString() + " running.");
				
			}
			else{
//				System.out.println("Play over!");
				isPlayOver = true;
				mdlEngineTimer.cancel();
			}
		}
		else if(timerId.compareTo(this.CRTL) == 0 && !isPlayOver){
			if(te.getCondition() == TimerEvent.TIMER_FIRED){

				long elapsedTime = System.currentTimeMillis() - (this.startTime + this.totalModeTime);
				
				//send control command
				ActionAdapter aa = currMode.getAction();
				float scaling = currMode.getScale();
//				System.out.println("Control: " + aa.actOn(scaling, elapsedTime));
				parentPanel.sendControl(aa.actOn(scaling, elapsedTime));
				
			}
			else if(te.getCondition() == TimerEvent.TIMER_DEAD){
			}
			
		}
	}

	public ModeString getCurrPlay() {
		return currPlay;
	}

	public void setCurrPlay(ModeString currPlay) {
		this.currPlay = currPlay;
		//initialize first node
		modeIndex = 0;
		isPlaySet = true;
	}

	public Mode getCurrMode() {
		return currMode;
	}

}
