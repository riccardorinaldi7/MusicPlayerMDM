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
	private String fxmlName_simpleInterface = "ktPlayerSimple.fxml";
	private Properties appProps;
	private String defaultLanguage = "English";
	private String defaultTheme = "Light";
	
	
	public Main() {}

	public void start(Stage primaryStage) throws Exception {
	
		setStage(primaryStage);

		appProps = new Properties();
		appProps.load(ClassLoader.getSystemResourceAsStream("application.properties"));

		//language selection
		ResourceBundle bundle = handleLanguageSelection();
		//theme selection
		String nameCssToLoad = handleThemeSelection();
		
		String interfaceType = handleInterfaceSelection(); //interfaceType = Simple|Advanced
		Parent root = null;
		
		//LOAD XML FILE per INTERFACCIA SEMPLICE
		if(interfaceType.equals("Simple")) {
			
			FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxmlName_simpleInterface), bundle);
			root =(Parent) fxmlLoader.load();
			ControllerSimple controller = fxmlLoader.getController();
			System.out.println("Simple Interface");
			controller.setMain(this);
			
		}
		//LOAD XML FILE per INTERFACCIA AVANZATA
		else if (interfaceType.equals("Advanced" )) {

			FXMLLoader fxmlLoader = new FXMLLoader(ClassLoader.getSystemResource(fxmlName), bundle);
			root =(Parent) fxmlLoader.load();
			Controller controller = fxmlLoader.getController(); 
			System.out.println("Advanced Interface");
			controller.setMain(this);
			
		}

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
	        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));

	        alert.getDialogPane().setStyle("-fx-border-color: #b3b3b3; -fx-border-width: 1.0px;");
	        alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/interfaces.png")));
	        
	        ButtonType simpleButton = new ButtonType("Simple", ButtonData.OK_DONE);
	        ButtonType advancedButton = new ButtonType("Advanced", ButtonData.CANCEL_CLOSE);
	        alert.getButtonTypes().setAll(simpleButton, advancedButton);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == simpleButton) {	
	        	// ... user chose "SIMPLE"
				interfaceType = "Simple";
	        } 
	        else {	
	        	// ... user chose "ADVANCED" 
				interfaceType = "Advanced";
			}
			
			appProps.setProperty("interface", interfaceType);
			try {
				appProps.store(new FileWriter(ClassLoader.getSystemResource("application.properties").toExternalForm()), null);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
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

		// If no language setting exists a dialog box is shown to the user to ask for it
		if(language == null) {
			
			//Creating a choice box asking for the language
			ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>("English");
			choiceDialog.setHeaderText("Select the language you prefer");
			choiceDialog.setTitle("Language");
			Image img = new Image("file:src/main/resources/images/languages.png");
			choiceDialog.setGraphic(new ImageView(img));
			choiceDialog.initStyle(StageStyle.UNDECORATED);
			choiceDialog.getDialogPane().setStyle("-fx-border-color: #b3b3b3; -fx-border-width: 1.0px;");
			
			//Retrieving the observable list
			ObservableList<String> list = choiceDialog.getItems();
			//Adding items to the language list
			list.add("English");
			list.add("Italiano");
			list.add("Français");
			list.add("Español");

			// Show the dialog box and wait for a selection
			Optional<String> selectedLanguage = choiceDialog.showAndWait();

			if (selectedLanguage.isPresent()) {
			    // ok was pressed
				language = selectedLanguage.get();
				appProps.setProperty("language", Utilities.returnLanguageToWrite(language));
				try {
					appProps.store(new FileWriter(ClassLoader.getSystemResource("application.properties").toExternalForm()), null);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			} else {
			    // cancel might have been pressed --> default language = English
				language = defaultLanguage;
				appProps.setProperty("language", Utilities.returnLanguageToWrite(defaultLanguage));
				try {
					appProps.store(new FileWriter(ClassLoader.getSystemResource("application.properties").toExternalForm()), null);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		System.out.println("Selected language: " + language);

		// Set the language Bundle according to the selected language
		Locale locale;
		switch (Utilities.returnLanguageToWrite(language)){
			case "Italian": locale = Locale.ITALIAN; break;
			case "French": locale = Locale.FRENCH; break;
			case "Spanish": locale = new Locale("es", "ES"); break;
			default: locale = Locale.ROOT;
		}
		return ResourceBundle.getBundle("UIText", locale);
	}

	//----------------------------------------------------------------------------------------------------
	// SCELTA THEMA
	//----------------------------------------------------------------------------------------------------
	
	private String handleThemeSelection() {
		// Trying to get the theme setting
		String theme = appProps.getProperty("theme");

		// If no theme setting exists a dialog box is shown to the user to ask for it
		if(theme == null) {

			//----------------------------------------------------------------------------------------------------
			//CHOICE DIALOG PER SCELTA TEMA
			//----------------------------------------------------------------------------------------------------

			//Creating a choice box asking for the theme
			ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>("Light");
			choiceDialog.setHeaderText("Select the theme you prefer");
			choiceDialog.setTitle("Theme");
			Image img = new Image("file:src/main/resources/images/theme.png");
			System.out.println(img.toString());
			choiceDialog.setGraphic(new ImageView(img));
			choiceDialog.initStyle(StageStyle.UNDECORATED);
			choiceDialog.getDialogPane().setStyle("-fx-border-color: #b3b3b3; -fx-border-width: 1.0px;");
			//Retrieving the observable list
			ObservableList<String> list = choiceDialog.getItems();
			//Adding items to the language list
			list.add("Dark");
			list.add("Light");

			// Show the dialog box and wait for a selection
			Optional<String> selectedTheme = choiceDialog.showAndWait();
			
			if (selectedTheme.isPresent()) {
			    // ok was pressed
				theme = selectedTheme.get();

				appProps.setProperty("theme", Utilities.returnThemeToWrite(theme));
				try {
					appProps.store(new FileWriter(ClassLoader.getSystemResource("application.properties").toExternalForm()), null);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			} else {
			    // cancel might have been pressed --> default theme = Light
				theme = defaultTheme;
				appProps.setProperty("theme", Utilities.returnThemeToWrite(theme));
				try {
					appProps.store(new FileWriter(ClassLoader.getSystemResource("application.properties").toExternalForm()), null);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
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