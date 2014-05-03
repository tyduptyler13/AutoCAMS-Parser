package com.myuplay.AutoCAMS;

import java.util.ArrayList;
import java.util.List;

public class AutoCAMSData extends Parser implements CSV {
	/**
	 * Stores the original csv data.
	 */
	private short participant;
	private short block;
	private String condition;
	private short version;

	private List<AutoCAMSBlockData> blocks = new ArrayList<AutoCAMSBlockData>();
	
	public AutoCAMSData(short participant, String condition, short version, short block){

		this.participant = participant;
		this.condition = condition;
		this.version = version;
		this.block = block;

	}

	public String print() {
		String out = "";
		
		
		
		return null;
	}

	@Override
	public void injestLine(String[] parts) {
		// TODO Auto-generated method stub

	}

}
