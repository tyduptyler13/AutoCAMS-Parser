package com.myuplay.AutoCAMS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;

/**
 * Handles a single file.
 * @author Tyler
 *
 */
public class FileReader extends Task<Boolean>{

	private List<File> files = new ArrayList<File>();
	private List<AutoCAMSData> data = new ArrayList<AutoCAMSData>();

	private final File output;
	
	public FileReader(File top, File output){

		this.output = output;
		
		if (top.isDirectory()){
			Console.log("Searching " + top.getName() + " for csv files.");
			getFiles(top);
			Console.log("Found " + files.size() + " files.");
		} else {
			if (top.exists() && top.getName().endsWith(".csv")){
				files.add(top);
			} else {
				Console.warn("Invalid file: " + top.getPath());
				Console.log("All files must be csv files! No parsing will be done.");
			}
		}

	}

	private void getFiles(File top){

		for (File f : top.listFiles()){

			if (f.isDirectory()){
				getFiles(f);
			} else {
				if (f.exists() && f.getName().endsWith(".csv")){
					files.add(f);
				}
			}

		}

	}

	public void parse(){

		for (int i = 0; i < files.size(); ++i){
			
			File f = files.get(i);

			if (f.canRead()){
				try {

					BufferedReader reader = Files.newBufferedReader(f.toPath(), Charset.forName("US-ASCII"));
					String line = null;

					String name = f.getName();
					name = name.replace(".csv", "");
					String[] tmp = name.split("-");

					//Parse name.
					short part = Short.parseShort(tmp[0]);
					String cond = tmp[1].replaceAll("\\d+", "");
					short version = Short.parseShort(tmp[1].replaceAll("[a-zA-Z]+", ""));
					String block = tmp[2];
					AutoCAMSData d = new AutoCAMSData(part, cond, version, block);

					//Parse lines
					while ((line = reader.readLine()) != null){
						if (line.startsWith("#")) continue;

						String[] parts = line.split(",");

						d.injestLine(parts);

					}

					//Add data object to output. Failed parses won't make it here.
					data.add(d);

				} catch (IOException e) {
					Console.warn("An error occured in file: " + f.getPath() + "! File skipped.");
					Console.error(e.getMessage());
					e.printStackTrace();
				} catch (NumberFormatException | ParseException e){
					Console.warn("A parsing error occured parsing " + f.getPath() + ". The file must be skipped.");
					Console.error(e.getMessage());
				}
			} else {
				Console.warn("Can't read: " + f.getPath() + "! Skipping.");
			}

			updateProgress(i, files.size());
			updateMessage("Parsing file " + i + "/" + files.size());

		}

	}
	
	public Boolean call(){
		
		try {
			parse();
			updateProgress(0,1);
			updateMessage("Saving...");
			save(output);
			updateProgress(1,1);
			updateMessage("Finished");
			return new Boolean(true);
		} catch (Exception e){
			Console.error("An error occurred:", e.getMessage());
			updateMessage("Failed");
			failed();
			return new Boolean(false);
		}
		
	}

	public void save(File out){

		if (out.exists() == out.canWrite()){ //If it exists. We need to check we can write to it.

			try {
				BufferedWriter bw = Files.newBufferedWriter(out.toPath(), Charset.forName("US-ASCII"));

				bw.write(AutoCAMSData.printHeader() + "\r\n");

				for (AutoCAMSData d : data){

					bw.write(d.printData());

				}

				bw.close();

				Console.log("File saved successfully.");

			} catch (IOException e) {
				Console.log("Could not save to file. Data dumped.");
				Console.error(e.getMessage());
				e.printStackTrace();
			}

		} else {

			Console.warn("Could not save to file! Check that it is not in use by the system and you have permission to access it.");

		}

	}

}
