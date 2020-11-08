package com.ktplayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javafx.scene.control.Dialog;

public class Utilities {

	private String currentTheme;

	public Utilities() {
		currentTheme = readCurrentThemeFromProperties();
	}

	// -------------------------------------------------------------------------
	//    PUBLIC METHODS
	// -------------------------------------------------------------------------	
	
	public void applyThemeToDialog(Dialog dialog) {
    	if (currentTheme.equals("Dark"))
    		dialog.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("DarkDialogs.css").toExternalForm());
    	else
    		dialog.getDialogPane().getStylesheets().add(ClassLoader.getSystemResource("LightDialogs.css").toExternalForm());
    }
	
	public String getCurrentTheme() {
		return currentTheme;
	}
	
	// -------------------------------------------------------------------------
	//    PRIVATE METHODS
	// -------------------------------------------------------------------------
	
    private String readCurrentThemeFromProperties() {
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
	
	     // Trying to get the language setting 
	     return appProps.getProperty("theme");
   }
	
}
