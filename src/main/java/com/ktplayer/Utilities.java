package com.ktplayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class Utilities {

	private String currentTheme;
	private String currentInterface;
	private String currentLanguage;
	
	public Utilities() {
		currentTheme = readProperty("theme");
		currentInterface = readProperty("interface");
		currentLanguage = readProperty("language");
	}

	// -------------------------------------------------------------------------
	//    PUBLIC METHODS
	// -------------------------------------------------------------------------	
	
	//we show languages in their language, but in properties we want "English", "French", "Spanish"
	public static String returnLanguageToWrite (String language) {
		
		String languageToWrite = null;
		
		switch(language) {
		  case "English":
		    languageToWrite = "English";
		    break;
		  case "Italiano":
			  languageToWrite = "Italian";
		    break;
		  case "Fran�ais":
			  languageToWrite = "French";
			    break;
		  case "Espa�ol":
			  languageToWrite = "Spanish";
			    break;
		}
		return languageToWrite;
	}
	
	//in properties we have "English", "French", "Spanish"... but we want to show them in their language
	public static String returnLanguageToShow (String language) {
		
		String languageToShow = null;
		
		switch(language) {
		  case "English":
			  languageToShow = "English";
		    break;
		  case "Italian":
			  languageToShow = "Italiano";
		    break;
		  case "French":
			  languageToShow = "Fran�ais";
			    break;
		  case "Spanish":
			  languageToShow = "Espa�ol";
			    break;
		}
		return languageToShow;
	}
	
	//return Dark or Light
	public static String returnThemeToWrite (String theme) {
			
		if (theme.equalsIgnoreCase("Light") || theme.equalsIgnoreCase("Claro") || theme.equalsIgnoreCase("Clair") || theme.equalsIgnoreCase("Chiaro"))
			return "Light";
		else
			return "Dark";	
	}
	
	public void applyThemeToDialog(Dialog dialog) {
		String cssFilename;
    	if (currentTheme.equals("Dark"))
    		cssFilename = "DarkDialogs.css";
    	else
    		cssFilename = "LightDialogs.css";

    	dialog.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource(cssFilename).toExternalForm());
		if(currentInterface.equals("Simple"))
			dialog.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("big-font.css").toExternalForm());
    }
	
	public void applyThemeToDialogPane(DialogPane dp) {
		String cssFilename;
    	if (currentTheme.equals("Dark"))
    		cssFilename = "DarkDialogs.css";
    	else
    		cssFilename = "LightDialogs.css";

    	dp.getStylesheets().add(ClassLoader.getSystemResource(cssFilename).toExternalForm());
		if(currentInterface.equals("Simple"))
			dp.getStylesheets().add(ClassLoader.getSystemResource("big-font.css").toExternalForm());
    }
	
	public String getCurrentTheme() {
		return currentTheme;
	}
	
	public String getCurrentLanguage() {
		return currentLanguage;
	}
	

	
	// -------------------------------------------------------------------------
	//    PRIVATE METHODS
	// -------------------------------------------------------------------------
	
    private String readProperty(String property) {
	
	     Properties appProps = new Properties();
	     try {
			 Path configPath = Paths.get(System.getProperty("user.home"));
			 Path configFile = configPath.resolve("ktplayer.properties");
			 InputStream configInputStream = Files.newInputStream(configFile);
	         appProps.load(configInputStream);
	     } catch (IOException e) {
	     	System.out.println("Utils.readProperty: error opening the config file");
	     }

		return appProps.getProperty(property);
   }
	
}
