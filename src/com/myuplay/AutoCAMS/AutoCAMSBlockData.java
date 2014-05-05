package com.myuplay.AutoCAMS;

public class AutoCAMSBlockData extends Parser implements CSV{

	private final short failure;

	private short time = -1; //Time

	private short diag = 0; //Diagnosed clicks
	private short cdiag = 0; //Correct diagnosed clicks.

	private short rep = 0; //Repairs
	private short crep = 0; //Correct repairs
	private short firstr = -1; //First repair.
	private short tcr = -1; //Time to correct repair

	private short mgmt = 0; //Management
	private short cmgmt = 0; //Correct management

	//Hidden and not reported data.
	private short start = -1;

	private String cond;

	public AutoCAMSBlockData(short failure){
		this.failure = failure;
	}

	public String printData() {
		return failure + "," + time + "," + diag + "," + cdiag + "," + rep + "," + crep + ","
				+ firstr + "," + tcr + "," + mgmt + "," + cmgmt;
	}

	public static String printHeader() {
		return "failure,block length,# diag,#correct diag,#repairs,#correct repairs" + 
				",RT first repair, RT correct repair,#management clicks,#correct management";
	}

	@Override
	public void injestLine(String[] parts) {

		if (start == -1){ //First entry.
			start = Short.parseShort(parts[0]);
			cond = parts[10]; //Condition.
		}

		//Column K = repair
		if (parts[10].contains("repair")){

			rep++; //Count up repairs.

			//First repair
			if (firstr == -1){
				firstr = (short) (Short.parseShort(parts[0]) - start);
			}

			//Correct repair
			if (parts[10].contains(cond)){
				tcr = (short) (Short.parseShort(parts[0]) - start);
				crep++; // Number of correct repairs.
			}

		}

		//Column k = end
		//if (parts[10].equals("phase changed") && parts[11].equals("GREEN")){
		//Commented out so that it will always equal the last time available
		time = (short) (Short.parseShort(parts[0]) - start);
		//}

		//Diagnosis/Management steps

		if (parts[10].matches("(ox|ni)_flow: .*|(oxygen|pressure)_manual: .*")){
			mgmt++;
		}

		if (cond.contains("OXYGEN")){//Oxygen failure

			//Diagnostics
			if (parts[9].equals("graphic_monitor")){
				if (parts[10].matches("(ox|ni)_open")){
					diag++;
					cdiag++;
				}
			} else if (parts[10].contains("open: ")){
				if (parts[9].matches("possible_flow|ox_second|ox_tank_display")){
					diag++;
					cdiag++;
				} else if (parts[9].matches("ni_second|ni_tank_display|mixer")){
					diag++;
					//Incorrect
				}
			}

			//Management
			if (parts[10].matches("ox_flow: .*|oxygen_manual: .*")){
				cmgmt++;
			}

		} else if (cond.contains("NITROGEN")){ //NITRO FAIL

			//Diagnostics
			if (parts[9].equals("graphic_monitor")){

				if (parts[10].matches("(ox|ni)_open")){
					diag++;
					cdiag++;
				}
			} else if (parts[10].contains("open: ")){
				if (parts[9].matches("possible_flow|ni_(second|tank_display)")){
					diag++;
					cdiag++;
				} else if (parts[9].matches("ox_(second|tank_display)|mixer")) {
					diag++;
					//Incorrect
				}
			}

			if (parts[10].matches("ni_flow: .*|pressure_manual: .*")){
				cmgmt++;
			}

		} else if (cond.contains("MIXER")){ //MIX FAIL

			//Diagnosticis
			if (parts[9].equals("graphic_monitor") && parts[10].matches("(ni|ox)_open")){
				diag++;
				cdiag++;
			} else if (parts[9].matches("possible_flow|(ox|ni)_(second|tank_display)|mixer") &&
					parts[10].contains("open: ")){
				diag++;
				cdiag++;
			}

			if (parts[10].matches("(ox|ni)_flow: .*|(oxygen|pressure)_manual: .*")){
				cmgmt++;
			}

		}

	}

}
