package com.myuplay.AutoCAMS;

import java.text.ParseException;
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
	private boolean inBlock = false;

	private List<AutoCAMSBlockData> blocks = new ArrayList<AutoCAMSBlockData>();

	public AutoCAMSData(short participant, String condition, short version, short block){

		this.participant = participant;
		this.condition = condition;
		this.version = version;
		this.block = block;

	}

	public String printData() {

		String out = "";

		String prelude = participant + "," + condition + version + "," + block;

		for (AutoCAMSBlockData block : blocks){

			out += prelude + "," + block.printData() + "\r\n";

		}

		return out;

	}

	public static String printHeader() {
		return "Participant,Condition Version,Block," + AutoCAMSBlockData.printHeader();
	}

	@Override
	public void injestLine(String[] parts) throws ParseException {

		if (parts.length != 14){
			throw new ParseException("Unexpected number of columns. (Expects 14)", parts.length);
		}

		if (inBlock){
			//Look for end of block
			if (parts[10].equals("phase changed") && parts[11].equals("GREEN")){
				inBlock = false;
				//End of block
			}
			//Get last block.
			blocks.get(blocks.size() - 1).injestLine(parts);

		} else {
			//Looking for beginning of block.
			if (parts[9].equals("detector")){
				inBlock = true;
				AutoCAMSBlockData b = new AutoCAMSBlockData();
				b.injestLine(parts);
				blocks.add(b);
			}
		}

	}

}
