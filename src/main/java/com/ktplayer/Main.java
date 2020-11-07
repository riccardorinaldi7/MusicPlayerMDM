//Riccardo Rinaldi e Laura Gruppioni
//AttivitÃ  progettuale MDM

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;


public class Main extends Application {

	private ObservableList<Song> songData = FXCollections.observableArrayList();
	static Stage primaryStage;
	private String fxmlName = "ktPlayer.fxml"; //default
	private Properties appProps;
	private String appConfigPath;
	
	public Main() {}

	public void start(Stage primaryStage) throws Exception {
		setStage(primaryStage);

		String rootPath = "src\\main\\resources\\";
		appConfigPath = rootPath + "application.properties";

		appProps = new Properties();
		appProps.load(new FileInputStream(appConfigPath));

		//language selection
		ResourceBundle bundle = handleLanguageSelection();
		
		// Load FXML file
		FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxmlName), bundle);

		Parent root =(Parent) fxmlLoader.load();
		Controller controller = fxmlLoader.getController();
		controller.setMain(this);

		Scene scene = new Scene(root, 820, 740);
		//theme selection
		String nameCssToLoad = handleThemeSelection();
		//scene.getStylesheets().add(ClassLoader.getSystemResource("LightTheme.css").toExternalForm());
		scene.getStylesheets().add(ClassLoader.getSystemResource(nameCssToLoad).toExternalForm());
		scene.setFill(Color.TRANSPARENT);

		primaryStage.setTitle("ktPlayer 0.1v");
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	private ResourceBundle handleLanguageSelection() {
		// Trying to get the language setting
		String language = appProps.getProperty("language");

		// If no language setting exists a dialog box is shown to the user to ask for it
		if(language == null) {

			//----------------------------------------------------------------------------------------------------
			//CHOICE DIALOG PER SCELTA LINGUA
			//----------------------------------------------------------------------------------------------------

			//Creating a choice box asking for the language
			ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>("English");
			choiceDialog.setHeaderText("Select the language you prefer");
			choiceDialog.setTitle("Language");
			Image img = new Image("file:src/main/resources/images/languages.png");
			System.out.println(img.toString());
			choiceDialog.setGraphic(new ImageView(img));

			//Retrieving the observable list
			ObservableList<String> list = choiceDialog.getItems();
			//Adding items to the language list
			list.add("English");
			list.add("Italiano");
			list.add("Français");
			list.add("Español");

			// Show the dialog box and wait for a selection
			Optional<String> selectedLanguage = choiceDialog.showAndWait();
			language = selectedLanguage.get();

			appProps.setProperty("language", language);
			try {
				appProps.store(new FileWriter(appConfigPath), null);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Selected language: " + language);

		// Set the language Bundle according to the selected language
		Locale locale;
		switch (language){
			case "Italiano": locale = Locale.ITALIAN; break;
			case "Français": locale = Locale.FRENCH; break;
			case "Español": locale = new Locale("es", "ES"); break;
			default: locale = Locale.ROOT;
		}
		ResourceBundle bundle = ResourceBundle.getBundle("UIText", locale);
		return bundle;
	}

	private String handleThemeSelection() {
		// Trying to get the theme setting
		String theme = appProps.getProperty("theme");

		// If no theme setting exists a dialog box is shown to the user to ask for it
		if(theme == null) {

			//----------------------------------------------------------------------------------------------------
			//CHOICE DIALOG PER SCELTA TEMA
			//----------------------------------------------------------------------------------------------------

			//Creating a choice box asking for the theme
			ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>();
			choiceDialog.setHeaderText("Select the theme you prefer");
			choiceDialog.setTitle("Theme");
			Image img = new Image("file:src/main/resources/images/theme.png");
			System.out.println(img.toString());
			choiceDialog.setGraphic(new ImageView(img));

			//Retrieving the observable list
			ObservableList<String> list = choiceDialog.getItems();
			//Adding items to the language list
			list.add("Dark");
			list.add("Light");

			// Show the dialog box and wait for a selection
			Optional<String> selectedTheme = choiceDialog.showAndWait();
			theme = selectedTheme.get();

			appProps.setProperty("theme", theme);
			try {
				appProps.store(new FileWriter(appConfigPath), null);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Selected theme: " + theme);

		// Set the theme according to the selected theme
		if (theme.equals("Dark"))
			return "DarkTheme.css";
		else return "LightTheme.css";
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