//Riccardo Rinaldi e Laura Gruppioni
//AttivitÃ  progettuale MDM

package com.ktplayer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;


public class Main extends Application {

	//private ObservableList<Song> songData = FXCollections.observableArrayList();
	static Stage primaryStage;
	private String fxmlName = "ktPlayer.fxml"; //default
	private String fxmlName_simpleInterface = "ktPlayerSimple.fxml";
	private Properties appProps;
	private String defaultLanguage = "English";
	private String defaultTheme = "Light";
	private Boolean firstConfig;
	
	public Main() {}

	public void start(Stage primaryStage) throws Exception {
	
		setStage(primaryStage);

		loadOrCreatePropertyFile();
		
		if (appProps.isEmpty()) firstConfig=true;
		else firstConfig=false;
		//System.out.println("\n" + firstConfig + "\n");
		
		//language selection
		ResourceBundle bundle = handleLanguageSelection();
		//theme selection
		String nameCssToLoad = handleThemeSelection();
		//interface selection
		String interfaceType = handleInterfaceSelection(); 
		Parent root = null;
		
		//LOAD XML FILE per INTERFACCIA SEMPLICE
		if(interfaceType.equals("Simple")) {

			FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxmlName_simpleInterface), bundle);
			root = fxmlLoader.load();
			ControllerSimple controller = fxmlLoader.getController();
			System.out.println("Main: Loading simple interface...");
			controller.setMain(this);

		}
		//LOAD XML FILE per INTERFACCIA AVANZATA
		else if (interfaceType.equals("Advanced")) {

			FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxmlName), bundle);
			root = fxmlLoader.load();
			Controller controller = fxmlLoader.getController();
			System.out.println("Main: Loading advanced interface...");
			controller.setMain(this);

		} else {
			System.out.println("Main: interface unknown, something bad happened...");
			getStage().close();
		}

		if (firstConfig) askForTutorial(); //alla prima configurazione chiediamo se vogliono vedere il tutorial

		assert root != null;
		Scene scene = new Scene(root, 820, 740);
		//scene.getStylesheets().add(ClassLoader.getSystemResource("LightTheme.css").toExternalForm());
		scene.getStylesheets().add(ClassLoader.getSystemResource(nameCssToLoad).toExternalForm());
		scene.setFill(Color.TRANSPARENT);

		primaryStage.setTitle("ktPlayer 0.1v");
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	private void loadOrCreatePropertyFile() {
		appProps = new Properties();

		Path configPath;
		Path configFile = null;
		InputStream configInputStream;

		try{
			configPath = Paths.get(System.getProperty("user.home"));
			configFile = configPath.resolve("ktplayer.properties");
		} catch (InvalidPathException e){
			System.out.println("loadOrCreatePropertyFile: Cannot find user's home folder.");
			primaryStage.close();
		}

		if(configFile != null) {

			try {
				Files.createFile(configFile);
			} catch (FileAlreadyExistsException e){
				System.out.println("loadOrCreatePropertyFile: file already exists.");
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			try {
				configInputStream = Files.newInputStream(configFile);
				appProps.load(configInputStream);
				System.out.println("loadOrCreatePropertyFile: config file loaded.");
			} catch (IOException e) {
				e.printStackTrace();
				primaryStage.close();
			}
		}
	}

	//only the first time you configure your program
	private void askForTutorial() {
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setHeaderText(null);
		alert.setContentText("Do you want to see a tutorial first?");
		alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
        alert.setTitle("Tutorial");
        //((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));

        alert.getDialogPane().setStyle("-fx-border-color: #b3b3b3; -fx-border-width: 1.0px;");
        alert.setGraphic(new ImageView(new Image(ClassLoader.getSystemResource("images/youtube.png").toExternalForm())));
        
        ButtonType yesButton = new ButtonType("Yes", ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == yesButton) {
        	//Open the browser
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI("https://www.youtube.com/watch?v=ANbDIIsi5Pg&t=3s"));
			} 
			catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		}
		
	}

	//----------------------------------------------------------------------------------------------------
	// SCELTA INTERFACCIA SEMPLICE O AVANZATA
	//----------------------------------------------------------------------------------------------------
	
	private String handleInterfaceSelection() {
		// Trying to get the theme setting
		String interfaceType = appProps.getProperty("interface");

		// If no theme setting exists a dialog box is shown to the user to ask for it
		if(interfaceType == null) {

			//----------------------------------------------------------------------------------------------------
			//CHOICE DIALOG PER SCELTA TEMA
			//----------------------------------------------------------------------------------------------------

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setHeaderText(null);
			alert.setContentText("Please, choose an interface");
			alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
	        alert.setTitle("Interface");
	        //((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));

	        alert.getDialogPane().setStyle("-fx-border-color: #b3b3b3; -fx-border-width: 1.0px;");
	        alert.setGraphic(new ImageView(new Image(ClassLoader.getSystemResource("images/interfaces.png").toExternalForm())));
	        
	        ButtonType simpleButton = new ButtonType("Simple", ButtonData.OK_DONE);
	        ButtonType advancedButton = new ButtonType("Advanced", ButtonData.CANCEL_CLOSE);
	        alert.getButtonTypes().setAll(simpleButton, advancedButton);

			Optional<ButtonType> result = alert.showAndWait();

			if ( result.isPresent() && result.get() == simpleButton) interfaceType = "Simple";
	        else interfaceType = "Advanced";
			
			setAndStoreProperty("interface", interfaceType);
		}
		System.out.println("Selected interface: " + interfaceType);
		return interfaceType;
	}

	//----------------------------------------------------------------------------------------------------
	// SCELTA LINGUA
	//----------------------------------------------------------------------------------------------------
	
	private ResourceBundle handleLanguageSelection() {
		// Trying to get the language setting
		String language = appProps.getProperty("language");
		Locale locale;
		
		// If no language setting exists a dialog box is shown to the user to ask for it
		if(language == null) {

			List<String> languageOptions = Arrays.asList("English", "Italiano", "Français", "Español");
			Optional<String> selectedLanguage = showStringChoicesDialog("Language","Select the language you prefer", "languages.png", languageOptions);

			language = selectedLanguage.orElseGet(() -> defaultLanguage);

			language = Utilities.returnLanguageToWrite(language);

			setAndStoreProperty("language", language);

		}

		System.out.println("Selected language: " + language);

		// Set the language Bundle according to the selected language
		switch (language){
			case "Italian": locale = Locale.ITALIAN; break;
			case "French": locale = Locale.FRENCH; break;
			case "Spanish": locale = new Locale("es", "ES"); break;
			default: locale = Locale.ROOT;
		}
		return ResourceBundle.getBundle("UIText", locale);
	}

	private void setAndStoreProperty(String key, String value) {
		appProps.setProperty(key, value);
		try {
			Path configPath = Paths.get(System.getProperty("user.home"));
			Path configFile = configPath.resolve("ktplayer.properties");
			BufferedWriter writer = Files.newBufferedWriter(configFile);
			appProps.store(writer, null);
		}
		catch (IOException e) {
			System.out.println("setAndStoreProperty: an error occurred writing the property");
		}
	}

	private Optional<String> showStringChoicesDialog(String title, String message, String icon, List<String> options) {
		//Creating a choice box asking for the language
		ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(options.get(0));
		choiceDialog.setHeaderText(message);
		choiceDialog.setTitle(title);
		Image img = new Image(ClassLoader.getSystemResource("images/" + icon).toExternalForm());
		choiceDialog.setGraphic(new ImageView(img));
		choiceDialog.initStyle(StageStyle.UNDECORATED);
		choiceDialog.getDialogPane().setStyle("-fx-border-color: #b3b3b3; -fx-border-width: 1.0px;");

		//add options to items
		choiceDialog.getItems().addAll(options);

		// Show the dialog box and wait for a selection
		return choiceDialog.showAndWait();
	}

	//----------------------------------------------------------------------------------------------------
	// SCELTA THEMA
	//----------------------------------------------------------------------------------------------------
	
	private String handleThemeSelection() {
		// Trying to get the theme setting
		String theme = appProps.getProperty("theme");

		// If no theme setting exists a dialog box is shown to the user to ask for it
		if(theme == null) {

			List<String> themeOptions = Arrays.asList("Light", "Dark");
			// Show the dialog box and wait for a selection
			Optional<String> selectedTheme = showStringChoicesDialog("Theme", "Select the theme you prefer", "theme.png", themeOptions);

			theme = selectedTheme.orElseGet(() -> defaultTheme);

			theme = Utilities.returnThemeToWrite(theme);

			setAndStoreProperty("theme", theme);
		}
		System.out.println("Selected theme: " + theme);
		// Set the theme according to the selected theme
		if (theme.equals("Dark")) return "DarkTheme.css";
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