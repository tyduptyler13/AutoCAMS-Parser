package com.myuplay.AutoCAMS;

public class AutoCAMSBlockData extends Parser implements CSV{

	private final short failure;

	private short time = -1; //Time

	private short idiag = 0; //Incorrect diagnosed clicks
	private short cdiag = 0; //Correct diagnosed clicks.
	private short gmonOx = 0;
	private short gmonNi = 0;
	private short pflow = 0;
	private short oxsec = 0;
	private short oxtank = 0;

	private short rep = 0; //Repairs
	private short crep = 0; //Correct repairs
	private short firstr = -1; //First repair.
	private short tcr = -1; //Time to correct repair

	private short mgmt = 0; //Management
	private short cmgmt = 0; //Correct management

	private short out = 0; //Pressure out of range.
	private short outME = 0; //Pressure out Management Error.
	private short nout = 0; //Invalid pressure out of range.

	private short co2 = 0; //Logged c02
	private short mco2 = 0; //missed c02

	private short con = 0; //Total connections
	private short lcon = 0; //Logged connections
	private short mcon = 0; //Missed connections

	//Hidden and not reported data.
	private short start = -1;

	private String cond;

	public AutoCAMSBlockData(short failure){
		this.failure = failure;
	}

	public String printData() {
		return failure + "," + time + "," + idiag + "," + cdiag + ","
				+ gmonOx + "," + gmonNi + "," + pflow + "," + oxsec + "," + oxtank + "," + rep
				+ "," + crep + "," + firstr + "," + tcr + "," + mgmt + "," + cmgmt + "," + out + "," + outME
				+ "," + nout + "," + co2 + "," + mco2 + "," + con + "," + lcon + "," + mcon;
	}

	public static String printHeader() {
		return "failure,block length,# incorrect diag,#correct diag,#graphic monitor ox,#graphic monitor ni," +
				"#possible flow,#* second,#* tank display,#repairs,#correct repairs" + 
				",RT first repair, RT correct repair,#management clicks,#correct management" + 
				",pressure out (sec),pressure out Management Error (sec),irrelevant pressure out (sec),logged co2,missed c02,total connections" +
				",logged connections,missed connections";
	}

	@Override
	public void injestLine(String[] parts) {

		if (start == -1){ //First entry.
			start = Short.parseShort(parts[0]);
			cond = parts[10]; //Condition.
		}

		//Column K = repair
		if (parts[10].contains("repair:")){

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

		if (parts[10].matches("(ox|ni)_flow:.*|(oxygen|pressure)_manual:.*")){
			mgmt++;
		}

		if (cond.contains("OXYGEN")){//Oxygen failure

			//Diagnostics
			if (parts[9].equals("graphic_monitor")){
				if (parts[10].matches("(ox|ni)_open")){
					cdiag++;

					if (parts[10].startsWith("ox")){
						gmonOx = 1;
					} else {
						gmonNi = 1;
					}

				}
			} else if (parts[10].contains("open:")){
				if (parts[9].matches("possible_flow|ox_(second|tank_display)")){
					cdiag++;

					if (parts[9].startsWith("p")){ //possible_flow
						pflow = 1;
					} else if (parts[9].endsWith("d")){ //ox_second
						oxsec = 1;
					} else { //tank_display
						oxtank = 1;
					}

				} else if (parts[9].matches("ni_(second|tank_display)|mixer")){
					idiag++;
				}
			}

			//Management
			if (parts[10].matches("(ox_flow|oxygen_manual):.*")){
				cmgmt++;
			}

			if (parts[10].equals("ox_out_of_setpoint")){
				out++;

				if (cond.contains("STUCK")){
					if (Float.parseFloat(parts[2]) < 19.6f){
						outME++;
					}
				} else {
					if (Float.parseFloat(parts[2]) > 20f){
						outME++;
					}
				}

			}

			if (parts[10].equals("pressure_out_of_setpoint")){
				nout++;
			}

		} else if (cond.contains("NITROGEN") || cond.contains("PRESSURE")){ //NITRO FAIL

			//Diagnostics
			if (parts[9].equals("graphic_monitor")){

				if (parts[10].matches("(ox|ni)_open")){
					cdiag++;

					if (parts[10].startsWith("ox")){
						gmonOx = 1;
					} else {
						gmonNi = 1;
					}

				}
			} else if (parts[10].contains("open:")){

				if (parts[9].matches("possible_flow|ni_(second|tank_display)")){
					cdiag++;

					if (parts[9].startsWith("p")){ //possible_flow
						pflow = 1;
					} else if (parts[9].endsWith("d")){ //ox_second
						oxsec = 1;
					} else { //tank_display
						oxtank = 1;
					}

				} else if (parts[9].matches("ox_(second|tank_display)|mixer")) {
					idiag++;
				}
			}

			//Managment sections
			if (parts[10].matches("(ni_flow|pressure_manual):.*")){
				cmgmt++;
			}

			if (parts[10].equals("pressure_out_of_setpoint")){
				out++;

				if (cond.contains("STUCK")){
					if (Float.parseFloat(parts[2]) < 0.99f){
						outME++;
					}
				} else {
					if (Float.parseFloat(parts[2]) > 1.025){
						outME++;
					}
				}

			}

			if (parts[10].equals("ox_out_of_setpoint")){
				nout++;
			}

		} else if (cond.contains("MIXER")){ //MIX FAIL

			//Diagnosticis
			if (parts[9].equals("graphic_monitor") && parts[10].matches("(ni|ox)_open")){
				cdiag++;
			} else if (parts[9].matches("possible_flow|(ox|ni)_(second|tank_display)|mixer") &&
					parts[10].contains("open:")){
				cdiag++;
			}

			//Management
			if (parts[10].matches("(ox|ni)_flow:.*|(oxygen|pressure)_manual:.*")){
				cmgmt++;
			}

			if (parts[10].matches("(ox|pressure)_out_of_setpoint")){
				out++;
			}

		}

		//Logged co2.
		if (parts[9].equals("logging_task")){

			if (parts[10].equals("missed") || parts[10].equals("0")){
				mco2++;
			} else {
				try {
					float val = Float.parseFloat(parts[10]);
					if (val > 0){
						co2++;
					} else {
						mco2++;
					}
				} catch (NumberFormatException e) {
					mco2++; //Means there is text.
					Console.warn("Recieved " + parts[10] + " in column k for logging_task, interpreting as a miss");
				}
			}

		}

		if (parts[9].equals("connection_check")){

			if (parts[10].equals("icon_appears")){
				con++;
			} else if (parts[10].equals("confirmed")){
				lcon++;
			} else if (parts[10].equals("icon_closed")){
				mcon++;
			}

		}

	}

}
