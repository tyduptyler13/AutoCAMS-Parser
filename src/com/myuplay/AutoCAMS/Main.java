package com.myuplay.AutoCAMS;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.ProgressBar;

@SuppressWarnings("restriction")
public class Main extends Application {

	private static Stage consoleWindow;
	private static final TextArea console = new TextArea();
	private static File directory = new File(System.getProperty("user.dir"));
	private static File output = new File(directory.getPath() + "/results.csv");
	private static Stage window;
	private static final ProgressBar prog = new ProgressBar();
	private static final Text status = new Text("Waiting...");
	
	@Override
	public void start(Stage stage) throws Exception {

		window = stage;

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

		BorderPane in = new BorderPane();

		//Input directory.
		final TextField idir = new TextField();
		idir.setPrefWidth(300);
		idir.setText(directory.getPath());
		idir.setEditable(false);
		Button infile = new Button("Input Dir");
		infile.setPrefWidth(100);
		infile.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent arg0) {
				DirectoryChooser dc = new DirectoryChooser();
				dc.setInitialDirectory(directory);
				dc.setTitle("Input directory");
				File tmp = dc.showDialog(window);
				if (tmp != null){
					directory = tmp;
					idir.setText(directory.getPath());
					Console.log("New input directory: " + directory.getPath());
				} else {
					Console.log("No new directory chosen");
				}
			}
		});

		in.setCenter(idir);
		in.setRight(infile);

		//Output entry.
		BorderPane out = new BorderPane();

		final TextField ofile = new TextField();
		ofile.setPrefWidth(300);
		ofile.setText(output.getPath());
		ofile.setEditable(false);
		Button outfile = new Button("Output file");
		outfile.setPrefWidth(100);
		outfile.setOnAction(new EventHandler<ActionEvent>(){

			public void handle(ActionEvent arg0) {
				FileChooser out = new FileChooser();
				out.setInitialDirectory(directory);
				out.setInitialFileName("results.csv");
				out.setTitle("Output file");
				File tmp = out.showSaveDialog(window);
				if (tmp != null){
					output = tmp;
					ofile.setText(output.getPath());
					Console.log("New output file: " + output.getName());
				} else {
					Console.log("No new output chosen");
				}
			}

		});

		out.setCenter(ofile);
		out.setRight(outfile);

		HBox bottom = new HBox();
		
		Button run = new Button("Run");
		run.setOnAction(new EventHandler<ActionEvent>(){

			public void handle(ActionEvent arg0) {
				if (directory.isDirectory() || directory.isFile()){
					
					FileReader fr = new FileReader(directory, output);
					prog.progressProperty().bind(fr.progressProperty());
					status.textProperty().bind(fr.messageProperty());
					new Thread(fr).start();

				} else {
					Console.warn("Invalid input/output settings. Please reselect your input and output.");
				}
			}

		});

		bottom.getChildren().addAll(run, prog, status);

		//Add elements.
		list.getChildren().addAll(in, out, bottom);

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
