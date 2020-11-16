package com.ktplayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
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
	public String returnLanguageToWrite (String language) {
		
		String languageToWrite = null;
		
		switch(language) {
		  case "English":
		    languageToWrite = "English";
		    break;
		  case "Italiano":
			  languageToWrite = "Italian";
		    break;
		  case "Français":
			  languageToWrite = "French";
			    break;
		  case "Español":
			  languageToWrite = "Spanish";
			    break;
		}
		return languageToWrite;
	}
	
	//in properties we have "English", "French", "Spanish"... but we want to show them in their language
	public String returnLanguageToShow (String language) {
		
		String languageToShow = null;
		
		switch(language) {
		  case "English":
			  languageToShow = "English";
		    break;
		  case "Italian":
			  languageToShow = "Italiano";
		    break;
		  case "French":
			  languageToShow = "Français";
			    break;
		  case "Spanish":
			  languageToShow = "Español";
			    break;
		}
		return languageToShow;
	}
	
	//return Dark or Light
	public String returnThemeToWrite (String theme) {
			
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
	  
    	String rootPath = "src\\main\\resources\\";
	     String appConfigPath = rootPath + "application.properties";
	
	     Properties appProps = new Properties();
	     try {
	         appProps.load(new FileInputStream(appConfigPath));
	     }
	     catch (FileNotFoundException e) {
	         e.printStackTrace();
	     } catch (IOException e) {
	         e.printStackTrace();
	     }

	     return appProps.getProperty(property);
   }
	
}
