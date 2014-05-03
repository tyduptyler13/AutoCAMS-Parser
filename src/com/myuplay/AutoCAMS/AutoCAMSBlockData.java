package com.myuplay.AutoCAMS;

public class AutoCAMSBlockData extends Parser implements CSV{

	public long time = -1; //Time

	public short clicks = -1; //Clicks
	public float cpm = -1; //Clicks per minute

	public short diagClick = -1; //Diagnosed clicks
	public float dcpm = -1; //Diagnosed clicks per minute

	public short loggedCO2 = -1; //Logged CO2
	public short confCon = -1; //Confirmed connections
	public short conChk = -1; //Connection checks
	public short te2 = -1; //2nd Task errors
	public short failures = -1;
	public short nr = -1; //Repairs
	public short ncr = -1; //Correct repairs

	public String print() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injestLine(String[] parts) {
		// TODO Auto-generated method stub

	}

}
