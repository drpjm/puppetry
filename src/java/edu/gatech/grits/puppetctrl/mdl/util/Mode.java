package edu.gatech.grits.puppetctrl.mdl.util;

import java.io.Serializable;


/**
 * A class that contains the abstraction of an MDL mode.
 * @author pmartin
 *
 */
public class Mode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8784111992985007776L;
	private float timeLength;
	private String region;
	private float scale;
	private ActionAdapter action;
	
	public Mode(){	
	}
	
	public Mode(float timeLength, String region, float scaleFactor) {
		super();
		this.timeLength = timeLength;
		this.region = region;
		this.scale = scaleFactor;
	}

	public Mode(float timeLength, String region, float scale, ActionAdapter action) {
		super();
		this.timeLength = timeLength;
		this.region = region;
		this.scale = scale;
		this.action = action;
	}

	public void setAction(ActionAdapter action) {
		this.action = action;
	}

	public ActionAdapter getAction() {
		return action;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getTimeLength() {
		return timeLength;
	}

	public void setTimeLength(int timeLength) {
		this.timeLength = timeLength;
	}

	@Override
	public String toString() {
		String str = "Mode: (";
		str += this.timeLength + ",";
		str += this.region + ",";
		str += this.action + "(";
		str += this.scale + ")";
		str+= ")";
		
		return str;
	}

	
}
