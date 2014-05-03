package com.myuplay.AutoCAMS;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private static Stage consoleWindow;
	private static final TextArea console = new TextArea();

	@Override
	public void start(Stage stage) throws Exception {

		BorderPane bp = new BorderPane();

		bp.setTop(createTitle());
		bp.setCenter(createInputs());

		Scene scene = new Scene(bp);

		stage.setScene(scene);
		stage.setTitle("AutoCAMS-Parser");
		stage.show();

		consoleWindow = new Stage();

		openConsole();

		consoleWindow.show();
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
			public void handle(WindowEvent arg0) {
				consoleWindow.close();
			}
		});
		
		stage.toFront();

	}

	private Pane createTitle(){

		HBox title = new HBox();
		title.setPadding(new Insets(15, 12, 15, 15));
		title.setStyle("-fx-background-color: #336699");

		Text t = new Text("AutoCAMS Parser");
		
		t.setFont(new Font(24));

		title.getChildren().add(t);

		return title;

	}

	private Pane createInputs(){

		VBox list = new VBox();

		//Create buttons and file intputs.

		HBox in = new HBox();

		//Input directory.
		TextField idir = new TextField();
		Button open = new Button("Input Dir");
		//TODO

		in.getChildren().addAll(idir, open);

		//Output entry.
		HBox out = new HBox();

		TextField ofile = new TextField();
		Button outfile = new Button("Output file");
		//TODO

		out.getChildren().addAll(ofile, outfile);

		//Run button
		HBox commands = new HBox();

		Button run = new Button("Run");
		//TODO

		commands.getChildren().add(run);



		//Add elements.
		list.getChildren().addAll(in, out, commands);

		return list;

	}

	public static void main(String[] args){

		Console.log("Preparing interfaces");
		Console.log("Preparing GUI");

		launch(Main.class, args);

	}

	public static void printToConsole(String out){
		console.appendText(out + "\r\n");
	}

	public static void openConsole(){

		console.setPrefHeight(300);
		console.setPrefWidth(500);
		console.setEditable(false);

		Console.register(new ConsoleOutput(){

			public void print(String out) {
				printToConsole(out);
			}

		});

		final Scene s = new Scene(console);
		consoleWindow.setScene(s);

		consoleWindow.setTitle("AutoCAMS-Console");

		Console.log("Loaded ConsoleGUI");

	}

}
