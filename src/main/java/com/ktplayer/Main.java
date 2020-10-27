package com.ktplayer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class Main extends Application {
	
	private ObservableList<Song> songData = FXCollections.observableArrayList();
	static Stage primaryStage;
	private String fxmlName = "ktPlayer.fxml"; //default
	
	public Main() {}
	
	public void start(Stage primaryStage) throws Exception {
		setStage(primaryStage);

		//Creating a choice box asking for the language
		ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>("English");
		//Retrieving the observable list
		ObservableList<String> list = choiceDialog.getItems();
		//Adding items to the language list
		list.add("English");
		list.add("Italian");

		// Show the dialog box and wait for a selection
		Optional<String> language =  choiceDialog.showAndWait();
		System.out.println("Selected language: " + language.get());

		// Set the language Bundle according to the selected language
		ResourceBundle bundle;
		switch (language.get()){
			case "Italian": bundle = ResourceBundle.getBundle("UIText", Locale.ITALIAN); break;
			default: bundle = ResourceBundle.getBundle("UIText", Locale.ROOT);
		}
		
		// Load FXML file
		FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxmlName), bundle);

		Parent root =(Parent) fxmlLoader.load();
		Controller controller = fxmlLoader.getController();
		controller.setMain(this);

		Scene scene = new Scene(root, 820, 740);
		scene.getStylesheets().add(ClassLoader.getSystemResource("LightTheme.css").toExternalForm());
		scene.setFill(Color.TRANSPARENT);

		primaryStage.setTitle("ktPlayer 0.1v");
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setResizable(true);
		primaryStage.show();
		
	}
	
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static Stage getStage() {
		return Main.primaryStage;
	}
	
	private void setStage(Stage stage) {
		Main.primaryStage = stage;
	}
}