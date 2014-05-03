package com.myuplay.AutoCAMS;

import java.util.ArrayList;
import java.util.List;

public class Console {

	private static final List<ConsoleOutput> outputs = new ArrayList<ConsoleOutput>();

	public static void register(ConsoleOutput c){
		outputs.add(c);
	}

	public static void unregister(ConsoleOutput c){
		outputs.remove(c);
	}

	public static void log(String...args){
		print("log", args);
	}

	public static void error(String...args){
		print("ERROR", args);
	}

	public static void debug(String...args){
		print("Debug", args);
	}

	private static synchronized void print(String channel, String[] args){
		for (String arg : args){

			String out = "[" + channel + "] " + arg;

			for (ConsoleOutput c : outputs){
				c.print(out);
			}

			System.out.println(out);

		}
	}

}
