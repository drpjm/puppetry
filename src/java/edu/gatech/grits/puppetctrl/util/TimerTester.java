package edu.gatech.grits.puppetctrl.util;

import java.util.Timer;
import java.util.TimerTask;
import edu.gatech.grits.puppetctrl.util.TimerListener;
import edu.gatech.grits.puppetctrl.util.TimerEvent;
import edu.gatech.grits.puppetctrl.util.SimpleTask;


/**
 * This class provides a test environment for developing the new timing mechanism in the PuppetDriver.
 * @author pmartin
 *
 */
public class TimerTester implements TimerListener {

	private Timer mainTimer;
	
	private long lastTime;
	private long startTime;
	
	public TimerTester(){
		mainTimer = new Timer();
//		mainTimer.scheduleAtFixedRate(new TestTask("mode", this), 0, 1000);
//		mainTimer.scheduleAtFixedRate(new TestTask("ctrl", this), 100, 100);
		long total = 0;
		for(int i = 0; i < 10; i++){
			long currTimeLength = (long)Math.floor(2000*Math.random());
			System.out.println("time: " + (currTimeLength + total));
			mainTimer.schedule(new SimpleTask("tau_" + i, this), currTimeLength+total);
			total += currTimeLength;
		}
		startTime = System.currentTimeMillis();
		lastTime = startTime;
	}

	public void onTimer(TimerEvent te) {
		System.out.println(Long.toString(te.getTime() - lastTime));
		lastTime = System.currentTimeMillis();

	}
	

	public static void main(String[] args){
		TimerTester tt = new TimerTester();
	}

}
