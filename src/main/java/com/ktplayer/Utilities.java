package com.ktplayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javafx.scene.control.Dialog;

public class Utilities {

	private String currentTheme;
	private String currentInterface;

	public Utilities() {
		currentTheme = readProperty("theme");
		currentInterface = readProperty("interface");
	}

	// -------------------------------------------------------------------------
	//    PUBLIC METHODS
	// -------------------------------------------------------------------------	
	
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
	
	public String getCurrentTheme() {
		return currentTheme;
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
