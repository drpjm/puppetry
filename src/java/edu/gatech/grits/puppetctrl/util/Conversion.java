package edu.gatech.grits.puppetctrl.util;

public enum Conversion {

	DEG("Degrees"),
	RAD("Radians");
	
	private String conversion;
	
	private Conversion(String s){
		conversion = s;
	}

	public String getConversion() {
		return conversion;
	}
	
}
